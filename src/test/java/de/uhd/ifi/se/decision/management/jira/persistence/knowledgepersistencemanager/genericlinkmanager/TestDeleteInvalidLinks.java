package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.genericlinkmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.GenericLinkManager;
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
		DecisionKnowledgeElement element = JiraIssues.addElementToDataBase();
		DecisionKnowledgeElement elementJiraIssue = new DecisionKnowledgeElementImpl(
				JiraIssues.getTestJiraIssues().get(0));
		Link link = new LinkImpl(elementJiraIssue, element);
		assertNull(GenericLinkManager.getLinkInDatabase(link));

		KnowledgePersistenceManager.getOrCreate("TEST").insertLink(link, null);
		assertFalse(GenericLinkManager.deleteInvalidLinks());
	}

	@Test
	@NonTransactional
	public void testLinkInValidLink() {
		DecisionKnowledgeElement element = JiraIssues.addElementToDataBase();
		DecisionKnowledgeElement elementJiraIssue = new DecisionKnowledgeElementImpl(
				JiraIssues.getTestJiraIssues().get(0));
		Link link = new LinkImpl(elementJiraIssue, element);
		KnowledgePersistenceManager.getOrCreate("TEST").insertLink(link, null);
		LinkInDatabase databaseEntry = GenericLinkManager.getLinkInDatabase(link);
		databaseEntry.setDestinationId(4223);
		databaseEntry.save();
		assertTrue(GenericLinkManager.deleteInvalidLinks());
	}
}
