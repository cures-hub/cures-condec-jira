package de.uhd.ifi.se.decision.management.jira.model.text.impl;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfText;
import de.uhd.ifi.se.decision.management.jira.model.text.TextSplitter;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.macros.AbstractKnowledgeClassificationMacro;

public class TextSplitterImpl implements TextSplitter {

	private List<Integer> startPositions;
	private List<Integer> endPositions;

	public TextSplitterImpl() {
		this.startPositions = new ArrayList<Integer>();
		this.endPositions = new ArrayList<Integer>();
	}

	@Override
	public List<PartOfText> getPartsOfText(String text, String projectKey) {
		List<PartOfText> parts = new ArrayList<PartOfText>();

		List<String> strings = TextSplitterImpl.getRawSentences(text, projectKey);
		runBreakIterator(strings, text);

		for (int i = 0; i < this.startPositions.size(); i++) {
			int startPosition = this.startPositions.get(i);
			int endPosition = this.endPositions.get(i);
			if (!startAndEndIndexRules(startPosition, endPosition, text)) {
				continue;
			}
			PartOfText partOfText = new PartOfTextImpl();
			partOfText.setEndPosition(endPosition);
			partOfText.setStartPosition(startPosition);
			partOfText.setProject(projectKey);
			String body = text.substring(startPosition, endPosition).toLowerCase();
			KnowledgeType type = getKnowledgeTypeFromTag(body, projectKey);
			partOfText.setType(type);
			if (type != KnowledgeType.OTHER) {
				partOfText.setRelevant(true);
				partOfText.setValidated(true);
			}
			parts.add(partOfText);
		}
		return parts;
	}

	private static List<String> getRawSentences(String body, String projectKey) {
		List<String> firstSplit = searchForTagsRecursively(body, "{quote}", "{quote}", new ArrayList<String>());

		firstSplit = searchForTags(firstSplit, "{noformat}", "{noformat}");
		firstSplit = searchForTags(firstSplit, "{panel:", "{panel}");
		firstSplit = searchForTags(firstSplit, "{code:", "{code}");
		for (String tag : RATIONALE_TAGS) {
			firstSplit = searchForTags(firstSplit, tag, tag);
		}
		if (ConfigPersistenceManager.isIconParsing(projectKey)) {
			for (String icon : RATIONALE_ICONS) {
				firstSplit = searchForTags(firstSplit, icon, System.getProperty("line.separator"));
			}
		}
		return firstSplit;
	}

	private static ArrayList<String> searchForTagsRecursively(String partOfText, String openTag, String closeTag,
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

	private static List<String> searchForTags(List<String> firstSplit, String openTag, String closeTag) {
		HashMap<Integer, ArrayList<String>> newSlices = new HashMap<Integer, ArrayList<String>>();
		for (String slice : firstSplit) {
			ArrayList<String> slicesOfSentence = searchForTagsRecursively(slice.toLowerCase(), openTag.toLowerCase(),
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

	private void runBreakIterator(List<String> rawSentences, String body) {
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);

		for (String currentSentence : rawSentences) {
			if (StringUtils.indexOfAny(currentSentence, TextSplitter.EXCLUDED_STRINGS) == -1) {
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
	 * 
	 * @param body
	 * @param projectKey
	 * @param lookOutForIcons
	 *            search also for icons
	 * @return tagged knowledge type of a given string
	 */
	public static KnowledgeType getKnowledgeTypeFromTag(String body, String projectKey) {
		boolean checkIcons = ConfigPersistenceManager.isIconParsing(projectKey);
		for (KnowledgeType type : KNOWLEDGE_TYPES) {
			if (body.toLowerCase().contains(AbstractKnowledgeClassificationMacro.getTag(type))
					|| (checkIcons && body.contains(type.getIconString()))) {
				return type;
			}
		}
		return KnowledgeType.OTHER;
	}

	public static boolean isAnyKnowledgeTypeTwiceExisting(String body, String projectKey) {
		Set<String> knowledgeTypeTags = getAllTagsUsedInProject(projectKey);
		for (String tag : knowledgeTypeTags) {
			if (knowledgeTypeTagExistsTwice(body, tag)) {
				return true;
			}
		}
		return false;
	}

	public static Set<String> getAllTagsUsedInProject(String projectKey) {
		Set<KnowledgeType> projectKnowledgeTypes = new DecisionKnowledgeProjectImpl(projectKey).getKnowledgeTypes();
		projectKnowledgeTypes.add(KnowledgeType.PRO);
		projectKnowledgeTypes.add(KnowledgeType.CON);
		Set<String> knowledgeTypeTags = new HashSet<String>();
		for (KnowledgeType type : projectKnowledgeTypes) {
			knowledgeTypeTags.add(AbstractKnowledgeClassificationMacro.getTag(type));
		}
		return knowledgeTypeTags;
	}

	public static boolean isCommentIconTagged(String text) {
		// TODO WHY >=
		return StringUtils.indexOfAny(text, TextSplitter.RATIONALE_ICONS) >= 0;
	}
}
