package de.uhd.ifi.se.decision.management.jira.model.git;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public enum CommentStyleType {

    NONE(new ArrayList<String>()), 
    JAVA_C(Arrays.asList(
            "java",
            "cpp",
            "c++",
            "c",
            "hpp",
            "h++",
            "h")), 
    PYTHON(Arrays.asList(
            "py",
            "sh")), 
    HTML(Arrays.asList(
            "xml",
            "js",
            "vm",
            "html",
            "htm",
            "css",
            "php"));

    private List<String> fileEndings;

    private CommentStyleType(List<String> fileEndings) {
        this.fileEndings = fileEndings;
    }

    public static CommentStyleType getCommentStyleTypeByFileName(String fileName) {
        return getCommentStyleTypeByFileEnding(fileName.substring(fileName.lastIndexOf(".") + 1));
    }

    public static CommentStyleType getCommentStyleTypeByFileEnding(String fileEnding) {
        for (CommentStyleType commentStyleType : CommentStyleType.values()) {
            if (commentStyleType.fileEndings.contains(fileEnding.toLowerCase())) {
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
