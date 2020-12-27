package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.macros.AbstractKnowledgeClassificationMacro;

/**
 * Splits a text into parts using Jira macro tags and sentences.
 * 
 * @see AbstractKnowledgeClassificationMacro
 * @see KnowledgeType
 * 
 * @issue Is there a parser/scanner library we can use to indentify macros or
 *        icons in text and to split the text into sentences?
 */
public class JiraIssueTextParser {

	/**
	 * Knowledge types that (currently) can be documented in Jira issue description
	 * or comments using {@link AbstractKnowledgeClassificationMacro}s.
	 */
	public static final Set<KnowledgeType> KNOWLEDGE_TYPES = EnumSet.of(KnowledgeType.DECISION, KnowledgeType.ISSUE,
			KnowledgeType.PRO, KnowledgeType.CON, KnowledgeType.ALTERNATIVE);

	private List<Integer> startPositions;
	private List<Integer> endPositions;
	private String projectKey;

	public JiraIssueTextParser(String projectKey) {
		this.projectKey = projectKey;
		this.startPositions = new ArrayList<Integer>();
		this.endPositions = new ArrayList<Integer>();
	}

	/**
	 * @param text
	 *            text to be split, e.g. Jira issue description or a comment body.
	 * @return {@link PartOfJiraIssueText}s (also referred to as sentences or
	 *         substrings) as a list.
	 */
	public List<PartOfJiraIssueText> getPartsOfText(String text) {
		if (text == null || text.isBlank()) {
			return new ArrayList<PartOfJiraIssueText>();
		}
		splitTextIntoSentences(text);

		List<PartOfJiraIssueText> parts = new ArrayList<PartOfJiraIssueText>();
		for (int i = 0; i < startPositions.size(); i++) {
			int startPosition = startPositions.get(i);
			int endPosition = endPositions.get(i);
			if (!startAndEndIndexRules(startPosition, endPosition, text)) {
				continue;
			}
			PartOfJiraIssueText partOfText = new PartOfJiraIssueText();
			partOfText.setStartPosition(startPosition);
			partOfText.setEndPosition(endPosition);
			partOfText.setProject(projectKey);
			String body = text.substring(startPosition, endPosition);
			KnowledgeType type = getKnowledgeTypeFromTag(body);
			partOfText.setType(type);
			if (type != KnowledgeType.OTHER) {
				// TODO: Why is this set here?
				partOfText.setValidated(true);
			}
			partOfText.setDescription(stripTagsFromBody(body));
			parts.add(partOfText);
		}
		return parts;
	}

	public String stripTagsFromBody(String body) {
		if (body == null) {
			return "";
		}
		if (isAnyKnowledgeTypeTwiceExisting(body)) {
			int tagLength = 2 + getKnowledgeTypeFromTag(body).toString().length();
			return body.substring(tagLength, body.length() - tagLength);
		}
		return body.replaceAll("\\(.*?\\)", "");
	}

	private List<String> splitTextIntoSentences(String body) {
		List<String> rawSentences = searchForTagsRecursively(body, "{quote}", "{quote}", new ArrayList<String>());

		rawSentences = searchForTags(rawSentences, "{noformat}", "{noformat}");
		rawSentences = searchForTags(rawSentences, "{noformat}", "{noformat}");
		rawSentences = searchForTags(rawSentences, "{panel:", "{panel}");
		rawSentences = searchForTags(rawSentences, "{code:", "{code}");

		for (KnowledgeType type : KNOWLEDGE_TYPES) {
			rawSentences = searchForTags(rawSentences, type.getTag(), type.getTag());
		}

		rawSentences = runBreakIterator(rawSentences, body);
		return rawSentences;
	}

	private static List<String> searchForTags(List<String> firstSplit, String openTag, String closeTag) {
		Map<Integer, List<String>> newSlices = new HashMap<Integer, List<String>>();
		for (String slice : firstSplit) {
			List<String> slicesOfSentence = searchForTagsRecursively(slice.toLowerCase(), openTag.toLowerCase(),
					closeTag.toLowerCase(), new ArrayList<String>());
			if (slicesOfSentence.size() > 1) {
				newSlices.put(firstSplit.indexOf(slice), slicesOfSentence);
			}
		}
		for (int i = newSlices.keySet().toArray().length - 1; i >= 0; i--) {
			int remove = (int) newSlices.keySet().toArray()[i];
			firstSplit.remove(remove);
			firstSplit.addAll(remove, newSlices.get(remove));
		}

		return firstSplit;
	}

	private static List<String> searchForTagsRecursively(String partOfText, String openTag, String closeTag,
			ArrayList<String> slices) {
		if (isIncorrectlyTagged(partOfText, openTag, closeTag)) {
			slices.add(partOfText);
			return slices;
		}
		// Icon is used to identify a sentence or a closing tag is forgotten
		if (partOfText.contains(openTag) && !partOfText.contains(closeTag)) {
			return slices;
		} // Open and close tags are existent
		if (partOfText.startsWith(openTag) && partOfText.contains(closeTag)) {
			String part = StringUtils.substringBetween(partOfText, openTag, closeTag);
			part = openTag + part + closeTag;
			slices.add(part);
			String commentPartSubstring = partOfText.substring(partOfText.indexOf(openTag) + part.length());
			return searchForTagsRecursively(commentPartSubstring, openTag, closeTag, slices);
		} else {// currently plain text
			if (partOfText.contains(openTag)) {// comment block has special text later
				slices.add(partOfText.substring(0, partOfText.indexOf(openTag)));
				return searchForTagsRecursively(partOfText.substring(partOfText.indexOf(openTag)), openTag, closeTag,
						slices);
			} else {// comment block has no more special text
				slices.add(partOfText);
			}
		}
		return slices;
	}

	/**
	 * Checks: Start Index >=0, End Index >= 0, End Index - Start Index > 0, Body
	 * not only whitespaces
	 *
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	private boolean startAndEndIndexRules(int startIndex, int endIndex, String body) {
		return (startIndex >= 0 && endIndex >= 0 && (endIndex - startIndex) > 0
				&& body.substring(startIndex, endIndex).replaceAll("\r\n", "").trim().length() > 1);
	}

	private List<String> runBreakIterator(List<String> rawSentences, String body) {
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);

		for (String currentSentence : rawSentences) {
			boolean containsAnyRationaleElement = false;
			for (KnowledgeType type : KNOWLEDGE_TYPES) {
				if (currentSentence.contains(type.getTag())) {
					containsAnyRationaleElement = true;
				}
			}
			if (!containsAnyRationaleElement) {
				iterator.setText(currentSentence);
				int start = iterator.first();
				for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
					if (end - start > 1 && currentSentence.substring(start, end).trim().length() > 0) {
						int startOfSentence = body.toLowerCase()
								.indexOf(currentSentence.toLowerCase().substring(start, end));
						int endOfSentence = currentSentence.substring(start, end).length() + startOfSentence;
						this.addSentenceIndex(startOfSentence, endOfSentence);
					}
				}
			} else {
				int start1 = body.toLowerCase().indexOf(currentSentence.toLowerCase());
				int end1 = currentSentence.length() + start1;
				this.addSentenceIndex(start1, end1);
			}
		}
		return rawSentences;
	}

	private static boolean isIncorrectlyTagged(String toSearch, String openTag, String closeTag) {
		return openTag.equals(closeTag) && !knowledgeTypeTagExistsTwice(toSearch, openTag);
	}

	private static boolean knowledgeTypeTagExistsTwice(String body, String knowledgeType) {
		if (body == null || knowledgeType == null) {
			return false;
		}
		return StringUtils.countMatches(body.toLowerCase(), knowledgeType.toLowerCase()) >= 2;
	}

	public void addSentenceIndex(int startIndex, int endIndex) {
		this.startPositions.add(startIndex);
		this.endPositions.add(endIndex);
	}

	/**
	 * @param body
	 * @param projectKey
	 *
	 * @return tagged knowledge type of a given string
	 */
	public KnowledgeType getKnowledgeTypeFromTag(String body) {
		boolean checkIcons = ConfigPersistenceManager.isIconParsing(projectKey);
		for (KnowledgeType type : KNOWLEDGE_TYPES) {
			if (body.toLowerCase().contains(type.getTag()) || checkIcons && body.contains(type.getIconString())) {
				return type;
			}
		}
		return KnowledgeType.OTHER;
	}

	public boolean isAnyKnowledgeTypeTwiceExisting(String body) {
		for (KnowledgeType type : KNOWLEDGE_TYPES) {
			if (knowledgeTypeTagExistsTwice(body, type.getTag())) {
				return true;
			}
		}
		return false;
	}
}
