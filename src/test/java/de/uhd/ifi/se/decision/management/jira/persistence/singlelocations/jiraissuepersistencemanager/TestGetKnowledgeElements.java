package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestGetKnowledgeElements extends TestSetUp {

	private JiraIssuePersistenceManager persistenceManager;

	@Before
	public void setUp() {
		init();
		persistenceManager = KnowledgePersistenceManager.getInstance("TEST").getJiraIssueManager();
	}

	@Test
	public void testProjectNonExistent() {
		JiraIssuePersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance("NOTEXISTENT")
				.getJiraIssueManager();
		assertEquals(0, persistenceManager.getKnowledgeElements().size());
	}

	@Test
	public void testProjectExistent() {
		assertEquals(JiraIssues.getTestJiraIssues().size(), persistenceManager.getKnowledgeElements().size());
	}
}
