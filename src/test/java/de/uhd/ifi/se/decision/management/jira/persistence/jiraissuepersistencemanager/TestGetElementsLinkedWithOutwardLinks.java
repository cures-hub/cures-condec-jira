package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;

public class TestGetElementsLinkedWithOutwardLinks extends TestIssueStrategySetUp {

	@Test
	public void testElementNull() {
		assertEquals(0, issueStrategy.getElementsLinkedWithOutwardLinks(null).size());
	}

	@Test
	public void testElementNonExistent() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertEquals(0, issueStrategy.getElementsLinkedWithInwardLinks(element).size());
	}
}
