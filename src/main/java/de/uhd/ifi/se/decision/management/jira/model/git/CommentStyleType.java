package de.uhd.ifi.se.decision.management.jira.model.git;

public enum CommentStyleType {

    NONE(null, null, null), 
    JAVA_C("//", "/*", "*/"), 
    PYTHON("#", null, null), 
    HTML(null, "<!--", "-->"),
    TEX("%", null, null);

    private String singleLineCommentChar;
    private String multiLineCommentCharStart;
    private String multiLineCommentCharEnd;

    public static CommentStyleType getFromString(String commentStyleTypeString) {
        switch (commentStyleTypeString.toUpperCase()) {
            case "JAVA_C":
                return CommentStyleType.JAVA_C;

            case "PYTHON":
                return CommentStyleType.PYTHON;

            case "HTML":
                return CommentStyleType.HTML;

            case "TEX":
                return CommentStyleType.TEX;

            default:
                return CommentStyleType.NONE;
        }
    }

    private CommentStyleType(String singleLineCommentChar, String multiLineCommentCharStart, String multiLineCommentCharEnd) {
        this.singleLineCommentChar = singleLineCommentChar;
        this.multiLineCommentCharStart = multiLineCommentCharStart;
        this.multiLineCommentCharEnd = multiLineCommentCharEnd;
    }

    public String getSingleLineCommentChar() {
        return this.singleLineCommentChar;
    }

    public String getMultiLineCommentCharStart() {
        return this.multiLineCommentCharStart;
    }

    public String getMultiLineCommentCharEnd() {
        return this.multiLineCommentCharEnd;
    }
}
