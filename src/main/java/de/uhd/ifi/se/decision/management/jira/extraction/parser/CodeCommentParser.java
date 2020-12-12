package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;

/**
 * Extracts decision knowledge elements from code comments.
 */
public class CodeCommentParser {
	private List<CodeComment> commentList = new ArrayList<CodeComment>();

	public List<CodeComment> getComments(ChangedFile inspectedFile) {
		if (this.commentList.size() == 0) {
			String fileContent = inspectedFile.getFileContent() + "\n";
			boolean inMultilineComment = false;
			String comment = "";
			int beginColumn = -1;
			int beginLine = -1;
			int lineNumber = 1;
			int lastLineCol = -1;

			String singleLineCommentChar = inspectedFile.getCommentStyleType().getSingleLineCommentChar();
			String multiLineCommentCharStart = inspectedFile.getCommentStyleType().getMultiLineCommentCharStart();
			String multiLineCommentCharEnd = inspectedFile.getCommentStyleType().getMultiLineCommentCharEnd();

			while (fileContent.indexOf("\n") != -1) {
				String line = fileContent.substring(0, fileContent.indexOf("\n") + 1);
				if (inMultilineComment) { // we are in a multi-line comment
					int multiLineCommentCharEndPos = -1;
					if (multiLineCommentCharEnd != null) {
						multiLineCommentCharEndPos = line.indexOf(multiLineCommentCharEnd);
					}
					if (multiLineCommentCharEndPos != -1) { // the multi-line comment ends in this line
						comment += line.substring(0, multiLineCommentCharEndPos + multiLineCommentCharEnd.length());
						commentList.add(new CodeComment(comment, beginColumn, beginLine, multiLineCommentCharEndPos + multiLineCommentCharEnd.length() + 1, lineNumber));
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
							commentList.add(new CodeComment(comment.substring(0, comment.length() - 1), beginColumn, beginLine, lastLineCol, lineNumber - 1));
							comment = "";
						}
						int multiLineCommentCharEndPos = -1;
						if (multiLineCommentCharEnd != null) { // the file type does not support multi-line comments
							multiLineCommentCharEndPos = line.indexOf(multiLineCommentCharEnd);
						}
						if (multiLineCommentCharEndPos == -1) { // the multi-line comment does not end in this line
							comment = line.substring(multiLineCommentCharStartPos);
						} else { // the multi-line comment ends in this line
							comment = line.substring(multiLineCommentCharStartPos, multiLineCommentCharEndPos + multiLineCommentCharEnd.length());
							commentList.add(new CodeComment(comment, beginColumn, beginLine, multiLineCommentCharEndPos + multiLineCommentCharEnd.length() + 1, lineNumber));
							comment = "";
							inMultilineComment = false;
						}
					} else if (singleLineCommentCharPos != -1) { // a single-line comment starts in this line
						if (comment.length() == 0) { // there is no single-line comment present
							beginLine = lineNumber;
							beginColumn = singleLineCommentCharPos + 1;
						}
						comment += line.substring(singleLineCommentCharPos);
					} else { // there is no comment in this line
						if (comment.length() > 0) { // there is a single-line comment to end
							commentList.add(new CodeComment(comment.substring(0, comment.length() - 1), beginColumn, beginLine, lastLineCol, lineNumber - 1));
							comment = "";
						}
					}
				}
				fileContent = fileContent.substring(fileContent.indexOf("\n") + 1);
				lineNumber++;
				lastLineCol = line.length();
			}
			if (comment.length() > 0) {
				commentList.add(new CodeComment(comment.substring(0, comment.length() - 1), beginColumn, beginLine, lastLineCol, lineNumber - 1));
			}
		}
		return this.commentList;
	}
}
