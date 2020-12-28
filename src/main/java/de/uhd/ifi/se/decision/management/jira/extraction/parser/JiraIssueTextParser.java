package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.view.macros.AbstractKnowledgeClassificationMacro;

/**
 * Splits a text into parts using Jira macro tags and sentences.
 * 
 * @see AbstractKnowledgeClassificationMacro
 * @see KnowledgeType
 * 
 * @issue Is there a parser/scanner library we can use to indentify macros or
 *        icons in text and to split the text into sentences?
 * @decision We write our own parser to identify 1) parts of text with tagged
 *           decision knowledge elements, 2) parts of text tagged with other
 *           macros (e.g. code), and 3) to split the remaining text into
 *           sentences.
 * @pro There seems to be no Jira issue macro parser that we could built on, so
 *      we have to write one ourselves.
 */
public class JiraIssueTextParser {

	private List<PartOfJiraIssueText> partsOfText;
	private String projectKey;

	private static final String[] JIRA_MACROS = { "code", "quote", "noformat", "color", "panel" };

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
			partsOfText.addAll(findKnowledgeElementsOfType(text, type));
		}

		for (String jiraMacro : JIRA_MACROS) {
			partsOfText.addAll(findMacroTextOfType(text, jiraMacro));
		}

		if (partsOfText.isEmpty()) {
			partsOfText.addAll(splitIntoSentences(new PartOfJiraIssueText(text)));
		}
		partsOfText.sort(Comparator.comparingInt(PartOfJiraIssueText::getStartPosition));
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

	public List<PartOfJiraIssueText> findKnowledgeElementsOfType(String text, KnowledgeType type) {
		List<PartOfJiraIssueText> partsOfText = findMacroTextOfType(text, type.name());
		partsOfText.forEach(partOfText -> partOfText.setType(type));
		return partsOfText;
	}

	public List<PartOfJiraIssueText> findMacroTextOfType(String text, String macro) {
		List<PartOfJiraIssueText> partsOfText = new ArrayList<PartOfJiraIssueText>();
		Pattern pattern = Pattern.compile("\\{" + macro + ":?.*?\\}.*?\\{" + macro + "\\}",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
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

		// Find sentences at the beginning
		PartOfJiraIssueText firstPart = partsOfText.get(0);
		if (firstPart.getStartPosition() > 0) {
			PartOfJiraIssueText newFirstPart = new PartOfJiraIssueText(0, firstPart.getStartPosition(), text);
			if (!newFirstPart.getDescription().isBlank()) {
				newPartsOfText.addAll(splitIntoSentences(newFirstPart));
			}
		}

		// Find sentences in between
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

		// Find sentences at the end
		PartOfJiraIssueText lastPart = partsOfText.get(partsOfText.size() - 1);
		if (text.length() > lastPart.getEndPosition()) {
			PartOfJiraIssueText newlastPart = new PartOfJiraIssueText(lastPart.getEndPosition(), text.length(), text);
			if (!newlastPart.getDescription().isBlank()) {
				newPartsOfText.addAll(splitIntoSentences(newlastPart));
			}
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

	/**
	 * @issue How to split a text into sentences?
	 * @decision Use the java.text.BreakIterator to split a text into sentences.
	 * @con Only allows Locale.US currently.
	 * @alternative We could use some more advanced NLP technique to split a text
	 *              into sentences.
	 * 
	 * @param partOfText
	 *            to be split into sentences.
	 * @return list of sentences. If no splitting was done, the original partOfText
	 *         is returned in the list.
	 */
	private List<PartOfJiraIssueText> splitIntoSentences(PartOfJiraIssueText partOfText) {
		List<PartOfJiraIssueText> sentences = new ArrayList<>();
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		iterator.setText(partOfText.getDescription());
		int start = iterator.first();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			PartOfJiraIssueText sentence = new PartOfJiraIssueText();
			sentence.setStartPosition(partOfText.getStartPosition() + start);
			sentence.setEndPosition(partOfText.getStartPosition() + end);
			sentence.setDescription(partOfText.getDescription().substring(start, end).trim());
			sentences.add(sentence);
		}
		return sentences;
	}
}
