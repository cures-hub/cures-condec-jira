package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * purpose: extract decision knowledge elements from single git
 * commit fullMessage text.
 *
 *
 * Decision knowledge can be documented in commit messages
 * using following syntax:
 * """
 * [decKnowledgeTag]knowledge summary text

 * knowledge description text after empty[/decKnowledgeTag]
 * """
 *
 * where [decKnowledgeTag] belongs to set of know Knowledge Types,
 * for example issue, alternative, decision etc.
 */
public class GitCommitMessageExtractor {

	private final static List<String> decKnowTags = KnowledgeType.toList();
	/**
	 * DecisionKnowledgeElement's key part to be replaced, probably by object
	 * higher in hierarchy than this object, with commit ish.
	 */
	public static final String COMMIT_PLACEHOLDER = "commitish";
	private final Pattern START_TAGS_SEARCH_PATTERN;
	private final Pattern END_TAGS_SEARCH_PATTERN;
	private List<DecisionKnowledgeElement> extractedElements;
	private String parseError;
	private List<String> parseWarnings;
	private String fullMessage;

	GitCommitMessageExtractor(String message) {
		extractedElements = new ArrayList<>();
		parseError = null;
		parseWarnings = new ArrayList<>();
		fullMessage = message;

		String startTagSearch = String.join("|", decKnowTags.stream()
				.map(tag -> "\\[" + tag + "\\]")
				.collect(Collectors.toList()));

		String endTagSearch = String.join("|", decKnowTags.stream()
				.map(tag -> "\\[\\/" + tag + "\\]")
				.collect(Collectors.toList()));

		START_TAGS_SEARCH_PATTERN = Pattern.compile(startTagSearch, Pattern.CASE_INSENSITIVE);
		END_TAGS_SEARCH_PATTERN = Pattern.compile(endTagSearch, Pattern.CASE_INSENSITIVE);

		extract();
	}

	/**
	 * extracts decision knowledge elements one by one
	 * in their order of appearance.
	 */
	private void extract() {
		if (fullMessage == null || fullMessage.trim().equals("")) {
			return;
		}
		if (hasNoDecisionKnowledgeStartTags()) {
			return;
		}
		extractSequences();
	}

	private void extractSequences() {
		Matcher startTagMatcher = START_TAGS_SEARCH_PATTERN.matcher(fullMessage);
		/* parsers position in the message, can only move forward */
		int cursorPosition = 0;

		while (startTagMatcher.find()
				&& cursorPosition <= startTagMatcher.start()) {
			cursorPosition = extractElementAndMoveCursor(startTagMatcher);
		}
		checkOrphanCloseTags(cursorPosition);
	}

	private int extractElementAndMoveCursor(Matcher startTagMatcher) {
		String rationaleTypeStartTag = startTagMatcher.group();
		String rationaleType = getRatTypeFromStartTag(rationaleTypeStartTag);
		String messageRest = fullMessage.substring(startTagMatcher.end());

		int cursorPosition = startTagMatcher.end();
		int textEnd = getEndingTagPosition(messageRest, rationaleTypeStartTag);
		if (textEnd > 0) {
			String rationaleText = messageRest.substring(0, textEnd);
			int textStart = cursorPosition + rationaleTypeStartTag.length();

			cursorPosition += textEnd + getEndingTagForStartTag(rationaleTypeStartTag).length();

			DecisionKnowledgeElement element = createElement(textStart, rationaleType, rationaleText, textEnd);
			extractedElements.add(element);
		} else {
			parseError = rationaleType + " has no end tag";
			cursorPosition = fullMessage.length() - 1; //ends further parsing
		}
		return cursorPosition;
	}

	private String getRatTypeFromStartTag(String rationaleTypeStartTag) {
		return rationaleTypeStartTag.substring(1, rationaleTypeStartTag.length() - 1);
	}

	private DecisionKnowledgeElement createElement(int start, String rationaleType
			, String rationaleText, int end) {
		return new DecisionKnowledgeElementImpl(0
				, getSummary(rationaleText)
				, getDescription(rationaleText)
				, rationaleType.toUpperCase()
				, "" // unknown, not needed at the moment
				, COMMIT_PLACEHOLDER + String.valueOf(start) + ":" + String.valueOf(end)
				, DocumentationLocation.COMMIT);
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

	/* checks the rest of the message for orphan closing tags */
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

	public List<DecisionKnowledgeElement> getElements() {
		return extractedElements;
	}
}
