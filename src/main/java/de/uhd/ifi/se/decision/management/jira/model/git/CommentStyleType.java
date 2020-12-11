package de.uhd.ifi.se.decision.management.jira.model.git;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public enum CommentStyleType {

    NONE(new ArrayList<String>(), null, null, null), 
    JAVA_C(Arrays.asList(
            "java",
            "cpp",
            "c++",
            "c",
            "hpp",
            "h++",
            "h"),
            "//", "/*", "*/"), 
    PYTHON(Arrays.asList(
            "py",
            "sh"),
            "#", null, null), 
    HTML(Arrays.asList(
            "xml",
            "js",
            "vm",
            "html",
            "htm",
            "css",
            "php"),
            null, "<!--", "-->"),
    TEX(Arrays.asList(
            "tex"),
            "%", null, null);

    private List<String> fileEndings;
    private String singleLineCommentChar;
    private String multiLineCommentCharStart;
    private String multiLineCommentCharEnd;

    private CommentStyleType(List<String> fileEndings, String singleLineCommentChar, String multiLineCommentCharStart, String multiLineCommentCharEnd) {
        this.fileEndings = fileEndings;
        this.singleLineCommentChar = singleLineCommentChar;
        this.multiLineCommentCharStart = multiLineCommentCharStart;
        this.multiLineCommentCharEnd = multiLineCommentCharEnd;
    
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
        return this.singleLineCommentChar;
    }

    public String getMultiLineCommentCharStart() {
        return this.multiLineCommentCharStart;
    }

    public String getMultiLineCommentCharEnd() {
        return this.multiLineCommentCharEnd;
    }
}
