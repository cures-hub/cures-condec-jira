package de.uhd.ifi.se.decision.management.jira.git.model;

/**
 * Model class for comment text and the area it occupies in a source code file.
 *
 * @issue How should this class be called?
 * @decision Simply call this model class "CodeComment"!
 * @pro It is inherent that a CodeComment has a beginning and an end, it does
 *      not need to be made explicit in the class' name.
 * @alternative Call this model class "CodeCommentWithRange"!
 * @pro The name suggests the comment is positioned within a range in a file.
 * @con The meaning of "WithRange" is not really clear.
 */
public class CodeComment {
	private String commentContent;
	private int beginLine = -1;
	private int endLine = -1;

	public CodeComment(String commentContent, int beginLine, int endLine) {
		this.commentContent = commentContent.trim();
		this.beginLine = beginLine;
		this.endLine = endLine;
	}

	/**
	 * @return textual content of the code comment.
	 */
	public String getCommentContent() {
		return commentContent;
	}

	/**
	 * @param commentContent
	 *            textual content of the code comment.
	 */
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	/**
	 * @return starting position in file.
	 */
	public int getBeginLine() {
		return beginLine;
	}

	/**
	 * @return end position in file.
	 */
	public int getEndLine() {
		return endLine;
	}
}
