package de.uhd.ifi.se.decision.management.jira.persistence.issuestrategy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.IssueStrategy;

public class TestGetDecisionKnowledgeElements extends TestIssueStrategySetUp {

	//TODO
//	@Test
//	public void testKeyNull() {
//		assertEquals(issueStrategy.getDecisionKnowledgeElements(), new ArrayList<DecisionKnowledgeElement>());
//	}

	@Test
	public void testKeyNotExistend() {
		IssueStrategy issueStrategy = new IssueStrategy("NOTExistend");
		assertEquals(0, issueStrategy.getDecisionKnowledgeElements().size());
	}

	//TODO
//	@Test
//	public void testKeyExistend() {
//		assertEquals(13, issueStrategy.getDecisionKnowledgeElements().size());
//	}
}
