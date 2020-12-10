package de.uhd.ifi.se.decision.management.jira.model.git;

import java.util.HashSet;
import java.util.Arrays;

public enum CommentStyleType {

    NONE(new HashSet<String>()), 
    JAVA_C(new HashSet<String>(Arrays.asList(
            "java",
            "cpp",
            "c++",
            "c",
            "hpp",
            "h++",
            "h"))), 
    PYTHON(new HashSet<>(Arrays.asList(
            "py",
            "sh"))), 
    HTML(new HashSet<>(Arrays.asList(
            "xml",
            "js",
            "vm",
            "html",
            "htm",
            "css",
            "php")));

    private HashSet<String> fileEndings;

    private CommentStyleType(HashSet<String> fileEndings) {
        this.fileEndings = fileEndings;
    }

    public static CommentStyleType getCommentStyleTypeByFileEnding(String fileEnding) {
        for (CommentStyleType commentStyleType : CommentStyleType.values()) {
            if (commentStyleType.fileEndings.contains(fileEnding)) {
                return commentStyleType;
            }
        }
        return CommentStyleType.NONE;
    }

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
