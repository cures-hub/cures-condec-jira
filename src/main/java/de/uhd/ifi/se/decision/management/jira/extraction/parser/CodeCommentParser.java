package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;
import de.uhd.ifi.se.decision.management.jira.model.git.CommentStyleType;

/**
 * Extracts {@link CodeComment}s from code file content. This is necessary to
 * identify decision knowledge elements in code comments using the
 * {@link RationaleFromCodeCommentParser}.
 */
public class CodeCommentParser {

	private int beginLineOfCurrentComment;
	private int lineNumber;
	private String currentCommentText;
	private List<CodeComment> codeComments;

	public CodeCommentParser() {
		beginLineOfCurrentComment = -1;
		lineNumber = 0;
		currentCommentText = "";
		codeComments = new ArrayList<CodeComment>();
	}

	/**
	 * @param inspectedFile
	 *            {@link ChangedFile} which contains {@link CodeComment}s with
	 *            documented decision knowledge.
	 * @return list of identified {@link CodeComment}s.
	 */
	public List<CodeComment> getComments(ChangedFile inspectedFile) {
		return getComments(inspectedFile.getFileContent() + "\n", inspectedFile.getCommentStyleType());
	}

	/**
	 * @param entireFileContent
	 *            textual content of a code file.
	 * @param commentStyleType
	 *            {@link CommentStyleType}, e.g. {@link CommentStyleType#JAVA_C} or
	 *            {@link CommentStyleType#PYTHON} to define the code comment syntax.
	 * @return list of identified {@link CodeComment}s.
	 */
	public List<CodeComment> getComments(String entireFileContent, CommentStyleType commentStyleType) {
		String fileContentToParse = entireFileContent;
		boolean inMultiLineComment = false;

		String singleLineCommentChar = commentStyleType.getSingleLineCommentChar();
		String multiLineCommentCharStart = commentStyleType.getMultiLineCommentCharStart();
		String multiLineCommentCharEnd = commentStyleType.getMultiLineCommentCharEnd();

		while (!isEndOfFileReached(fileContentToParse)) {
			String line = readFirstLine(fileContentToParse);
			if (inMultiLineComment) { // we are in a multi-line comment
				CodeComment multilineComment = parseMultiLineComment(line, currentCommentText, multiLineCommentCharEnd);
				currentCommentText += line;
				inMultiLineComment = !addCommentIfPresent(multilineComment);
			} else { // we are not in a multi-line comment
				int singleLineCommentCharPos = singleLineCommentChar == null ? -1 : line.indexOf(singleLineCommentChar);
				int multiLineCommentCharStartPos = multiLineCommentCharStart == null ? -1
						: line.indexOf(multiLineCommentCharStart);
				if (multiLineCommentCharStartPos != -1
						&& !isCommentInStringsOfTestCase(line, multiLineCommentCharStartPos)) {
					// a multi-line comment starts in this line
					inMultiLineComment = true;
					beginLineOfCurrentComment = lineNumber;
					addCommentIfPresent(createCodeComment(currentCommentText, beginLineOfCurrentComment, lineNumber));
					CodeComment multilineComment = parseMultiLineComment(line, currentCommentText,
							multiLineCommentCharEnd);
					currentCommentText += line;
					inMultiLineComment = !addCommentIfPresent(multilineComment);
				} else if (singleLineCommentCharPos != -1
						&& !isCommentInStringsOfTestCase(line, singleLineCommentCharPos)) {
					// a single-line comment starts in this line
					if (currentCommentText.length() == 0) { // there is no single-line comment present
						beginLineOfCurrentComment = lineNumber;
					}
					currentCommentText += line.substring(singleLineCommentCharPos);
				} else { // there is no comment in this line
					addCommentIfPresent(createCodeComment(currentCommentText, beginLineOfCurrentComment, lineNumber));
				}
			}
			fileContentToParse = removeFirstLine(fileContentToParse);
			lineNumber++;
		}
		addCommentIfPresent(createCodeComment(currentCommentText, beginLineOfCurrentComment, lineNumber));
		return codeComments;
	}

	private String readFirstLine(String fileContent) {
		return fileContent.substring(0, fileContent.indexOf("\n") + 1);
	}

	private String removeFirstLine(String fileContent) {
		return fileContent.substring(fileContent.indexOf("\n") + 1);
	}

	private boolean isEndOfFileReached(String fileContent) {
		return fileContent.indexOf("\n") == -1;
	}

	private CodeComment createCodeComment(String commentText, int beginLine, int endline) {
		if (!commentText.isBlank()) {
			return new CodeComment(commentText, beginLine, endline);
		}
		return null;
	}

	private boolean addCommentIfPresent(CodeComment codeComment) {
		if (codeComment != null) {
			currentCommentText = "";
			return codeComments.add(codeComment);
		}
		return false;
	}

	private boolean isCommentInStringsOfTestCase(String line, int positionOfCommentTag) {
		return positionOfCommentTag > 0 && line.substring(positionOfCommentTag - 1, positionOfCommentTag).matches("\"");
	}

	private CodeComment parseMultiLineComment(String line, String comment, String multiLineCommentCharEnd) {
		int multiLineCommentCharEndPos = line.indexOf(multiLineCommentCharEnd);
		if (multiLineCommentCharEndPos != -1) {
			// the multi-line comment ends in this line
			String commentContent = comment
					+ line.substring(0, multiLineCommentCharEndPos + multiLineCommentCharEnd.length());
			return new CodeComment(commentContent, beginLineOfCurrentComment, lineNumber);
		}
		return null;
	}
}
