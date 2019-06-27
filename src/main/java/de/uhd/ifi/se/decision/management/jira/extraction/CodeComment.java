package de.uhd.ifi.se.decision.management.jira.extraction;

/** purpose: structure to store comment text
 * and area it occupies in a source code file
 *
 * @issue How should this class be called?
 * @decision Simply call this model class "CodeComment"!
 * @pro It is inherent that a CodeComment has a beginning and an end,
 * it does not need to be made explicit in the class' name.
 * @alternative Call this model class "CodeCommentWithRange"!
 * @pro the name suggests the comment is positioned within a range in a file
 * @con the meaning of "WithRange" is not really clear
 */
public class CodeComment {
	public String commentContent = null;
	// position in source code of the file
	public int beginColumn = -1;
	public int beginLine = -1;
	public int endColumn = -1;
	public int endLine = -1;

	public CodeComment(String commentContent, int beginColumn, int beginLine, int endColumn, int endLine) {
		this.commentContent = commentContent;
		this.beginColumn = beginColumn;
		this.beginLine = beginLine;
		this.endColumn = endColumn;
		this.endLine = endLine;
	}
}
