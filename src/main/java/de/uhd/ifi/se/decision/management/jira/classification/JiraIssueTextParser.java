package de.uhd.ifi.se.decision.management.jira.classification;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

	private String text;
	private List<PartOfJiraIssueText> partsOfText;
	private String projectKey;

	private static final String[] JIRA_MACROS = { "code", "quote", "noformat", "color", "panel", "codesummarization" };

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
		this.partsOfText = new ArrayList<PartOfJiraIssueText>();
		this.text = text;

		// 1) identify parts of text with tagged decision knowledge elements
		for (KnowledgeType type : KnowledgeType.macroTypes()) {
			partsOfText.addAll(findKnowledgeElementsOfType(type));
		}

		// 2) identify parts of text with other Jira macros, e.g. the code macro
		for (String jiraMacro : JIRA_MACROS) {
			partsOfText.addAll(findMacroTextOfType(jiraMacro));
		}

		// 3) split the remaining text into sentences for automatic text classification
		if (partsOfText.isEmpty()) {
			partsOfText.addAll(splitIntoSentences(new PartOfJiraIssueText(text)));
		}
		partsOfText.sort(Comparator.comparingInt(PartOfJiraIssueText::getStartPosition));
		partsOfText.addAll(findRemainingParts());
		partsOfText.sort(Comparator.comparingInt(PartOfJiraIssueText::getStartPosition));

		partsOfText.forEach(partOfText -> partOfText.setProject(projectKey));
		return partsOfText;
	}

	/**
	 * @param type
	 *            {@link KnowledgeType} that can be documented in Jira issue
	 *            descriptions or comments, see subclasses of
	 *            {@link AbstractKnowledgeClassificationMacro}.
	 * @return {@link PartOfJiraIssueText}s (also referred to as sentences or
	 *         substrings) that include the macro, e.g. {issue}How to?{issue}.
	 */
	private List<PartOfJiraIssueText> findKnowledgeElementsOfType(KnowledgeType type) {
		List<PartOfJiraIssueText> partsOfText = findMacroTextOfType(type.name());
		partsOfText.forEach(partOfText -> {
			partOfText.setType(type);
			partOfText.setValidated(true);
			partOfText.setRelevant(true);
		});
		return partsOfText;
	}

	/**
	 * @param macro
	 *            tag, i.e. decision knowledge tags or Jira macros such as code,
	 *            quote, noformat. The tag is provided without parenthesis. Also,
	 *            further information such as "code:java" must not be provided.
	 * @return {@link PartOfJiraIssueText}s (also referred to as sentences or
	 *         substrings) that include the macro.
	 */
	private List<PartOfJiraIssueText> findMacroTextOfType(String macro) {
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
	 * @return sentences before, in between, and after any macros.
	 */
	private List<PartOfJiraIssueText> findRemainingParts() {
		List<PartOfJiraIssueText> newPartsOfText = new ArrayList<PartOfJiraIssueText>();
		newPartsOfText.addAll(findPartsAtTheBeginning());
		newPartsOfText.addAll(findPartsInBetween());
		newPartsOfText.addAll(findPartsAtTheEnd());
		return newPartsOfText;
	}

	/**
	 * @return sentences at the beginning of the text, in front of any macros.
	 */
	private List<PartOfJiraIssueText> findPartsAtTheBeginning() {
		PartOfJiraIssueText newFirstPart = new PartOfJiraIssueText(0, partsOfText.get(0).getStartPosition(), text);
		if (newFirstPart.getDescription().isBlank()) {
			return new ArrayList<PartOfJiraIssueText>();
		}
		return splitIntoSentences(newFirstPart);
	}

	/**
	 * @return sentences in between macros.
	 */
	private List<PartOfJiraIssueText> findPartsInBetween() {
		List<PartOfJiraIssueText> newPartsOfText = new ArrayList<PartOfJiraIssueText>();
		for (int i = 0; i < partsOfText.size() - 1; i++) {
			int tempStartPosition = partsOfText.get(i).getEndPosition();
			int tempEndPosition = partsOfText.get(i + 1).getStartPosition();
			if (tempStartPosition + 1 >= tempEndPosition) {
				continue;
			}

			PartOfJiraIssueText newPart = new PartOfJiraIssueText(tempStartPosition, tempEndPosition, text);
			if (!newPart.getDescription().isBlank()) {
				newPartsOfText.addAll(splitIntoSentences(newPart));
			}
		}
		return newPartsOfText;
	}

	/**
	 * @return sentences at the end of the text, after any macros.
	 */
	private List<PartOfJiraIssueText> findPartsAtTheEnd() {
		PartOfJiraIssueText lastPart = partsOfText.get(partsOfText.size() - 1);
		PartOfJiraIssueText newLastPart = new PartOfJiraIssueText(lastPart.getEndPosition(), text.length(), text);
		if (newLastPart.getDescription().isBlank()) {
			return new ArrayList<PartOfJiraIssueText>();
		}
		return splitIntoSentences(newLastPart);
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
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.getDefault());

		// the description might be already trimmed, thus, we use the entire text
		iterator.setText(text.substring(partOfText.getStartPosition(), partOfText.getEndPosition()));
		int start = iterator.first();
		int end = start;
		while ((end = iterator.next()) != BreakIterator.DONE) {
			int sentenceStartPosition = partOfText.getStartPosition() + start;
			int sentenceEndPosition = partOfText.getStartPosition() + end;
			String sentenceText = text.substring(sentenceStartPosition, sentenceEndPosition);
			Stream<String> lines = sentenceText.lines();
			lines.forEachOrdered(line -> {
				int lineStartPosition = sentenceStartPosition + sentenceText.indexOf(line);
				int lineEndPosition = lineStartPosition + line.length();
				if (!line.isBlank() && line.length() > 4) {
					PartOfJiraIssueText sentence = new PartOfJiraIssueText(lineStartPosition, lineEndPosition, text);
					sentences.add(sentence);
				}
			});

			start = end;
		}
		return sentences;
	}
}
