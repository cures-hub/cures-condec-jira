package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestGetAdjacentElements extends TestJiraIssuePersistenceManagerSetUp {

	@Test
	public void testDecisionKnowledgeElementNull() {
		assertEquals(new ArrayList<KnowledgeElementImpl>(), issueStrategy.getAdjacentElements(null));
	}

	@Test
	public void testDecisionKnowledgeElementEmpty() {
		KnowledgeElement element = new KnowledgeElementImpl();
		assertEquals(0, issueStrategy.getAdjacentElements(element).size());
	}

	@Test
	public void testDecisionKnowledgeElementValid() {
		KnowledgeElement element = new KnowledgeElementImpl(JiraIssues.getTestJiraIssues().get(1));
		assertNotNull(issueStrategy.getAdjacentElements(element));
	}
}
