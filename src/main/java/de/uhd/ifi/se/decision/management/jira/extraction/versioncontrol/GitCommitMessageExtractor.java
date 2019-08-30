package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;

/**
 * Extracts decision knowledge elements from the message of a single git commit.
 *
 * 
 * Decision knowledge can be documented in commit messages using the following
 * syntax:
 * <p>
 * <b>[decisionKnowledgeTag]</b>knowledge summary
 * text<b>[/decisionKnowledgeTag]</b>
 * <p>
 *
 * where [decisionKnowledgeTag] belongs to a set of {@link KnowledgeType}s, for
 * example, issue, alternative, decision, pro, and con.
 */
public class GitCommitMessageExtractor {

	private final static List<String> decKnowTags = KnowledgeType.toList();
	/**
	 * DecisionKnowledgeElement's key part to be replaced, probably by object higher
	 * in hierarchy than this object, with commit ish.
	 */
	public static final String COMMIT_PLACEHOLDER = "commitish";
	private final Pattern START_TAGS_SEARCH_PATTERN;
	private final Pattern END_TAGS_SEARCH_PATTERN;
	private List<DecisionKnowledgeElement> extractedElements;
	private String parseError;
	private List<String> parseWarnings;
	private String fullMessage;
	private DecisionKnowledgeClassifier decisionKnowledgeClassifier;

	GitCommitMessageExtractor(String message) {
		extractedElements = new ArrayList<>();
		parseError = null;
		parseWarnings = new ArrayList<>();
		fullMessage = message;
		decisionKnowledgeClassifier = new DecisionKnowledgeClassifierImpl();

		String startTagSearch = String.join("|",
				decKnowTags.stream().map(tag -> "\\[" + tag + "\\]").collect(Collectors.toList()));

		String endTagSearch = String.join("|",
				decKnowTags.stream().map(tag -> "\\[\\/" + tag + "\\]").collect(Collectors.toList()));

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
		if (hasNoDecisionKnowledgeStartTags()) {
			//we assume that if a user has marked ANY knowledge he has marked all relevant knowledge
			classifyMessage();
		}
		extractSequences();
	}

	private void classifyMessage() {
		//@pdesombre TODO: If no Decision Knowledge was manually annotated -> Classifier should be called
		String delimiters = "(?<=[\\r\\n\\t\\.,;:'\"\\(\\)\\?!])+";
		Pattern pattern = Pattern.compile(delimiters);
		Matcher matcher = pattern.matcher(fullMessage);
		//Split message in its sentences for classification.
		List<String> messageToBeClassified = new ArrayList<>();//Arrays.asList(fullMessage.split(delimiters));
		List<Integer> startPositions = new ArrayList<>();
		List<Integer> endPositions = new ArrayList<>();

		while (matcher.find()) {
			startPositions.add(matcher.start());
			endPositions.add(matcher.end());
			messageToBeClassified.add(matcher.group());
		}
		// Binary classification: isRelevant or not
		List<Boolean> isRelevantPredictions = decisionKnowledgeClassifier.makeBinaryPredictions(messageToBeClassified);

		List<String> isRelevantMessages = new ArrayList<>();
		for (int i = 0; i < messageToBeClassified.size(); i++){
			if(isRelevantPredictions.get(i)){
				isRelevantMessages.add(messageToBeClassified.get(i));
			}
		}
		//fine grained classification of relevant messages
		List<KnowledgeType> fineGrainedPredictions = decisionKnowledgeClassifier.makeFineGrainedPredictions(isRelevantMessages);

		for (int i = 0; i < messageToBeClassified.size(); i++){
			if(isRelevantPredictions.get(i)){
				// Add new extracted element for each relevant knowledge part.
				extractedElements.add(createElement(
						startPositions.get(i),
						fineGrainedPredictions.get(i),
						messageToBeClassified.get(i),
						endPositions.get(i)
				));
			}
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
		String rationaleType = getRatTypeFromStartTag(rationaleTypeStartTag);
		String messageRest = fullMessage.substring(startTagMatcher.end());

		int cursorPosition = startTagMatcher.end();
		int textEnd = getEndingTagPosition(messageRest, rationaleTypeStartTag);
		if (textEnd > 0) {
			String rationaleText = messageRest.substring(0, textEnd);
			int textStart = cursorPosition + rationaleTypeStartTag.length();

			cursorPosition += textEnd + getEndingTagForStartTag(rationaleTypeStartTag).length();
			//Create new DecisionKnowledgeElement of extracted string
			DecisionKnowledgeElement element = createElement(textStart, rationaleType, rationaleText, textEnd);
			// add it to the extracted elements
			extractedElements.add(element);
		} else {
			parseError = rationaleType + " has no end tag";
			cursorPosition = fullMessage.length() - 1; // ends further parsing
		}
		return cursorPosition;
	}

	private String getRatTypeFromStartTag(String rationaleTypeStartTag) {
		return rationaleTypeStartTag.substring(1, rationaleTypeStartTag.length() - 1);
	}

	private DecisionKnowledgeElement createElement(int start, String rationaleType, String rationaleText, int end) {
		return new DecisionKnowledgeElementImpl(0, getSummary(rationaleText), getDescription(rationaleText),
				rationaleType.toUpperCase(), "" // unknown, not needed at the moment
				, COMMIT_PLACEHOLDER + String.valueOf(start) + ":" + String.valueOf(end), DocumentationLocation.COMMIT);
	}

	private DecisionKnowledgeElement createElement(int start, KnowledgeType rationaleType,
												   String rationaleText, int end){
		// id is set to a useful value in GitDecExtract.getElementsFromMessage!
		return new DecisionKnowledgeElementImpl(0
				, getSummary(rationaleText)
				, getDescription(rationaleText)
				, rationaleType
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

	/**
	 *  Checks the rest of the message for orphan closing tags.
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

	public List<DecisionKnowledgeElement> getElements() {
		return extractedElements;
	}
}
