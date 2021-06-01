package de.uhd.ifi.se.decision.management.jira.git.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Extracts decision knowledge elements from commit messages. Decision knowledge
 * is documented in commit messages using the following syntax:
 * 
 * <b>[decisionKnowledgeTag]</b> knowledge summary text
 * <b>[/decisionKnowledgeTag]</b>
 * 
 * where <b>[decisionKnowledgeTag]</b> belongs to a set of
 * {@link KnowledgeType}s, for example, issue, alternative, decision, pro, and
 * con.
 */
public class RationaleFromCommitMessageParser {

	/**
	 * DecisionKnowledgeElement's key part to be replaced, probably by object higher
	 * in hierarchy than this object, with commit ish.
	 */
	private final Pattern START_TAGS_SEARCH_PATTERN;
	private final Pattern END_TAGS_SEARCH_PATTERN;
	private List<KnowledgeElement> extractedElements;
	private String parseError;
	private List<String> parseWarnings;
	private String fullMessage;

	public RationaleFromCommitMessageParser(String message) {
		extractedElements = new ArrayList<>();
		parseError = null;
		parseWarnings = new ArrayList<>();
		fullMessage = message;

		String startTagSearch = String.join("|", KnowledgeType.toStringList().stream()
				.map(tag -> generateRegexForOpenTag(tag)).collect(Collectors.toList()));

		String endTagSearch = String.join("|", KnowledgeType.toStringList().stream()
				.map(tag -> generateRegexForCloseTag(tag)).collect(Collectors.toList()));

		START_TAGS_SEARCH_PATTERN = Pattern.compile(startTagSearch, Pattern.CASE_INSENSITIVE);
		END_TAGS_SEARCH_PATTERN = Pattern.compile(endTagSearch, Pattern.CASE_INSENSITIVE);

		extract();
	}

	/**
	 * Extracts decision knowledge elements one by one in their order of appearance.
	 */
	private void extract() {
		if (fullMessage == null || fullMessage.trim().equals("")) {
			return;
		}
		if (!hasNoDecisionKnowledgeStartTags()) {
			// we assume that if a user has marked ANY knowledge he has marked all relevant
			// knowledge
			// generateCommentString();
			extractSequences();
		}
	}

	private void extractSequences() {
		Matcher startTagMatcher = START_TAGS_SEARCH_PATTERN.matcher(fullMessage);
		/* parsers position in the message, can only move forward */
		int cursorPosition = 0;

		while (startTagMatcher.find() && cursorPosition <= startTagMatcher.start()) {
			cursorPosition = extractElementAndMoveCursor(startTagMatcher);
		}
		checkOrphanCloseTags(cursorPosition);
	}

	private int extractElementAndMoveCursor(Matcher startTagMatcher) {
		String rationaleTypeStartTag = startTagMatcher.group();
		String rationaleType = getRationaleTypeFromStartTag(rationaleTypeStartTag);
		String messageRest = fullMessage.substring(startTagMatcher.end());

		int cursorPosition = startTagMatcher.end();
		int textEnd = getEndingTagPosition(messageRest, rationaleTypeStartTag);
		if (textEnd > 0) {
			String rationaleText = messageRest.substring(0, textEnd);
			int textStart = cursorPosition + rationaleTypeStartTag.length();

			cursorPosition += textEnd + getEndingTagForStartTag(rationaleTypeStartTag).length();
			// Create new DecisionKnowledgeElement of extracted string
			KnowledgeElement element = createElement(textStart, rationaleType, rationaleText, textEnd);
			// add it to the extracted elements
			extractedElements.add(element);
		} else {
			parseError = rationaleType + " has no end tag";
			cursorPosition = fullMessage.length() - 1; // ends further parsing
		}
		return cursorPosition;
	}

	private String getRationaleTypeFromStartTag(String rationaleTypeStartTag) {
		return rationaleTypeStartTag.substring(1, rationaleTypeStartTag.length() - 1);
	}

	private KnowledgeElement createElement(int start, String rationaleType, String rationaleText, int end) {
		return new KnowledgeElement(0, getSummary(rationaleText), getDescription(rationaleText),
				rationaleType.toUpperCase(), "" // unknown, not needed at the moment
				, start + ":" + end, DocumentationLocation.CODE, "");
	}

	private String getDescription(String rationaleText) {
		return rationaleText.substring(getSummaryEndPosition(rationaleText));
	}

	private String getSummary(String rationaleText) {
		return rationaleText.substring(0, getSummaryEndPosition(rationaleText));
	}

	// TODO: implement logic for split between summary and description
	private int getSummaryEndPosition(String rationaleText) {
		return rationaleText.length();
	}

	/**
	 * Checks the rest of the message for orphan closing tags.
	 */
	private void checkOrphanCloseTags(int cursor) {
		Matcher matcher = END_TAGS_SEARCH_PATTERN.matcher(fullMessage);
		while (matcher.find()) {
			if (cursor <= matcher.start()) {
				parseWarnings.add(matcher.group() + " has no start tag");
			}
		}
	}

	private int getEndingTagPosition(String txt, String startTag) {
		String endTagElement = getEndingTagForStartTag(startTag);
		return txt.toLowerCase().indexOf(endTagElement.toLowerCase());
	}

	private String getEndingTagForStartTag(String startTag) {
		return "[/" + startTag.substring(1);
	}

	private boolean hasNoDecisionKnowledgeStartTags() {
		Matcher matcher = START_TAGS_SEARCH_PATTERN.matcher(fullMessage);
		return !matcher.find();
	}

	public String getParseError() {
		return parseError;
	}

	public List<String> getParseWarnings() {
		return parseWarnings;
	}

	public List<KnowledgeElement> getElements() {
		return extractedElements;
	}

	public static String generateRegexForOpenTag(String tag) {
		return "(?i)(\\[(" + tag + ")\\])";
	}

	public static String generateRegexForCloseTag(String tag) {
		return "(?i)(\\[\\/(" + tag + ")\\])";
	}
}