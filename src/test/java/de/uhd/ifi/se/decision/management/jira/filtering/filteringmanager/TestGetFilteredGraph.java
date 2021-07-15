package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

public class TestGetFilteredGraph extends TestSetUp {

	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElement("TEST-1");
	}

	@Test
	public void testFilterSettingsNull() {
		FilteringManager filteringManager = new FilteringManager(null);
		assertNull(filteringManager.getFilteredGraph());
	}

	@Test
	public void testGraphNull() {
		FilteringManager filteringManager = new FilteringManager(new FilterSettings(null, ""));
		assertNull(filteringManager.getFilteredGraph());
	}

	@Test
	public void testLinkDistanceZero() {
		filterSettings.getDefinitionOfDone().setMaximumLinkDistanceToDecisions(0);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		assertEquals(1, filteringManager.getFilteredGraph().vertexSet().size());
		assertEquals(0, filteringManager.getFilteredGraph().edgeSet().size());
	}

	@Test
	public void testLinkDistanceOne() {
		filterSettings.getDefinitionOfDone().setMaximumLinkDistanceToDecisions(1);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		assertEquals(6, filteringManager.getFilteredGraph().vertexSet().size());
	}

	@Test
	public void testLinkDistanceTwo() {
		filterSettings.getDefinitionOfDone().setMaximumLinkDistanceToDecisions(2);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		assertEquals(10, filteringManager.getFilteredGraph().vertexSet().size());
	}

	@Test
	public void testLinkDistanceThree() {
		filterSettings.getDefinitionOfDone().setMaximumLinkDistanceToDecisions(3);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		assertEquals(10, filteringManager.getFilteredGraph().vertexSet().size());
	}

	@Test
	public void testTransitiveLinksEnabled() {
		filterSettings.setKnowledgeTypes(new HashSet<String>(Arrays.asList("Issue", "Argument", "Pro", "Con")));
		filterSettings.setCreateTransitiveLinks(true);
		filterSettings.getDefinitionOfDone().setMaximumLinkDistanceToDecisions(4);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
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
		filterSettings.getDefinitionOfDone().setMaximumLinkDistanceToDecisions(4);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
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
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		Graph<KnowledgeElement, Link> subgraph = filteringManager.getFilteredGraph();
		Set<Link> transitiveLinks = new HashSet<Link>(subgraph.edgeSet());
		transitiveLinks.removeIf(link -> link.getType() != LinkType.TRANSITIVE);
		assertEquals(0, transitiveLinks.size());
	}
}