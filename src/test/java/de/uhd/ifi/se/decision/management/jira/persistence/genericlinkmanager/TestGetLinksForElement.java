package de.uhd.ifi.se.decision.management.jira.persistence.genericlinkmanager;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetLinksForElement extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testElementNull() {
		assertEquals(0, GenericLinkManager.getLinksForElement(0, null).size());
		assertEquals(0, GenericLinkManager.getLinksForElement(null).size());
	}

	@Test
	@NonTransactional
	public void testElementIdFilledDocumentationLocationFilled() {
		KnowledgeElement element = JiraIssues.addElementToDataBase();
		KnowledgeElement elementJiraIssue = new KnowledgeElement(
				JiraIssues.getTestJiraIssues().get(0));
		Link link = new Link(elementJiraIssue, element);
		KnowledgePersistenceManager.getOrCreate("TEST").insertLink(link, null);
		assertEquals(1, GenericLinkManager.getLinksForElement(1, DocumentationLocation.JIRAISSUE).size());
	}

	@Test
	@NonTransactional
	public void testElementFilled() {
		KnowledgeElement element = JiraIssues.addElementToDataBase();
		KnowledgeElement elementJiraIssue = new KnowledgeElement(
				JiraIssues.getTestJiraIssues().get(0));
		Link link = new Link(elementJiraIssue, element);
		KnowledgePersistenceManager.getOrCreate("TEST").insertLink(link, null);
		assertEquals(1, GenericLinkManager.getLinksForElement(element).size());
	}
}