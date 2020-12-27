package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 *         substrings). The list is sorted according the sentence position
	 *         within the text.
	 */
	public List<PartOfJiraIssueText> getPartsOfText(String text) {
		if (text == null || text.isBlank()) {
			return new ArrayList<PartOfJiraIssueText>();
		}
		List<PartOfJiraIssueText> partsOfText = new ArrayList<PartOfJiraIssueText>();

		for (KnowledgeType type : KnowledgeType.macroTypes()) {
			partsOfText.addAll(locateKnowledgeElements(text, type));
		}

		partsOfText.addAll(locateOtherMacros(text, "code"));
		partsOfText.addAll(locateOtherMacros(text, "quote"));
		partsOfText.addAll(locateOtherMacros(text, "noformat"));

		partsOfText.sort(Comparator.comparingInt(PartOfJiraIssueText::getStartPosition));

		if (partsOfText.isEmpty()) {
			partsOfText.addAll(splitIntoSentences(new PartOfJiraIssueText(text)));
		}

		PartOfJiraIssueText firstPart = partsOfText.get(0);
		if (firstPart.getStartPosition() > 0) {
			PartOfJiraIssueText newFirstPart = new PartOfJiraIssueText(0, firstPart.getStartPosition(), text);
			if (!newFirstPart.getDescription().isBlank()) {
				// TODO Split sentences
				partsOfText.add(0, newFirstPart);
			}
		}

		PartOfJiraIssueText lastPart = partsOfText.get(partsOfText.size() - 1);
		if (text.length() > lastPart.getEndPosition()) {
			PartOfJiraIssueText newlastPart = new PartOfJiraIssueText(lastPart.getEndPosition(), text.length(), text);
			if (!newlastPart.getDescription().isBlank()) {
				partsOfText.addAll(splitIntoSentences(newlastPart));
			}
		}

		partsOfText.addAll(locateRemainingParts(text));
		partsOfText.sort(Comparator.comparingInt(PartOfJiraIssueText::getStartPosition));

		partsOfText.forEach(partOfText -> partOfText.setProject(projectKey));

		// TODO This does not seem to be true that every sentence is validated
		partsOfText.forEach(partOfText -> {
			if (partOfText.getType() != KnowledgeType.OTHER)
				partOfText.setValidated(true);
		});
		return partsOfText;
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

	public List<PartOfJiraIssueText> locateKnowledgeElements(String text, KnowledgeType type) {
		List<PartOfJiraIssueText> partsOfText = new ArrayList<PartOfJiraIssueText>();
		String tag = "\\{" + type.toString() + "\\}";
		Pattern pattern = Pattern.compile(tag + ".*?" + tag, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			if (isAlreadyIncludedInOtherSentence(matcher)) {
				continue;
			}
			startPositions.add(matcher.start());
			endPositions.add(matcher.end());

			PartOfJiraIssueText partOfText = new PartOfJiraIssueText(matcher.start(), matcher.end(), text);
			partOfText.setType(type);
			partsOfText.add(partOfText);
		}
		return partsOfText;
	}

	public List<PartOfJiraIssueText> locateOtherMacros(String text, String macro) {
		List<PartOfJiraIssueText> partsOfText = new ArrayList<PartOfJiraIssueText>();
		Pattern pattern = Pattern.compile("\\{" + macro + ":?.*?\\}.*?\\{" + macro + "\\}", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			if (isAlreadyIncludedInOtherSentence(matcher)) {
				continue;
			}
			startPositions.add(matcher.start());
			endPositions.add(matcher.end());

			PartOfJiraIssueText partOfText = new PartOfJiraIssueText(matcher.start(), matcher.end(), text);
			partsOfText.add(partOfText);
		}
		return partsOfText;
	}

	public List<PartOfJiraIssueText> locateRemainingParts(String text) {
		Collections.sort(startPositions);
		Collections.sort(endPositions);
		List<PartOfJiraIssueText> newPartsOfText = new ArrayList<PartOfJiraIssueText>();
		for (int i = 0; i < startPositions.size() - 1; i++) {
			int tempStartPosition = endPositions.get(i);
			int tempEndPosition = startPositions.get(i + 1);
			if (tempStartPosition + 1 >= tempEndPosition) {
				continue;
			}

			PartOfJiraIssueText newPart = new PartOfJiraIssueText(tempStartPosition, tempEndPosition, text);
			if (newPart.getDescription().isBlank()) {
				continue;
			}

			newPartsOfText.addAll(splitIntoSentences(newPart));
		}
		return newPartsOfText;
	}

	private boolean isAlreadyIncludedInOtherSentence(Matcher matcher) {
		for (int i = 0; i < startPositions.size(); i++) {
			if (matcher.start() > startPositions.get(i) && matcher.start() < endPositions.get(i)) {
				return true;
			}
			if (matcher.end() > startPositions.get(i) && matcher.end() < endPositions.get(i)) {
				return true;
			}
		}
		return false;
	}

	private List<PartOfJiraIssueText> splitIntoSentences(PartOfJiraIssueText partOfText) {
		List<PartOfJiraIssueText> sentences = new ArrayList<>();
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		iterator.setText(partOfText.getDescription());
		int start = iterator.first();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			PartOfJiraIssueText sentence = new PartOfJiraIssueText();
			sentence.setStartPosition(partOfText.getStartPosition() + start);
			sentence.setEndPosition(partOfText.getEndPosition());
			sentence.setDescription(partOfText.getDescription().substring(start, end).trim());
			sentences.add(sentence);
		}
		return sentences;
	}

	private static boolean knowledgeTypeTagExistsTwice(String body, String knowledgeType) {
		if (body == null || knowledgeType == null) {
			return false;
		}
		return StringUtils.countMatches(body.toLowerCase(), knowledgeType.toLowerCase()) >= 2;
	}

	/**
	 * @param body
	 * @param projectKey
	 *
	 * @return tagged knowledge type of a given string
	 */
	public KnowledgeType getKnowledgeTypeFromTag(String body) {
		boolean checkIcons = ConfigPersistenceManager.isIconParsing(projectKey);
		for (KnowledgeType type : KnowledgeType.macroTypes()) {
			if (body.toLowerCase().contains(type.getTag()) || checkIcons && body.contains(type.getIconString())) {
				return type;
			}
		}
		return KnowledgeType.OTHER;
	}

	public boolean isAnyKnowledgeTypeTwiceExisting(String body) {
		for (KnowledgeType type : KnowledgeType.macroTypes()) {
			if (knowledgeTypeTagExistsTwice(body, type.getTag())) {
				return true;
			}
		}
		return false;
	}
}
