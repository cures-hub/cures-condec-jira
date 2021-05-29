package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;
import de.uhd.ifi.se.decision.management.jira.model.git.CommentStyleType;

/**
 * Extracts decision knowledge elements from code comments.
 */
public class CodeCommentParser {

	private int beginLine;
	private int lineNumber;
	private String comment;
	private List<CodeComment> codeComments;

	public CodeCommentParser() {
		beginLine = -1;
		lineNumber = 1;
		comment = "";
		codeComments = new ArrayList<CodeComment>();
	}

	public List<CodeComment> getComments(ChangedFile inspectedFile) {
		return getComments(inspectedFile.getFileContent() + "\n", inspectedFile.getCommentStyleType());
	}

	public List<CodeComment> getComments(String entireFileContent, CommentStyleType commentStyleType) {
		String fileContentToParse = entireFileContent;
		boolean inMultilineComment = false;

		String singleLineCommentChar = commentStyleType.getSingleLineCommentChar();
		String multiLineCommentCharStart = commentStyleType.getMultiLineCommentCharStart();
		String multiLineCommentCharEnd = commentStyleType.getMultiLineCommentCharEnd();

		while (!isEndOfFileReached(fileContentToParse)) {
			String line = readFirstLine(fileContentToParse);
			if (inMultilineComment) { // we are in a multi-line comment
				CodeComment multilineComment = parseMultiLineComment(line, comment, multiLineCommentCharEnd);
				comment += line;
				inMultilineComment = !addCommentIfPresent(multilineComment);
			} else { // we are not in a multi-line comment
				int singleLineCommentCharPos = -1;
				if (singleLineCommentChar != null) { // the file type does not support single-line comments
					singleLineCommentCharPos = line.indexOf(singleLineCommentChar);
				}
				int multiLineCommentCharStartPos = -1;
				if (multiLineCommentCharStart != null) { // the file type does not support multi-line comments
					multiLineCommentCharStartPos = line.indexOf(multiLineCommentCharStart);
				}
				if (multiLineCommentCharStartPos != -1) { // a multi-line comment starts in this line
					inMultilineComment = true;
					beginLine = lineNumber;
					if (comment.length() > 0) { // there is a single-line comment to end
						codeComments.add(new CodeComment(comment, beginLine, lineNumber - 1));
						comment = "";
					}
					CodeComment multilineComment = parseMultiLineComment(line, comment, multiLineCommentCharEnd);
					comment += line;
					inMultilineComment = !addCommentIfPresent(multilineComment);
				} else if (singleLineCommentCharPos != -1) { // a single-line comment starts in this line
					if (comment.length() == 0) { // there is no single-line comment present
						beginLine = lineNumber;
					}
					comment += line.substring(singleLineCommentCharPos);
				} else { // there is no comment in this line
					if (comment.length() > 0) { // there is a single-line comment to end
						codeComments.add(new CodeComment(comment, beginLine, lineNumber - 1));
						comment = "";
					}
				}
			}
			fileContentToParse = removeFirstLine(fileContentToParse);
			lineNumber++;
		}
		if (comment.length() > 0) {
			codeComments.add(new CodeComment(comment, beginLine, lineNumber - 1));
		}
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

	private boolean addCommentIfPresent(CodeComment codeComment) {
		if (codeComment != null) {
			comment = "";
			return codeComments.add(codeComment);
		}
		return false;
	}

	private CodeComment parseMultiLineComment(String line, String comment, String multiLineCommentCharEnd) {
		int multiLineCommentCharEndPos = -1;
		if (multiLineCommentCharEnd != null) {
			multiLineCommentCharEndPos = line.indexOf(multiLineCommentCharEnd);
		}
		if (multiLineCommentCharEndPos != -1) { // the multi-line comment ends in this line
			comment += line.substring(0, multiLineCommentCharEndPos + multiLineCommentCharEnd.length());
			return new CodeComment(comment, beginLine, lineNumber);
		}
		return null;
	}
}
