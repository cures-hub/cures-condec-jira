package de.uhd.ifi.se.decision.documentation.jira.persistence.issusstrategytest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;

public class TestGetDecisionKnowledgeElements extends TestIssueStrategySetUp {

	@Test
	public void testKeyNull() {
		assertEquals(issueStrategy.getDecisionKnowledgeElements(null), new ArrayList<DecisionKnowledgeElement>());
	}

	@Test
	public void testKeyNotExistend() {
		assertEquals(0, issueStrategy.getDecisionKnowledgeElements("NOTExistend").size());
	}

	@Test
	public void testKeyExistend() {
		assertEquals(13, issueStrategy.getDecisionKnowledgeElements("TEST").size());
	}
}
