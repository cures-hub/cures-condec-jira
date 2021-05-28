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

	private int beginColumn;
	private int beginLine;
	private int lineNumber;
	private int lastLineCol;
	private String comment;

	public CodeCommentParser() {
		beginColumn = -1;
		beginLine = -1;
		lineNumber = 1;
		lastLineCol = -1;
		comment = "";
	}

	public List<CodeComment> getComments(ChangedFile inspectedFile) {
		return getComments(inspectedFile.getFileContent() + "\n", inspectedFile.getCommentStyleType());
	}

	public List<CodeComment> getComments(String entireEileContent, CommentStyleType commentStyleType) {
		String fileContent = entireEileContent;
		List<CodeComment> commentList = new ArrayList<CodeComment>();
		boolean inMultilineComment = false;

		String singleLineCommentChar = commentStyleType.getSingleLineCommentChar();
		String multiLineCommentCharStart = commentStyleType.getMultiLineCommentCharStart();
		String multiLineCommentCharEnd = commentStyleType.getMultiLineCommentCharEnd();

		while (fileContent.indexOf("\n") != -1) {
			String line = fileContent.substring(0, fileContent.indexOf("\n") + 1);
			if (inMultilineComment) { // we are in a multi-line comment
				CodeComment multilineComment = parseMultiLineComment(line, comment, multiLineCommentCharEnd);
				if (multilineComment != null) {
					commentList.add(multilineComment);
					comment = "";
					inMultilineComment = false;
				} else { // the multi-line comment does not end in this line
					comment += line;
				}
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
					beginColumn = multiLineCommentCharStartPos + 1;
					if (comment.length() > 0) { // there is a single-line comment to end
						commentList.add(new CodeComment(comment.substring(0, comment.length() - 1), beginColumn,
								beginLine, lastLineCol, lineNumber - 1));
						comment = "";
					}
					CodeComment multilineComment = parseMultiLineComment(line, comment, multiLineCommentCharEnd);
					if (multilineComment != null) {
						commentList.add(multilineComment);
						comment = "";
						inMultilineComment = false;
					} else { // the multi-line comment does not end in this line
						comment += line;
					}
				} else if (singleLineCommentCharPos != -1) { // a single-line comment starts in this line
					if (comment.length() == 0) { // there is no single-line comment present
						beginLine = lineNumber;
						beginColumn = singleLineCommentCharPos + 1;
					}
					comment += line.substring(singleLineCommentCharPos);
				} else { // there is no comment in this line
					if (comment.length() > 0) { // there is a single-line comment to end
						commentList.add(new CodeComment(comment.substring(0, comment.length() - 1), beginColumn,
								beginLine, lastLineCol, lineNumber - 1));
						comment = "";
					}
				}
			}
			fileContent = fileContent.substring(fileContent.indexOf("\n") + 1);
			lineNumber++;
			lastLineCol = line.length();
		}
		if (comment.length() > 0) {
			commentList.add(new CodeComment(comment.substring(0, comment.length() - 1), beginColumn, beginLine,
					lastLineCol, lineNumber - 1));
		}
		return commentList;
	}
	//
	// public List<CodeComment> parseMultiline(String entireEileContent,
	// CommentStyleType commentStyleType) {
	// String fileContent = entireEileContent;
	// List<CodeComment> commentList = new ArrayList<CodeComment>();
	// boolean inMultilineComment = false;
	//
	// String singleLineCommentChar = commentStyleType.getSingleLineCommentChar();
	// String multiLineCommentCharStart =
	// commentStyleType.getMultiLineCommentCharStart();
	// String multiLineCommentCharEnd =
	// commentStyleType.getMultiLineCommentCharEnd();
	//
	// while (fileContent.indexOf("\n") != -1) {
	// String line = fileContent.substring(0, fileContent.indexOf("\n") + 1);
	// if (inMultilineComment) { // we are in a multi-line comment
	// CodeComment multilineComment = parseMultiLineComment(line, comment,
	// multiLineCommentCharEnd);
	// if (multilineComment != null) {
	// commentList.add(multilineComment);
	// comment = "";
	// inMultilineComment = false;
	// } else { // the multi-line comment does not end in this line
	// comment += line;
	// }
	// } else { // we are not in a multi-line comment
	// int multiLineCommentCharStartPos = -1;
	// if (multiLineCommentCharStart != null) { // the file type does not support
	// multi-line comments
	// multiLineCommentCharStartPos = line.indexOf(multiLineCommentCharStart);
	// }
	// if (multiLineCommentCharStartPos != -1) { // a multi-line comment starts in
	// this line
	// inMultilineComment = true;
	// beginLine = lineNumber;
	// beginColumn = multiLineCommentCharStartPos + 1;
	// if (comment.length() > 0) { // there is a single-line comment to end
	// commentList.add(new CodeComment(comment.substring(0, comment.length() - 1),
	// beginColumn,
	// beginLine, lastLineCol, lineNumber - 1));
	// comment = "";
	// }
	// CodeComment multilineComment = parseMultiLineComment(line, comment,
	// multiLineCommentCharEnd);
	// if (multilineComment != null) {
	// commentList.add(multilineComment);
	// comment = "";
	// inMultilineComment = false;
	// } else { // the multi-line comment does not end in this line
	// comment += line;
	// }
	// }
	// }
	// fileContent = fileContent.substring(fileContent.indexOf("\n") + 1);
	// lineNumber++;
	// lastLineCol = line.length();
	// }
	// if (comment.length() > 0) {
	// commentList.add(new CodeComment(comment.substring(0, comment.length() - 1),
	// beginColumn, beginLine,
	// lastLineCol, lineNumber - 1));
	// }
	// return commentList;
	// }

	private CodeComment parseMultiLineComment(String line, String comment, String multiLineCommentCharEnd) {
		int multiLineCommentCharEndPos = -1;
		if (multiLineCommentCharEnd != null) {
			multiLineCommentCharEndPos = line.indexOf(multiLineCommentCharEnd);
		}
		if (multiLineCommentCharEndPos != -1) { // the multi-line comment ends in this line
			comment += line.substring(0, multiLineCommentCharEndPos + multiLineCommentCharEnd.length());
			return new CodeComment(comment, beginColumn, beginLine,
					multiLineCommentCharEndPos + multiLineCommentCharEnd.length() + 1, lineNumber);
		}
		return null;
	}
	//
	// private CodeComment parseSingleLineComment(String line, String
	// singleLineCommentChar) {
	// int singleLineCommentCharPos = -1;
	// if (singleLineCommentChar != null) { // the file type does not support
	// single-line comments
	// singleLineCommentCharPos = line.indexOf(singleLineCommentChar);
	// }
	// if (singleLineCommentCharPos != -1) { // a single-line comment starts in this
	// line
	// if (comment.length() == 0) { // there is no single-line comment present
	// beginLine = lineNumber;
	// beginColumn = singleLineCommentCharPos + 1;
	// }
	// comment += line.substring(singleLineCommentCharPos);
	// } else { // there is no comment in this line
	// if (comment.length() > 0) { // there is a single-line comment to end
	// return new CodeComment(comment.substring(0, comment.length() - 1),
	// beginColumn, beginLine, lastLineCol,
	// lineNumber - 1);
	// }
	// }
	// return null;
	// }

}
