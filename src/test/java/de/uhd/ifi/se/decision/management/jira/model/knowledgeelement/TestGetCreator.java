package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestGetCreator extends TestSetUp {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getTestKnowledgeElement();
	}

	@Test
	public void testGetCreatorOfJiraIssue() {
		assertEquals(JiraUsers.SYS_ADMIN.getApplicationUser(), element.getCreator());
	}

	@Test
	public void testGetCreatorOfJiraIssueComment() {
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("TEST");
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
		assertNull(element.getCreator());
	}

	@Test
	public void testGetCreatorOfCodeElement() {
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("TEST");
		element.setDocumentationLocation(DocumentationLocation.CODE);
		assertNull(element.getCreator());
	}

	@Test
	public void testGetCreatorNameWithFilledUpdateAndAuthorMap() {
		assertFalse(element.getUpdateDateAndAuthor().isEmpty());
		assertEquals("", element.getCreatorName());
	}

	@Test
	public void testGetCreatorNameWithEmptyUpdateAndAuthorMap() {
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("TEST");
		assertTrue(element.getUpdateDateAndAuthor().isEmpty());
		assertEquals("", element.getCreatorName());
	}

}
