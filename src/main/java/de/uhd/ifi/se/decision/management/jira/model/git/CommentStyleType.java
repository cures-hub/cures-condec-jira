package de.uhd.ifi.se.decision.management.jira.model.git;

public enum CommentStyleType {
	NONE, JAVA_C, PYTHON, HTML;

	public static CommentStyleType getCommentStyleTypeByName(String name) {
		if (name == null || name.isBlank()) {
			return NONE;
		}
		for (CommentStyleType commentStyleType : values()) {
			if (commentStyleType.name().equalsIgnoreCase(name)) {
				return commentStyleType;
			}
		}
		return NONE;
    }

    public String getSingleLineCommentChar() {
        switch (this) {
            case JAVA_C:
                return "//";

            case PYTHON:
                return "#";

            case HTML:
                return null;
        
            default:
                return null;
        }
    }

    public String getMultiLineCommentCharStart() {
        switch (this) {
            case JAVA_C:
                return "/*";

            case PYTHON:
                return null;

            case HTML:
                return "<!--";
        
            default:
                return null;
        }
    }

    public String getMultiLineCommentCharEnd() {
        switch (this) {
            case JAVA_C:
                return "*/";

            case PYTHON:
                return null;

            case HTML:
                return "-->";
        
            default:
                return null;
        }
    }
}
