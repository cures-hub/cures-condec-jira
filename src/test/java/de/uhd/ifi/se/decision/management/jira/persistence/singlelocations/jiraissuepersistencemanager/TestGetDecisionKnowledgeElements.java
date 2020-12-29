package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;

public class TestGetDecisionKnowledgeElements extends TestJiraIssuePersistenceManagerSetUp {

	@Test
	public void testProjectNonExistent() {
		JiraIssuePersistenceManager issueStrategy = KnowledgePersistenceManager.getOrCreate("NOTEXISTENT").getJiraIssueManager();
		assertEquals(0, issueStrategy.getKnowledgeElements().size());
	}

	@Test
	public void testProjectExistent() {
		assertEquals(numberOfElements, issueStrategy.getKnowledgeElements().size());
	}
}
