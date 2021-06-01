package de.uhd.ifi.se.decision.management.jira.git.model;

/**
 * Necessary to identify decision knowledge in code comments for different
 * programming languages. Is part of the {@link FileType}.
 */
public enum CommentStyleType {

	JAVA_C("//", "/*", "*/"), // example: java, c, h, cpp, hpp
	PYTHON("#", null, null), // example: py, sh, R
	HTML(null, "<!--", "-->"), // example: html, htm, xml, css, php, vm, js
	TEX("%", null, null), // example: tex
	UNKNOWN(null, null, null);

	private String singleLineCommentChar;
	private String multiLineCommentCharStart;
	private String multiLineCommentCharEnd;

	public static CommentStyleType getFromString(String commentStyleTypeString) {
		for (CommentStyleType type : CommentStyleType.values()) {
			if (type.toString().equalsIgnoreCase(commentStyleTypeString)) {
				return type;
			}
		}
		return UNKNOWN;
	}

	private CommentStyleType(String singleLineCommentChar, String multiLineCommentCharStart,
			String multiLineCommentCharEnd) {
		this.singleLineCommentChar = singleLineCommentChar;
		this.multiLineCommentCharStart = multiLineCommentCharStart;
		this.multiLineCommentCharEnd = multiLineCommentCharEnd;
	}

	public String getSingleLineCommentChar() {
		return singleLineCommentChar;
	}

	public String getMultiLineCommentCharStart() {
		return multiLineCommentCharStart;
	}

	public String getMultiLineCommentCharEnd() {
		return multiLineCommentCharEnd;
	}
}
