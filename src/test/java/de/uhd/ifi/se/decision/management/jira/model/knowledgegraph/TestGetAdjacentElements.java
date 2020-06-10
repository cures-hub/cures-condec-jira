package de.uhd.ifi.se.decision.management.jira.model.knowledgegraph;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import org.jgrapht.Graphs;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGetAdjacentElements extends TestSetUp {

	private KnowledgeGraph graph;

	@Before
	public void setUp() {
		init();
		graph = new KnowledgeGraph("TEST");
	}

	@Test
	public void testDecisionKnowledgeElementValid() {
		KnowledgeElement element = new KnowledgeElement(JiraIssues.getTestJiraIssues().get(1));
		assertEquals(3, Graphs.neighborListOf(graph, element).size());
	}
}
