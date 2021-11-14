package de.uhd.ifi.se.decision.management.jira.git.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestDecisionKnowledgeElementInCodeComment extends TestSetUp {

	private DecisionKnowledgeElementInCodeComment codeCommentElement;

	@Before
	public void setUp() {
		init();
		codeCommentElement = new DecisionKnowledgeElementInCodeComment();
		codeCommentElement.setSummary("I am an issue");
		codeCommentElement.setType(KnowledgeType.ISSUE);
		codeCommentElement.setProject("TEST");
		codeCommentElement.setStartLine(42);
	}

	@Test
	public void testCodeFile() {
		ChangedFile file = new ChangedFile();
		file.setSummary("File1.java");
		file.setProject("TEST");
		codeCommentElement.setCodeFile(file);
		assertEquals("File1.java", codeCommentElement.getCodeFileName());
	}

	@Test
	public void testStartLine() {
		assertEquals(42, codeCommentElement.getStartLine());
	}

	@Test
	public void testImage() {
		assertTrue(codeCommentElement.getImage().contains("issue.png"));
	}
}
