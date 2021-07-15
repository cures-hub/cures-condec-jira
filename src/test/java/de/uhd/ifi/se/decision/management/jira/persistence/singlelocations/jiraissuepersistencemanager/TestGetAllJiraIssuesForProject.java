package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;

public class TestGetAllJiraIssuesForProject extends TestSetUp {

	private JiraIssuePersistenceManager persistenceManager;

	@Before
	public void setUp() {
		init();
		persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueManager();
	}

	@Test
	public void testProjectExistent() {
		assertTrue(persistenceManager.getAllJiraIssuesForProject().size() > 0);
	}

	@Test
	public void testProjectUnknown() {
		assertTrue(KnowledgePersistenceManager.getOrCreate("UNKNOWN").getJiraIssueManager().getAllJiraIssuesForProject()
				.size() == 0);
	}
}
