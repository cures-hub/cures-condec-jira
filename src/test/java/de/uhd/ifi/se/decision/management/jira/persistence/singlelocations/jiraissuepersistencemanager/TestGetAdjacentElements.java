package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestGetAdjacentElements extends TestJiraIssuePersistenceManagerSetUp {

	@Test
	public void testDecisionKnowledgeElementNull() {
		assertEquals(new ArrayList<DecisionKnowledgeElementImpl>(), issueStrategy.getAdjacentElements(null));
	}

	@Test
	public void testDecisionKnowledgeElementEmpty() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertEquals(0, issueStrategy.getAdjacentElements(element).size());
	}

	@Test
	public void testDecisionKnowledgeElementValid() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(JiraIssues.getTestJiraIssues().get(1));
		assertNotNull(issueStrategy.getAdjacentElements(element));
	}
}
