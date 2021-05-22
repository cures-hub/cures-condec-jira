package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestFilterLinkTypes extends TestSetUp {

	private FilteringManager filteringManager;

	@Before
	public void setUp() {
		init();
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElement("TEST-1");
		filteringManager = new FilteringManager(filterSettings);
	}

	@Test
	public void testNoLinkTypesSelected() {
		filteringManager.getFilterSettings().setLinkTypes(new HashSet<>());
		assertTrue(filteringManager.getLinksNotMatchingFilterSettings(new HashSet<>(Links.getTestLinks())).size() > 10);
		KnowledgeGraph subgraph = filteringManager.getFilteredGraph();
		assertTrue(subgraph.edgeSet().size() == 0);
	}

	@Test
	public void testOneLinkTypeSelected() {
		filteringManager.getFilterSettings().setLinkTypes(Set.of("relates"));
		assertEquals(0, filteringManager.getLinksNotMatchingFilterSettings(new HashSet<>(Links.getTestLinks())).size());
		KnowledgeGraph subgraph = filteringManager.getFilteredGraph();
		assertTrue(subgraph.edgeSet().size() > 0);
	}
}