package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeGraphImpl;

public class TestVisGraph extends TestSetUp {
	private VisGraph visGraph;
	private HashSet<VisNode> nodes;
	private HashSet<VisEdge> edges;
	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		init();
		nodes = new HashSet<VisNode>();
		edges = new HashSet<VisEdge>();
		visGraph = new VisGraph();
		visGraph.setEdges(edges);
		visGraph.setNodes(nodes);
		visGraph.setGraph(new KnowledgeGraphImpl("TEST"));
		visGraph.setRootElementKey("");

		element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 14));
		element.setProject(new DecisionKnowledgeProjectImpl("TEST"));
	}

	@Test
	public void testGetNodes() {
		assertEquals(this.visGraph.getNodes(), this.nodes);
	}

	@Test
	public void testGetEdges() {
		assertEquals(this.visGraph.getEdges(), this.edges);
	}

	@Test
	public void testSetNodes() {
		HashSet<VisNode> newNodes = new HashSet<>();
		this.visGraph.setNodes(newNodes);
		assertEquals(this.visGraph.getNodes(), newNodes);
	}

	@Test
	public void testSetEdges() {
		HashSet<VisEdge> newEdges = new HashSet<>();
		this.visGraph.setEdges(newEdges);
		assertEquals(this.visGraph.getEdges(), newEdges);
	}

	@Test
	public void testWithoutFiltering() {
		List<DecisionKnowledgeElement> elements = new ArrayList<>();
		elements.add(element);
		VisGraph newVisGraph = new VisGraph(element, elements);
		assertNotNull(newVisGraph);
	}

	@Test
	public void testConstWithListNullProjectNull() {
		VisGraph visGraph = new VisGraph((List<DecisionKnowledgeElement>) null, (String) null);
		assertEquals(0, visGraph.getEdges().size(), 0.0);
	}

	@Test
	public void testConstWithListEmptyProjectNull() {
		List<DecisionKnowledgeElement> elements = new ArrayList<>();
		VisGraph visGraph = new VisGraph(elements, (String) null);
		assertEquals(0, visGraph.getEdges().size(), 0.0);
	}

	@Test
	public void testConstWithListFilledProjectNull() {
		List<DecisionKnowledgeElement> elements = new ArrayList<>();
		elements.add(element);
		VisGraph visGraph = new VisGraph(elements, (String) null);
		assertEquals(0, visGraph.getEdges().size(), 0.0);
	}

	@Test
	public void testConstWithListNullProjectFilled() {
		VisGraph visGraph = new VisGraph((List<DecisionKnowledgeElement>) null, "TEST");
		assertEquals(0, visGraph.getNodes().size(), 0.0);
	}

	@Test
	public void testConstWithListEmptyProjectFilled() {
		List<DecisionKnowledgeElement> elements = new ArrayList<>();
		VisGraph visGraph = new VisGraph(elements, "TEST");
		assertEquals(0, visGraph.getNodes().size(), 0.0);
	}

	@Test
	public void testConstWithListFilledProjectFilled() {
		List<DecisionKnowledgeElement> elements = new ArrayList<>();
		elements.add(element);
		VisGraph visGraph = new VisGraph(elements, "TEST");
		assertEquals(4, visGraph.getNodes().size());
	}

	@Test
	public void testGetRootElementKey() {
		assertEquals("", visGraph.getRootElementKey());
	}

	@Test
	public void testSetRootElementKey() {
		visGraph.setRootElementKey("TestKey");
		assertEquals("TestKey", visGraph.getRootElementKey());
	}

	@Test
	public void testGetGraph() {
		assertNotNull(visGraph.getGraph());
	}

	@Test
	public void testSetGraph() {
		KnowledgeGraph newGraph = KnowledgeGraph.getOrCreate("ConDec");
		visGraph.setGraph(newGraph);
		assertEquals(newGraph, visGraph.getGraph());
	}

	@Test
	public void addNewNodeToGraph() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(42, "", "", KnowledgeType.DECISION, "TEST",
				"TEST-42", DocumentationLocation.JIRAISSUE, KnowledgeStatus.DECIDED);
		VisGraph visGraph = new VisGraph(element, new ArrayList<DecisionKnowledgeElement>());
		KnowledgeGraph newGraph = KnowledgeGraph.getOrCreate("ConDec");
		visGraph.setGraph(newGraph);
		assertEquals(newGraph, visGraph.getGraph());
	}
}
