package de.uhd.ifi.se.decision.management.jira.model.git;

/**
 * Type of {@link ChangedFile} depending on the programming language (e.g. java,
 * cpp, py, ...). Contains the {@link CommentStyleType} and thus necessary
 * information to extract decision knowledge elements in code comments.
 */
public class FileType {

	private String fileEnding;
	private CommentStyleType commentStyleType;

	public FileType(String fileEnding, CommentStyleType commentStyleType) {
		this.fileEnding = fileEnding;
		this.commentStyleType = commentStyleType;
	}

	public String getFileEnding() {
		return fileEnding;
	}

	public CommentStyleType getCommentStyleType() {
		return commentStyleType;
	}

	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (!(object instanceof FileType)) {
			return false;
		}
		FileType otherType = (FileType) object;
		return getFileEnding().equalsIgnoreCase(otherType.getFileEnding());
	}
}