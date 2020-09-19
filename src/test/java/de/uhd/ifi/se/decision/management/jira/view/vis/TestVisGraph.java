package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestVisGraph extends TestSetUp {
	private VisGraph visGraph;
	private ApplicationUser user;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		filterSettings = new FilterSettings("TEST", "");
		visGraph = new VisGraph(user, filterSettings);
	}

	@Test
	public void testGetNodes() {
		assertEquals(10, visGraph.getNodes().size());
	}

	@Test
	public void testGetEdges() {
		assertEquals(10, KnowledgeGraph.getOrCreate("TEST").vertexSet().size());
		assertEquals(15, KnowledgeGraph.getOrCreate("TEST").edgeSet().size());
		assertEquals(15, visGraph.getEdges().size());
	}

	@Test
	public void testSetNodes() {
		HashSet<VisNode> newNodes = new HashSet<VisNode>();
		visGraph.setNodes(newNodes);
		assertEquals(visGraph.getNodes(), newNodes);
	}

	@Test
	public void testSetEdges() {
		HashSet<VisEdge> newEdges = new HashSet<VisEdge>();
		visGraph.setEdges(newEdges);
		assertEquals(visGraph.getEdges(), newEdges);
	}

	@Test
	public void testGetSelectedVisNodeId() {
		assertEquals("", visGraph.getSelectedVisNodeId());
	}

	@Test
	public void testGetGraph() {
		assertEquals(10, visGraph.getGraph().vertexSet().size());
	}

	@Test
	public void testConstructorUserNullFilterSettingsNull() {
		assertNotNull(new VisGraph((ApplicationUser) null, (FilterSettings) null));
	}

	@Test
	public void testConstructorUserFilledFilterSettingsNull() {
		assertNotNull(new VisGraph(user, (FilterSettings) null));
	}

	@Test
	public void testConstructorUserNullFilterSettingsFilled() {
		assertNotNull(new VisGraph((ApplicationUser) null, filterSettings));
	}

	@Test
	public void testConstructorUserValidFilterSettingsFilledRootElementExisting() {
		filterSettings.setSelectedElement("TEST-1");
		filterSettings.setLinkDistance(1);
		visGraph = new VisGraph(user, filterSettings);
		assertTrue(KnowledgeGraph.getOrCreate("TEST").vertexSet().size() > visGraph.getGraph().vertexSet().size());
		assertTrue(KnowledgeGraph.getOrCreate("TEST").edgeSet().size() > visGraph.getGraph().edgeSet().size());
	}

	@Test
	public void testVisGraphWithHierarchy() {
		filterSettings.setHierarchical(true);
		filterSettings.setSelectedElement("TEST-1");
		visGraph = new VisGraph(user, filterSettings);
		Set<VisNode> nodes = visGraph.getNodes();
		assertEquals(visGraph.getGraph().vertexSet().size(), nodes.size());
	}
}
