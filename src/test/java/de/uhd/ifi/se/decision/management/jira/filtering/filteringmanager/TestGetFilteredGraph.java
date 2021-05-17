package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetFilteredGraph extends TestSetUp {

	private ApplicationUser user;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElement("TEST-1");
	}

	@Test
	public void testFilterSettingsNull() {
		FilteringManager filteringManager = new FilteringManager(user, null);
		assertNull(filteringManager.getFilteredGraph());
	}

	@Test
	public void testGraphNull() {
		FilteringManager filteringManager = new FilteringManager(user, new FilterSettings(null, ""));
		assertNull(filteringManager.getFilteredGraph());
	}

	@Test
	public void testLinkDistanceZero() {
		filterSettings.setLinkDistance(0);
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		assertEquals(1, filteringManager.getFilteredGraph().vertexSet().size());
		assertEquals(0, filteringManager.getFilteredGraph().edgeSet().size());
	}

	@Test
	public void testLinkDistanceOne() {
		filterSettings.setLinkDistance(1);
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		assertEquals(6, filteringManager.getFilteredGraph().vertexSet().size());
	}

	@Test
	public void testLinkDistanceTwo() {
		filterSettings.setLinkDistance(2);
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		assertEquals(10, filteringManager.getFilteredGraph().vertexSet().size());
	}

	@Test
	public void testLinkDistanceThree() {
		filterSettings.setLinkDistance(3);
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		assertEquals(10, filteringManager.getFilteredGraph().vertexSet().size());
	}

	@Test
	public void testTransitiveLinksEnabled() {
		filterSettings.setKnowledgeTypes(new HashSet<String>(Arrays.asList("Issue", "Argument", "Pro", "Con")));
		filterSettings.setCreateTransitiveLinks(true);
		filterSettings.setLinkDistance(4);
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		Graph<KnowledgeElement, Link> subgraph = filteringManager.getFilteredGraph();
		assertEquals(4, subgraph.edgeSet().size());

		Set<Link> transitiveLinks = new HashSet<Link>(subgraph.edgeSet());
		transitiveLinks.removeIf(link -> link.getType() != LinkType.TRANSITIVE);
		assertEquals(1, transitiveLinks.size());
	}

	@Test
	public void testTransitiveLinksDisabled() {
		filterSettings.setKnowledgeTypes(new HashSet<String>(Arrays.asList("Issue", "Argument", "Pro", "Con")));
		filterSettings.setCreateTransitiveLinks(false);
		filterSettings.setLinkDistance(4);
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		Graph<KnowledgeElement, Link> subgraph = filteringManager.getFilteredGraph();
		assertEquals(3, subgraph.edgeSet().size());

		Set<Link> transitiveLinks = new HashSet<Link>(subgraph.edgeSet());
		transitiveLinks.removeIf(link -> link.getType() != LinkType.TRANSITIVE);
		assertEquals(0, transitiveLinks.size());
	}

	@Test
	public void testTransitiveLinksNoSelectedElement() {
		filterSettings.setSelectedElement((KnowledgeElement) null);
		filterSettings.setKnowledgeTypes(new HashSet<String>(Arrays.asList("Issue", "Argument", "Pro", "Con")));
		filterSettings.setCreateTransitiveLinks(true);
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		Graph<KnowledgeElement, Link> subgraph = filteringManager.getFilteredGraph();
		Set<Link> transitiveLinks = new HashSet<Link>(subgraph.edgeSet());
		transitiveLinks.removeIf(link -> link.getType() != LinkType.TRANSITIVE);
		assertEquals(0, transitiveLinks.size());
	}
}