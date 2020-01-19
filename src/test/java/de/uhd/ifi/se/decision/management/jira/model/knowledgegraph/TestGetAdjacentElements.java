package de.uhd.ifi.se.decision.management.jira.model.knowledgegraph;

import static org.junit.Assert.assertEquals;

import org.jgrapht.Graphs;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeGraphImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestGetAdjacentElements extends TestSetUp {

	private KnowledgeGraph graph;

	@Before
	public void setUp() {
		init();
		graph = new KnowledgeGraphImpl("TEST");
	}

	@Test
	public void testDecisionKnowledgeElementValid() {
		KnowledgeElement element = new KnowledgeElementImpl(JiraIssues.getTestJiraIssues().get(1));
		assertEquals(2, Graphs.neighborListOf(graph, element).size());
	}
}
