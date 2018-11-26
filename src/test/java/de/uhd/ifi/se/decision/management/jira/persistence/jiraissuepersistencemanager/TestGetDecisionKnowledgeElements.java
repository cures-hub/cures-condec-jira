package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssuePersistenceManager;

public class TestGetDecisionKnowledgeElements extends TestJiraIssuePersistenceManagerSetUp {

	@Test
	public void testProjectNonExistent() {
		JiraIssuePersistenceManager issueStrategy = new JiraIssuePersistenceManager("NOTExistend");
		assertEquals(0, issueStrategy.getDecisionKnowledgeElements().size());
	}

	@Test
	public void testProjectExistent() {
		assertEquals(numberOfElements, issueStrategy.getDecisionKnowledgeElements().size());
	}
}
