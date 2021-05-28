package de.uhd.ifi.se.decision.management.jira.model.git;

/**
 * Necessary to identify decision knowledge in code comments for different
 * programming languages.
 */
public enum CommentStyleType {

	NONE(null, null, null), //
	JAVA_C("//", "/*", "*/"), // example: java, c, h, cpp, hpp
	PYTHON("#", null, null), // example: py, sh, R
	HTML(null, "<!--", "-->"), // example: html, htm, xml, css, php, vm, js
	TEX("%", null, null); // example: tex

	private String singleLineCommentChar;
	private String multiLineCommentCharStart;
	private String multiLineCommentCharEnd;

	public static CommentStyleType getFromString(String commentStyleTypeString) {
		for (CommentStyleType type : CommentStyleType.values()) {
			if (type.toString().equalsIgnoreCase(commentStyleTypeString)) {
				return type;
			}
		}
		return NONE;
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
