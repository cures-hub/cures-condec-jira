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
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class TestGetElementsMatchingFilterSettings extends TestSetUp {

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
		assertEquals(0, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testGraphNull() {
		FilteringManager filteringManager = new FilteringManager(new FilterSettings(null, ""));
		assertEquals(0, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testNoSelectedElement() {
		filterSettings.setSelectedElementObject((KnowledgeElement) null);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		assertTrue(filteringManager.getElementsMatchingFilterSettings().size() > 10);
	}

	@Test
	public void testLinkDistanceZero() {
		filterSettings.getDefinitionOfDone().setMaximumLinkDistanceToDecisions(0);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		assertEquals(1, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testLinkDistanceOne() {
		filterSettings.getDefinitionOfDone().setMaximumLinkDistanceToDecisions(1);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		assertEquals(6, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testLinkDistanceTwo() {
		filterSettings.getDefinitionOfDone().setMaximumLinkDistanceToDecisions(2);
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		assertEquals(10, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testGetElementsByType() {
		Set<String> knowledgeTypes = new HashSet<>();
		knowledgeTypes.add("Decision");
		filterSettings.setKnowledgeTypes(knowledgeTypes);

		FilteringManager filteringManager = new FilteringManager(filterSettings);
		assertEquals(2, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testGetElementsBySearchTerm() {
		filterSettings.setSearchTerm("Implement feature");
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		assertEquals(1, filteringManager.getElementsMatchingFilterSettings().size());
	}
}