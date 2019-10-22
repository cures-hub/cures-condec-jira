package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;

public class TestGetElementsLinkedWithInwardLinks extends TestJiraIssuePersistenceManagerSetUp {

	@Test
	public void testElementNull() {
		assertEquals(0, issueStrategy.getElementsLinkedWithInwardLinks(null).size());
	}

	@Test
	public void testElementNonExistent() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertEquals(0, issueStrategy.getElementsLinkedWithInwardLinks(element).size());
	}
}
