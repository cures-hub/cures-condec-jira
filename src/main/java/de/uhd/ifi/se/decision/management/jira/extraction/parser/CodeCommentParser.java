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

	private CodeComment createComment(ChangedFile inspectedFile, String comment) {
		return new CodeComment(comment, 0, 0, 0, 0); // TODO implement
	}

	public List<CodeComment> getComments(ChangedFile inspectedFile) {
		if (this.commentList == new ArrayList<CodeComment>()) {
			String fileContent = inspectedFile.getFileContent() + "\n";
			boolean inMultilineComment = false;
			String comment = "";
			int beginColumn = -1;
			int beginLine = -1;
			int lineNumber = 1;

			String singleLineCommentChar = "//"; // TODO change by reading from inspectedFile
			String multiLineCommentCharStart = "/*"; // TODO change by reading from inspectedFile
			String multiLineCommentCharEnd = "*/"; // TODO change by reading from inspectedFile

			while (fileContent.indexOf("\n") != -1) {
				String line = fileContent.substring(0, fileContent.indexOf("\n"));
				if (inMultilineComment) { // we are in a multi-line comment
					int multiLineCommentCharEndPos = line.indexOf(multiLineCommentCharEnd);
					if (multiLineCommentCharEndPos != -1) { // the multi-line comment ends in this line
						comment += line.substring(0, multiLineCommentCharEndPos + multiLineCommentCharEnd.length());
						commentList.add(new CodeComment(comment, beginColumn, beginLine, lineNumber, multiLineCommentCharEndPos + multiLineCommentCharEnd.length()));
						comment = "";
						inMultilineComment = false;
					} else { // the multi-line comment does not end in this line
						comment += line;
					}
				} else { // we are not in a multi-line comment
					int singleLineCommentCharPos = line.indexOf(singleLineCommentChar);
					int multiLineCommentCharStartPos = line.indexOf(multiLineCommentCharStart);
					if (multiLineCommentCharStartPos != -1) { // a multi-line comment starts in this line
						inMultilineComment = true;
						beginLine = lineNumber;
						beginColumn = multiLineCommentCharStartPos;
						if (comment.length() > 0) { // there is a single-line comment to end
							commentList.add(new CodeComment(comment + "\n", beginColumn, beginLine, lineNumber, 0));
						}
						int multiLineCommentCharEndPos = line.indexOf(multiLineCommentCharEnd);
						if (multiLineCommentCharEndPos == -1) { // the multi-line comment ends in this line
							comment = line.substring(multiLineCommentCharStartPos);
						} else { // the multi-line comment does not end in this line
							comment = line.substring(multiLineCommentCharStartPos, multiLineCommentCharEndPos + multiLineCommentCharEnd.length());
							commentList.add(new CodeComment(comment, beginColumn, beginLine, lineNumber, multiLineCommentCharEndPos + multiLineCommentCharEnd.length()));
							comment = "";
							inMultilineComment = false;
						}
					} else if (singleLineCommentCharPos != -1) { // a single-line comment starts in this line
						comment += line.substring(singleLineCommentCharPos);
						if (comment.length() == 0) { // there is no single-line comment present
							beginLine = lineNumber;
							beginColumn = singleLineCommentCharPos;
						}
					} else { // there is no comment in this line
						if (comment.length() > 0) { // there is a single-line comment to end
							commentList.add(new CodeComment(comment + "\n", beginColumn, beginLine, lineNumber, 0));
							comment = "";
						}
					}
				}
				fileContent = fileContent.substring(fileContent.indexOf("\n") + 1);
				lineNumber++;
			}
			if (comment.length() > 0) {
				commentList.add(createComment(inspectedFile, comment));
			}
		}
		return this.commentList;
	}
}
