package de.uhd.ifi.se.decision.management.jira.model.knowledgegraph;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetMutableSubgraph extends TestSetUp {

	private KnowledgeGraph graph;

	@Before
	public void setUp() {
		init();
		graph = new KnowledgeGraph("TEST");
	}

	@Test
	@NonTransactional
	public void testThatSubgraphIsMutable() {
		KnowledgeGraph mutableSubgraph = graph.getMutableSubgraphFor(graph.vertexSet());
		mutableSubgraph.addVertex(new KnowledgeElement());
		assertEquals(graph.vertexSet().size() + 1, mutableSubgraph.vertexSet().size());
	}
}
