package de.uhd.ifi.se.decision.management.jira.model;

import com.atlassian.jira.component.ComponentAccessor;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeGraphImpl;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import javax.validation.constraints.NotNull;

import static org.junit.Assert.assertEquals;

public class TestKnowledgeGraph extends TestSetUp {

	private KnowledgeGraph graph;
	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 4));
		graph = new KnowledgeGraphImpl(element.getProject().getProjectKey(), element);
	}

	@Test
	@NonTransactional
	public void testGetNodes() {
		assertEquals(2, graph.getAllNodes().size(), 0.0);
	}

	@Test
	@NonTransactional
	public void testGetEdges() {
		assertEquals(2, graph.getAllEdges().size(), 0.0);
	}

	@Test
    @NonTransactional
    public void testGetSubGraph() {
	    KnowledgeGraph compareGraph = graph.getSubGraph(element);
	    assertEquals(graph.getAllNodes().size(), compareGraph.getAllNodes().size(), 0.0);
    }

}
