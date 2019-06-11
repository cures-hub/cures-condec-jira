package de.uhd.ifi.se.decision.management.jira.extraction;

/** purpose: structure to store comment text
 * and area it occupies in a source code file
 */
public class CodeCommentWithRange {
	public String commentContent = null;
	// position in source code of the file
	public int beginColumn = -1;
	public int beginLine = -1;
	public int endColumn = -1;
	public int endLine = -1;

	public CodeCommentWithRange(String commentContent, int beginColumn, int beginLine, int endColumn, int endLine) {
		this.commentContent = commentContent;
		this.beginColumn = beginColumn;
		this.beginLine = beginLine;
		this.endColumn = endColumn;
		this.endLine = endLine;
	}
}
