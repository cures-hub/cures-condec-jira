package de.uhd.ifi.se.decision.management.jira.model.git;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class TestCommentStyleType {
    @Test
	public void testGetFromString() {
        assertEquals(CommentStyleType.JAVA_C, CommentStyleType.getFromString("JAVA_C"));
        assertEquals(CommentStyleType.PYTHON, CommentStyleType.getFromString("python"));
        assertEquals(CommentStyleType.HTML, CommentStyleType.getFromString("HTML"));
        assertEquals(CommentStyleType.TEX, CommentStyleType.getFromString("TeX"));
        assertEquals(CommentStyleType.NONE, CommentStyleType.getFromString("cheesecake"));
    }
}
