package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
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
		filterSettings = new FilterSettingsImpl("TEST", "");
		visGraph = new VisGraph(user, filterSettings);
	}

	@Test
	public void testGetNodes() {
		assertEquals(8, visGraph.getNodes().size());
	}

	@Test
	public void testGetEdges() {
		assertEquals(9, visGraph.getEdges().size());
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
	public void testGetRootElementKey() {
		assertEquals("", visGraph.getRootElementKey());
	}

	@Test
	public void testGetGraph() {
		assertEquals(KnowledgeGraph.getOrCreate("TEST"), visGraph.getGraph());
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
	public void testConstructorUserNullFilterSettingsFilledRootElementExisting() {
		assertNotNull(new VisGraph(user, filterSettings, "TEST-1"));
	}
}
