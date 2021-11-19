package de.uhd.ifi.se.decision.management.jira.git.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestCodeComment {

	private CodeComment codeComment;

	@Before
	public void setUp() {
		codeComment = new CodeComment("", 23, 42);
	}

	@Test
	public void testStartLine() {
		assertEquals(23, codeComment.getBeginLine());
	}

	@Test
	public void testEndLine() {
		assertEquals(42, codeComment.getEndLine());
	}

	@Test
	public void testCommentContent() {
		codeComment.setCommentContent("/** @decision */");
		assertEquals("/** @decision */", codeComment.getCommentContent());
	}
}
