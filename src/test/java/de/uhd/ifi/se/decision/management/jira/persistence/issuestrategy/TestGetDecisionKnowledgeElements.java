package de.uhd.ifi.se.decision.management.jira.persistence.issuestrategy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.persistence.IssueStrategy;

public class TestGetDecisionKnowledgeElements extends TestIssueStrategySetUp {

	@Test
	public void testProjectNonExistent() {
		IssueStrategy issueStrategy = new IssueStrategy("NOTExistend");
		assertEquals(0, issueStrategy.getDecisionKnowledgeElements().size());
	}

	@Test
	public void testProjectExistent() {
		assertEquals(numberOfElements, issueStrategy.getDecisionKnowledgeElements().size());
	}
}
