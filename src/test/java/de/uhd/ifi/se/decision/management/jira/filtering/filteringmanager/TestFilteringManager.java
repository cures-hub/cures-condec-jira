package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;

public class TestFilteringManager extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testConstructorWithQueryInvalid() {
		FilteringManager filteringManager = new FilteringManager(null, null, null);
		assertEquals(0, filteringManager.getElementsMatchingFilterSettings().size());
		assertNull(filteringManager.getFilteredGraph());
	}

	@Test
	public void testConstructorWithFilterSettingsInvalid() {
		FilteringManager filteringManager = new FilteringManager(null);
		assertNull(filteringManager.getFilterSettings());
	}

	@Test
	public void testSetFilterSettings() {
		FilteringManager filteringManager = new FilteringManager(null);
		FilterSettings settings = new FilterSettings("TEST", "search term");
		filteringManager.setFilterSettings(settings);
		assertEquals("search term", filteringManager.getFilterSettings().getSearchTerm());
	}
}