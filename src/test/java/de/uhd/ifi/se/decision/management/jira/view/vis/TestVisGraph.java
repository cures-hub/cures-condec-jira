package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeGraphImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestVisGraph extends TestSetUp {
	private VisGraph visGraph;
	private HashSet<VisNode> nodes;
	private HashSet<VisEdge> edges;
	private KnowledgeElement element;
	private ApplicationUser user;
	private FilterSettings filterSettings;

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

		element = new KnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 14));
		element.setProject(new DecisionKnowledgeProjectImpl("TEST"));

		user = JiraUsers.BLACK_HEAD.getApplicationUser();
		filterSettings = new FilterSettingsImpl("TEST", "");
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
	public void testConstUserNullFilterNull() {
		assertNotNull(new VisGraph((ApplicationUser) null, (FilterSettings) null));
	}

	@Test
	public void testConstUserFilledFilterNull() {
		assertNotNull(new VisGraph(user, (FilterSettings) null));
	}

	@Test
	public void testConstUserNullFilterFilled() {
		assertNotNull(new VisGraph((ApplicationUser) null, filterSettings));
	}

	@Test
	public void testConstUserFilterFilled() {
		assertNotNull(new VisGraph(user, filterSettings));
	}
}
