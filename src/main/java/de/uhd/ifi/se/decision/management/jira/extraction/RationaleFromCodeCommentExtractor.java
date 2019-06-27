package de.uhd.ifi.se.decision.management.jira.extraction;

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
 * purpose: extract decision knowledge elements from single comment.
 * Codes rationale source within the source file/comment.
 */
public class RationaleFromCodeCommentExtractor {
	private ArrayList<DecisionKnowledgeElement> elements;
	private final static List<String> decKnowTags = KnowledgeType.toList();
	private CodeComment comment;
	private final Pattern TAGS_SEARCH_PATTERN;
	private final Pattern TWO_EMPTY_LINES_PATTERN;
	private final Pattern SPACE_ATCHAR_LETTER_PATTERN;
	private final Pattern NEWLINE_CHAR_PATTERN;

	public RationaleFromCodeCommentExtractor(CodeComment comment) {
		String tagSearch = String.join("|", decKnowTags.stream()
				.map(tag -> "@" + tag + "\\:?") //at-char + ratType + colon
				.collect(Collectors.toList()));
		TAGS_SEARCH_PATTERN = Pattern.compile(tagSearch, Pattern.CASE_INSENSITIVE);
		TWO_EMPTY_LINES_PATTERN = Pattern.compile("\\s*\\n\\s*\\n\\s*\\n"); //with optional white spaces
		SPACE_ATCHAR_LETTER_PATTERN = Pattern.compile("\\s@[a-z]");
		NEWLINE_CHAR_PATTERN = Pattern.compile("\\n");
		this.comment = comment;
		this.elements = new ArrayList<>();
	}

	public static int getRationaleStartLineInCode(DecisionKnowledgeElement element) {
		if (!canProcesElement(element)) {
			return -1;
		} else {
			return RationaleCommitElementPositionCodingHelper.getStartLine(element.getKey());
		}
	}

	public static int getRationaleEndLineInCode(DecisionKnowledgeElement element) {
		if (!canProcesElement(element)) {
			return -1;
		} else {
			return RationaleCommitElementPositionCodingHelper.getEndLine(element.getKey());
		}
	}

	public static int getRationaleCursorInCodeComment(DecisionKnowledgeElement element) {
		if (!canProcesElement(element)) {
			return -1;
		} else {
			return RationaleCommitElementPositionCodingHelper.getCursor(element.getKey());
		}
	}

	public static boolean canProcesElement(DecisionKnowledgeElement element) {
		return element.getDocumentationLocation() == DocumentationLocation.COMMIT;
	}

	public ArrayList<DecisionKnowledgeElement> getElements() {
		if (comment.commentContent == null || comment.commentContent.trim().equals("")) {
			return elements;
		}
		Matcher tagMatcher = TAGS_SEARCH_PATTERN.matcher(comment.commentContent);
		int cursorPosition = -1;

		while (tagMatcher.find()
				&& cursorPosition <= tagMatcher.start()) {
			cursorPosition = extractElementAndMoveCursor(tagMatcher);
		}
		return elements;
	}

	private int extractElementAndMoveCursor(Matcher tagMatcher) {
		String rationaleTypeTag = tagMatcher.group();
		String rationaleType = getRatTypeFromTag(rationaleTypeTag);
		String rationaleText = comment.commentContent.substring(tagMatcher.end());

		int cursorPosition = tagMatcher.end();
		int textEnd = getRationaleTextEndPosition(rationaleText);
		if (textEnd > 0) {
			rationaleText = rationaleText.substring(0, textEnd);
		}
		cursorPosition += rationaleText.length();

		elements.add(addElement(tagMatcher.end(), rationaleText, rationaleType));

		return cursorPosition;
	}

	private DecisionKnowledgeElement addElement(int start
			, String rationaleText, String rationaleType) {
		return new DecisionKnowledgeElementImpl(0
				, getSummary(rationaleText)
				, getDescription(rationaleText)
				, rationaleType.toUpperCase()
				, "" // unknown, not needed at the moment
				, calculateAndCodeRationalePositionInSourceFile(start, rationaleText)
				, DocumentationLocation.COMMIT);
	}

	private String calculateAndCodeRationalePositionInSourceFile(int start
			, String rationaleText) {
		/**
		 * @issue: what information the dec. know. key encapsulate
		 * regarding its position in a source code file and the rationale
		 * itself?
		 *
		 * KEY :=
		 * 		POSITION_TEXTHASH
		 *
		 * 		POSITION :=
		 * 		 lineBegin,columnBegin:lineEnd,columnEnd
		 * @alternative: the key must include the start POINT and end POINT in source
		 * code andthe hash of the rationale text!
		 * @pro: with start point(line,column) and end point the order of rationale
		 * within source file can be easily read.
		 * @pro: with start point(line,column) and end point intersections with diff
		 * entries can be calculated
		 * @con: calculating start and end column is complicated
		 * @con: end column information is not useful for diff intersections nor
		 * rationale order calculation.
		 *
		 * @decision: the key must include the start LINE, end LINE in source
		 * code, tje cursor position within comment and the hash of the rationale text!
		 *
		 * KEY :=
		 * 		 POSITION_TEXTHASH
		 *
		 * 		POSITION :=
		 * 		 lineBegin:lineEnd:cursorInComment
		 *
		 * @pro: start line, end line in source code and the cursor position within
		 * comment is sufficient to get the order of rationale within the source code
		 * @pro: with start line, end line and cursor position intersections
		 * with diff entries can be calculated
		 */

		String fullCommentText = comment.commentContent;
		// calculate rationale start line in source code
		int absoluteFileStartLine = comment.beginLine;
		Matcher match = NEWLINE_CHAR_PATTERN.matcher(fullCommentText);
		while (match.find()) {
			if (match.start() < start) {
				absoluteFileStartLine++;
			} else {
				break;
			}
		}

		// calculate rationale end line in source code
		int absoluteFileEndLine = absoluteFileStartLine;
		match = NEWLINE_CHAR_PATTERN.matcher(rationaleText);
		while (match.find()) {
			absoluteFileEndLine++;
		}

		return RationaleCommitElementPositionCodingHelper.encodeAttributes(absoluteFileStartLine
				, absoluteFileEndLine, start);
	}

	/* Either rationale is delimited by two new lines or @ gets observed*/
	private int getRationaleTextEndPosition(String rationaleText) {
		int twoLinesPos = -1;
		int spaceAtCharPos = -1;
		Matcher matcher = TWO_EMPTY_LINES_PATTERN.matcher(rationaleText);
		if (matcher.find()) {
			twoLinesPos = matcher.start();
		}
		matcher = SPACE_ATCHAR_LETTER_PATTERN.matcher(rationaleText);
		if (matcher.find()) {
			spaceAtCharPos = matcher.start();
		}
		// what ever is observed first, take it.
		if (twoLinesPos >= 0 && spaceAtCharPos >= 0)
			return Math.min(twoLinesPos, spaceAtCharPos);
		// found only two line
		if (twoLinesPos >= 0 && spaceAtCharPos < 0)
			return twoLinesPos;
		// found space char
		if (twoLinesPos < 0 && spaceAtCharPos >= 0)
			return spaceAtCharPos;
		// neither found
		return -1;
	}

	private String getRatTypeFromTag(String rationaleTypeStartTag) {
		int atCharPosition = rationaleTypeStartTag.indexOf("@");
		int colonCharPosition = rationaleTypeStartTag.indexOf(":");
		if (colonCharPosition > -1) {
			return rationaleTypeStartTag.substring(atCharPosition + 1, colonCharPosition);
		} else {
			return rationaleTypeStartTag.substring(atCharPosition + 1);
		}
	}

	// similar 3 below methods found in extraction/versioncontrol/GitCommitMessageExtractor.java
	// and possibly Jira text extractor. TODO: use one code portion
	private String getDescription(String rationaleText) {
		return rationaleText.substring(getSummaryEndPosition(rationaleText)).trim();
	}

	private String getSummary(String rationaleText) {
		return rationaleText.substring(0, getSummaryEndPosition(rationaleText)).trim();
	}

	// TODO: implement logic for split between summary and description
	private int getSummaryEndPosition(String rationaleText) {
		return rationaleText.length();
	}

	private static class RationaleCommitElementPositionCodingHelper {
		/*
		 * Keyformat: ...lineStartInt_lineEndInt_CursorInCommetnInt
		 */
		// decode method
		public static int getAttribute(String key, int attributePositionOffsetFromEnd) {
			String[] keyComponents = key.split(":");
			int len = keyComponents.length;
			int returnValue = -1;
			if (len > 2) {
				try {
					returnValue = Integer.valueOf(keyComponents[len
							- (1 + attributePositionOffsetFromEnd)]);
				} catch (NumberFormatException e) {
				}
			}
			return returnValue;
		}

		public static int getCursor(String key) {
			return getAttribute(key, 0);
		}

		public static int getEndLine(String key) {
			return getAttribute(key, 1);
		}

		public static int getStartLine(String key) {
			return getAttribute(key, 2);
		}

		// encode method
		public static String encodeAttributes(int lineStart, int lineEnd
				, int inCommentCursor) {
			return String.valueOf(lineStart) +
					":" +
					String.valueOf(lineEnd) +
					":" +
					String.valueOf(inCommentCursor);
		}
	}
}
