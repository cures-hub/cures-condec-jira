package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeGraphImpl;
import net.java.ao.test.jdbc.NonTransactional;

public class TestKnowledgeGraph extends TestSetUp {

	private KnowledgeGraph graph;
	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 4));
		graph = new KnowledgeGraphImpl(element.getProject().getProjectKey());
	}

	@Test
	@NonTransactional
	public void testGetNodes() {
		assertEquals(8, graph.vertexSet().size());
	}

	@Test
	@NonTransactional
	public void testGetEdges() {
		assertEquals(1, graph.edgeSet().size());
	}

	@Test
	@NonTransactional
	public void testGetSubGraph() {
		KnowledgeGraph compareGraph = graph.getSubGraph(element);
		assertEquals(1, compareGraph.vertexSet().size());
	}

}
