package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;

/**
 * Extracts decision knowledge elements from {@link CodeComment}s of a
 * {@link ChangedFile}. Stores the source of the element within the source
 * file/comment in its key.
 * 
 * The decision knowledge elements need to be indicated with an @ char, e.g.
 * <b>@decision</b> We decided to...
 */
public class RationaleFromCodeCommentParser {
	private List<KnowledgeElement> elements;
	private final Pattern TAGS_SEARCH_PATTERN;
	private final Pattern TWO_EMPTY_LINES_PATTERNS;
	private final Pattern SPACE_ATCHAR_LETTER_PATTERN;
	private final Pattern NEWLINE_CHAR_PATTERN;
	private final List<String> NEWLINE_WITH_COMMENT_CHAR_PATTERNS;

	public RationaleFromCodeCommentParser() {
		String tagSearch = String.join("|",
				KnowledgeType.toStringList().stream().map(tag -> "@" + tag + "\\:?").collect(Collectors.toList()));
		Set<String> COMMENT_STRINGS = new HashSet<String>(Arrays.asList("\\*", "\\/\\/", "#")); // matches all
																								// characters that may
																								// interrupt multi-line
																								// decision knowledge
																								// elements
		TAGS_SEARCH_PATTERN = Pattern.compile(tagSearch, Pattern.CASE_INSENSITIVE);
		String TWO_EMPTY_LINES_PATTERN_STRING = "\\s*\\n\\s*(";
		NEWLINE_WITH_COMMENT_CHAR_PATTERNS = new ArrayList<String>();
		for (String comment_string : COMMENT_STRINGS) {
			NEWLINE_WITH_COMMENT_CHAR_PATTERNS.add("[^\\S\\n]*\\n[^\\S\\n]*" + comment_string + "*[^\\S\\n]*");
			TWO_EMPTY_LINES_PATTERN_STRING += comment_string + "|";
		}
		TWO_EMPTY_LINES_PATTERN_STRING = TWO_EMPTY_LINES_PATTERN_STRING.substring(0,
				TWO_EMPTY_LINES_PATTERN_STRING.length() - 1) + // remove last "|"
				")*\\s*\\n\\s*\\**\\s*";
		TWO_EMPTY_LINES_PATTERNS = Pattern.compile(TWO_EMPTY_LINES_PATTERN_STRING); // with optional white spaces and
																					// optional comment characters
		SPACE_ATCHAR_LETTER_PATTERN = Pattern.compile("\\s@[a-z]");
		NEWLINE_CHAR_PATTERN = Pattern.compile("\\n");
		this.elements = new ArrayList<>();
	}

	public List<KnowledgeElement> getElements(CodeComment comment) {
		Matcher tagMatcher = TAGS_SEARCH_PATTERN.matcher(comment.getCommentContent());
		int cursorPosition = -1;

		while (tagMatcher.find() && cursorPosition <= tagMatcher.start()) {
			cursorPosition = extractElementAndMoveCursor(comment, tagMatcher);
		}
		return elements;
	}

	private int extractElementAndMoveCursor(CodeComment comment, Matcher tagMatcher) {
		String rationaleTypeTag = tagMatcher.group();
		String rationaleType = getRatTypeFromTag(rationaleTypeTag);
		String rationaleText = comment.getCommentContent().substring(tagMatcher.end());

		int cursorPosition = tagMatcher.end();
		int textEnd = getRationaleTextEndPosition(rationaleText);
		if (textEnd > 0) {
			rationaleText = rationaleText.substring(0, textEnd);
		}
		cursorPosition += rationaleText.length();

		elements.add(addElement(comment, tagMatcher.end(), rationaleText, rationaleType));

		return cursorPosition;
	}

	private KnowledgeElement addElement(CodeComment comment, int start, String rationaleText, String rationaleType) {
		String rationaleTextSanitized = sanitize(rationaleText);
		return new KnowledgeElement(0, getSummary(rationaleTextSanitized), getDescription(rationaleTextSanitized),
				rationaleType.toUpperCase(), "" // unknown, not needed at the moment
				, calculateAndCodeRationalePositionInSourceFile(comment, start, rationaleText),
				DocumentationLocation.CODE, "");
	}

	private String sanitize(String rationaleText) {
		String rationaleTextSanitized = rationaleText;
		for (String pattern : NEWLINE_WITH_COMMENT_CHAR_PATTERNS) {
			rationaleTextSanitized = rationaleTextSanitized.replaceAll(pattern, "\n");
		}
		rationaleTextSanitized = rationaleTextSanitized.replaceAll("\\s*\n\\s*", " ");
		rationaleTextSanitized = rationaleTextSanitized.replaceAll("\\s+", " ");
		return rationaleTextSanitized;
	}

	/**
	 * @issue what information the dec. know. key encapsulate regarding its position
	 *        in a source code file and the rationale itself?
	 *
	 *        KEY := POSITION_TEXTHASH
	 *
	 *        POSITION := lineBegin,columnBegin:lineEnd,columnEnd
	 * @alternative the key must include the start POINT and end POINT in source
	 *              code andthe hash of the rationale text!
	 * @pro with start point(line,column) and end point the order of rationale
	 *      within source file can be easily read.
	 * @pro with start point(line,column) and end point intersections with diff
	 *      entries can be calculated
	 * @con calculating start and end column is complicated
	 * @con end column information is not useful for diff intersections nor
	 *      rationale order calculation.
	 *
	 * @decision the key must include the start LINE, end LINE in source code, tje
	 *           cursor position within comment and the hash of the rationale text!
	 *
	 *           KEY := POSITION_TEXTHASH
	 *
	 *           POSITION := lineBegin:lineEnd:cursorInComment
	 *
	 * @pro start line, end line in source code and the cursor position within
	 *      comment is sufficient to get the order of rationale within the source
	 *      code
	 * @pro with start line, end line and cursor position intersections with diff
	 *      entries can be calculated
	 */
	private String calculateAndCodeRationalePositionInSourceFile(CodeComment comment, int start, String rationaleText) {
		String fullCommentText = comment.getCommentContent();
		// calculate rationale start line in source code
		int absoluteFileStartLine = comment.getBeginLine();
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

		return absoluteFileStartLine + ":" + absoluteFileEndLine + ":" + start;
	}

	/* Either rationale is delimited by two new lines or @ gets observed */
	private int getRationaleTextEndPosition(String rationaleText) {
		int twoLinesPos = -1;
		int spaceAtCharPos = -1;
		Matcher matcher = TWO_EMPTY_LINES_PATTERNS.matcher(rationaleText);
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

	// similar 3 below methods found in
	// extraction/versioncontrol/GitCommitMessageExtractor.java
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
}
