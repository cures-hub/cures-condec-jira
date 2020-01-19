package de.uhd.ifi.se.decision.management.jira.model.knowledgegraph;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeGraphImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestGetUnlinkedElements extends TestSetUp {

	public KnowledgePersistenceManager knowledgePersistenceManager;

	private KnowledgeGraph graph;

	@Before
	public void setUp() {
		init();
		graph = new KnowledgeGraphImpl("TEST");
	}

	@Test
	public void testElementNull() {
		assertEquals(8, graph.getUnlinkedElements(null).size());
	}

	@Test
	public void testElementValid() {
		Issue issue = JiraIssues.getTestJiraIssues().get(0);
		KnowledgeElement element = new KnowledgeElementImpl(issue);
		assertEquals(5, graph.getUnlinkedElements(element).size());
	}
}
