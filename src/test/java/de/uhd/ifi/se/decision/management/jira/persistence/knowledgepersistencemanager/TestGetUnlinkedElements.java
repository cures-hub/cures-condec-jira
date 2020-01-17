package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestGetUnlinkedElements extends TestSetUp {

	public KnowledgePersistenceManager knowledgePersistenceManager;

	@Before
	public void setUp() {
		init();
		knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate("TEST");
	}

	@Test
	public void testElementNull() {
		assertEquals(8, knowledgePersistenceManager.getUnlinkedElements(null).size());
	}

	@Test
	public void testElementValid() {
		Issue issue = JiraIssues.getTestJiraIssues().get(0);
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(issue);
		assertEquals(6, knowledgePersistenceManager.getUnlinkedElements(element).size());
	}
}
