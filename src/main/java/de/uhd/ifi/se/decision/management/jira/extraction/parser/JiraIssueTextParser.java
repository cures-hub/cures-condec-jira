package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import java.text.BreakIterator;
import java.util.ArrayList;
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

	private List<PartOfJiraIssueText> partsOfText;
	private String projectKey;

	public JiraIssueTextParser(String projectKey) {
		this.projectKey = projectKey;
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
		partsOfText = new ArrayList<PartOfJiraIssueText>();

		for (KnowledgeType type : KnowledgeType.macroTypes()) {
			partsOfText.addAll(locateKnowledgeElementsOfType(text, type));
		}

		partsOfText.addAll(locateMacroTextOfType(text, "code"));
		partsOfText.addAll(locateMacroTextOfType(text, "quote"));
		partsOfText.addAll(locateMacroTextOfType(text, "noformat"));

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

	public List<PartOfJiraIssueText> locateKnowledgeElementsOfType(String text, KnowledgeType type) {
		List<PartOfJiraIssueText> partsOfText = locateMacroTextOfType(text, type.name());
		partsOfText.forEach(partOfText -> partOfText.setType(type));
		return partsOfText;
	}

	public List<PartOfJiraIssueText> locateMacroTextOfType(String text, String macro) {
		List<PartOfJiraIssueText> partsOfText = new ArrayList<PartOfJiraIssueText>();
		Pattern pattern = Pattern.compile("\\{" + macro + ":?.*?\\}.*?\\{" + macro + "\\}", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			if (isAlreadyIncludedInOtherSentence(matcher)) {
				continue;
			}

			PartOfJiraIssueText partOfText = new PartOfJiraIssueText(matcher.start(), matcher.end(), text);
			partsOfText.add(partOfText);
		}
		return partsOfText;
	}

	public List<PartOfJiraIssueText> locateRemainingParts(String text) {
		List<PartOfJiraIssueText> newPartsOfText = new ArrayList<PartOfJiraIssueText>();
		for (int i = 0; i < partsOfText.size() - 1; i++) {
			int tempStartPosition = partsOfText.get(i).getEndPosition();
			int tempEndPosition = partsOfText.get(i + 1).getStartPosition();
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
		for (PartOfJiraIssueText partOfText : partsOfText) {
			if (matcher.start() > partOfText.getStartPosition() && matcher.start() < partOfText.getEndPosition()) {
				return true;
			}
			if (matcher.end() > partOfText.getStartPosition() && matcher.end() < partOfText.getEndPosition()) {
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
