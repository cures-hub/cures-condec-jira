package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestGetUrl extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testJiraIssue() {
		KnowledgeElement jiraIssue = KnowledgeElements.getTestKnowledgeElement();
		// Jira issue with task Jira issue type
		// null because Jira base URL is not mocked
		assertEquals("null/browse/TEST-1", jiraIssue.getUrl());
	}

	@Test
	public void testPartOfJiraIssueText() {
		KnowledgeElement partOfJiraIssueText = new KnowledgeElement();
		partOfJiraIssueText.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
		partOfJiraIssueText.setKey("TEST-42:42");
		// null because Jira base URL is not mocked
		assertEquals("null/browse/TEST-42", partOfJiraIssueText.getUrl());
	}

	@Test
	public void testCode() {
		KnowledgeElement elementInCodeComment = new KnowledgeElement();
		elementInCodeComment.setKey("TEST:graph:-42");
		elementInCodeComment.setDocumentationLocation(DocumentationLocation.CODE);
		elementInCodeComment.setProject("TEST");
		elementInCodeComment.setType("Decision");
		// null because Jira base URL is not mocked
		assertEquals("null/projects/TEST?selectedItem=decision-knowledge-page&type=Decision",
				elementInCodeComment.getUrl());
	}
}