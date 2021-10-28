package de.uhd.ifi.se.decision.management.jira.persistence.genericlinkmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteInvalidLinks extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testAllLinksValid() {
		KnowledgeElement element = JiraIssues.addElementToDataBase();
		KnowledgeElement elementJiraIssue = new KnowledgeElement(
				JiraIssues.getTestJiraIssues().get(0));
		Link link = new Link(elementJiraIssue, element);
		assertNull(GenericLinkManager.getLinkInDatabase(link));

		KnowledgePersistenceManager.getInstance("TEST").insertLink(link, null);
		assertFalse(GenericLinkManager.deleteInvalidLinks());
	}

	@Test
	@NonTransactional
	public void testLinkInValidLink() {
		KnowledgeElement element = JiraIssues.addElementToDataBase();
		KnowledgeElement elementJiraIssue = new KnowledgeElement(
				JiraIssues.getTestJiraIssues().get(0));
		Link link = new Link(elementJiraIssue, element);
		KnowledgePersistenceManager.getInstance("TEST").insertLink(link, null);
		LinkInDatabase databaseEntry = GenericLinkManager.getLinkInDatabase(link);
		databaseEntry.setDestinationId(4223);
		databaseEntry.save();
		assertTrue(GenericLinkManager.deleteInvalidLinks());
	}
}
