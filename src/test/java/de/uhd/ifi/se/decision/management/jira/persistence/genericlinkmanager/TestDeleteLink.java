package de.uhd.ifi.se.decision.management.jira.persistence.genericlinkmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteLink extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testLinkNull() {
		assertFalse(GenericLinkManager.deleteLink(null));
	}

	@Test
	@NonTransactional
	public void testLinkFilled() {
		KnowledgeElement element = JiraIssues.addElementToDataBase();
		KnowledgeElement elementJiraIssue = new KnowledgeElement(
				JiraIssues.getTestJiraIssues().get(0));
		Link link = new Link(elementJiraIssue, element);
		long linkId = KnowledgePersistenceManager.getOrCreate("TEST").insertLink(link, null);
		link.setId(linkId);
		assertTrue(GenericLinkManager.deleteLink(link));
	}
}
