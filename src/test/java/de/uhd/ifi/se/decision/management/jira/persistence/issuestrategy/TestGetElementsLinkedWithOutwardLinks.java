package de.uhd.ifi.se.decision.management.jira.persistence.issuestrategy;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;

import static org.junit.Assert.assertEquals;

public class TestGetElementsLinkedWithOutwardLinks extends TestIssueStrategySetUp {

	@Test
	public void testElementNull() {
		assertEquals(0, issueStrategy.getElementsLinkedWithOutwardLinks(null).size());
	}

	@Test
	public void testElementNotExistend() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertEquals(0, issueStrategy.getElementsLinkedWithInwardLinks(element).size());
	}
}
