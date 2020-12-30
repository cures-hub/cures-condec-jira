package de.uhd.ifi.se.decision.management.jira.persistence.genericlinkmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

public class TestDeleteLinksForElements extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testElementNull() {
		assertFalse(GenericLinkManager.deleteLinksForElement(0, null));
	}

	@Test
	@NonTransactional
	public void testElementFilled() {
		KnowledgeElement element = JiraIssues.addElementToDataBase();
		KnowledgeElement elementJiraIssue = new KnowledgeElement(
				JiraIssues.getTestJiraIssues().get(0));
		Link link = new Link(elementJiraIssue, element);
		KnowledgePersistenceManager.getOrCreate("TEST").insertLink(link, null);
		assertTrue(GenericLinkManager.deleteLinksForElement(1, DocumentationLocation.JIRAISSUE));
	}
}
