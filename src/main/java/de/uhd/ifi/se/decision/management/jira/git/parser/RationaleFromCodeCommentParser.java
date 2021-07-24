package de.uhd.ifi.se.decision.management.jira.git.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.CodeComment;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Extracts decision knowledge elements from {@link CodeComment}s of a
 * {@link ChangedFile}. Stores the line of the element within the source
 * file/comment in its key.
 * 
 * Decision knowledge is documented in code comments using the following syntax:
 * 
 * <b>{@literal @}decisionKnowledgeTag</b> knowledge summary text
 * <p>
 * where <b>decisionKnowledgeTag</b> belongs to set of {@link KnowledgeType}s,
 * for example issue, alternative, decision, pro, and con. Empty two lines
 * denote the end of the decision knowledge element. The observation of another
 * tag ends the element, too. Comment end ends also the decision knowledge
 * element.
 * <p>
 * e.g. <b>{@literal @}decision</b> We decided to...
 */
public class RationaleFromCodeCommentParser {
	private final Pattern TAGS_SEARCH_PATTERN;
	private final Pattern TWO_EMPTY_LINES_PATTERNS;
	private final Pattern SPACE_ATCHAR_LETTER_PATTERN;
	private final Pattern NEWLINE_CHAR_PATTERN;
	private final List<String> NEWLINE_WITH_COMMENT_CHAR_PATTERNS;

	public RationaleFromCodeCommentParser() {
		String tagSearch = String.join("|",
				KnowledgeType.toStringList().stream().map(tag -> "@" + tag + "\\:?").collect(Collectors.toList()));
		Set<String> COMMENT_STRINGS = new HashSet<String>(Arrays.asList("\\*", "\\/\\/", "#"));
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
		TWO_EMPTY_LINES_PATTERNS = Pattern.compile(TWO_EMPTY_LINES_PATTERN_STRING);
		SPACE_ATCHAR_LETTER_PATTERN = Pattern.compile("\\s@[a-z]");
		NEWLINE_CHAR_PATTERN = Pattern.compile("\\n");
	}

	/**
	 * @param codeFile
	 *            {@link ChangedFile} object, i.e. a code file.
	 * @return all decision knowledge elements within the comments of the given code
	 *         file.
	 */
	public List<KnowledgeElement> getRationaleElementsFromCode(ChangedFile codeFile) {
		List<KnowledgeElement> elementsFromCode = getRationaleElementsFromCodeComments(codeFile.getCodeComments());
		List<KnowledgeElement> knowledgeElements = elementsFromCode.stream().map(element -> {
			element.setProject(codeFile.getProject());
			element.setDescription(codeFile.getName() + ":" + element.getKey());
			element.setDocumentationLocation(DocumentationLocation.CODE);
			element.setCreationDate(codeFile.getCreationDate());
			element.setUpdatingDate(codeFile.getUpdatingDate());
			element.setCreator(codeFile.getCreatorName());
			return element;
		}).collect(Collectors.toList());
		return knowledgeElements;
	}

	/**
	 * @param codeComments
	 *            list of {@link CodeComment}s which contain decision knowledge
	 *            elements.
	 * @return all decision knowledge elements within the comments.
	 */
	public List<KnowledgeElement> getRationaleElementsFromCodeComments(List<CodeComment> codeComments) {
		List<KnowledgeElement> elements = new ArrayList<>();
		for (CodeComment codeComment : codeComments) {
			elements.addAll(getRationaleElementsFromCodeComment(codeComment));
		}
		return elements;
	}

	/**
	 * @param comment
	 *            {@link CodeComment} which contains decision knowledge elements.
	 * @return all decision knowledge elements within the comment.
	 */
	public List<KnowledgeElement> getRationaleElementsFromCodeComment(CodeComment comment) {
		List<KnowledgeElement> elements = new ArrayList<>();
		Matcher tagMatcher = TAGS_SEARCH_PATTERN.matcher(comment.getCommentContent());

		while (tagMatcher.find()) {
			elements.add(parseNextElement(comment, tagMatcher));
		}
		return elements;
	}

	private KnowledgeElement parseNextElement(CodeComment comment, Matcher tagMatcher) {
		String rationaleTypeTag = tagMatcher.group();
		KnowledgeType rationaleType = getRationaleTypeFromTag(rationaleTypeTag);
		String rationaleText = comment.getCommentContent().substring(tagMatcher.end());

		int textEnd = getRationaleTextEndPosition(rationaleText);
		if (textEnd > 0) {
			rationaleText = rationaleText.substring(0, textEnd);
		}

		String rationaleTextSanitized = sanitize(rationaleText);
		KnowledgeElement elementInCodeComment = new KnowledgeElement();
		elementInCodeComment.setSummary(rationaleTextSanitized);
		elementInCodeComment.setType(rationaleType);
		elementInCodeComment.setDocumentationLocation(DocumentationLocation.CODE);
		elementInCodeComment.setKey(calculateStartLineInSourceFile(comment, tagMatcher.end()) + "");

		return elementInCodeComment;
	}

	private String sanitize(String rationaleText) {
		String rationaleTextSanitized = rationaleText;
		for (String pattern : NEWLINE_WITH_COMMENT_CHAR_PATTERNS) {
			rationaleTextSanitized = rationaleTextSanitized.replaceAll(pattern, "\n");
		}
		rationaleTextSanitized = rationaleTextSanitized.replaceAll("[\t\n\r]", " ");
		if (rationaleTextSanitized.endsWith("/")) {
			rationaleTextSanitized = rationaleTextSanitized.substring(0, rationaleTextSanitized.length() - 1);
		}
		return rationaleTextSanitized.trim();
	}

	/**
	 * @issue What information do decision knowledge elements extracted from code
	 *        comments contain regarding their position in a source code file?
	 * @decision the key must include the start line in source code.
	 * @pro start line in source code is sufficient to get the order of rationale
	 *      within the source code.
	 *
	 * @param comment
	 * @param start
	 * @return
	 */
	private int calculateStartLineInSourceFile(CodeComment comment, int start) {
		String fullCommentText = comment.getCommentContent();
		// calculate rationale start line in source code
		int absoluteFileStartLine = comment.getBeginLine();
		Matcher match = NEWLINE_CHAR_PATTERN.matcher(fullCommentText);
		while (match.find() && match.start() < start) {
			absoluteFileStartLine++;
		}

		return absoluteFileStartLine;
	}

	/**
	 * @param rationaleText
	 *            text of the code comment.
	 * @return position where rationale elements ends. A rationale element is
	 *         delimited by two new lines or when {@literal @} gets observed.
	 */
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
		if (twoLinesPos >= 0)
			return twoLinesPos;
		// found space char
		if (spaceAtCharPos >= 0)
			return spaceAtCharPos;
		// neither found
		return -1;
	}

	/**
	 * @param rationaleTypeStartTag
	 *            e.g. {@literal @}decision
	 * @return {@link KnowledgeType} instance for the tag.
	 */
	public static KnowledgeType getRationaleTypeFromTag(String rationaleTypeStartTag) {
		int atCharPosition = rationaleTypeStartTag.indexOf("@");
		int colonCharPosition = rationaleTypeStartTag.indexOf(":");
		String rationaleTypeName = "";
		if (colonCharPosition > -1) {
			rationaleTypeName = rationaleTypeStartTag.substring(atCharPosition + 1, colonCharPosition);
		} else {
			rationaleTypeName = rationaleTypeStartTag.substring(atCharPosition + 1).split(" ")[0];
		}
		return KnowledgeType.getKnowledgeType(rationaleTypeName);
	}
}
