package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestIsElementMatchingSubStringFilter extends TestSetUp {

	private FilteringManager filteringManager;
	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		FilterSettings settings = new FilterSettings("TEST", "");
		filteringManager = new FilteringManager(settings);
		element = KnowledgeElements.getTestKnowledgeElement();
	}

	@Test
	public void testNoSearchTermInFilter() {
		assertTrue(filteringManager.isElementMatchingSubStringFilter(element));
	}

	@Test
	public void testNoSearchTermDiffers() {
		filteringManager.getFilterSettings().setSearchTerm("42");
		assertFalse(filteringManager.isElementMatchingSubStringFilter(element));
		assertFalse(filteringManager.isElementMatchingFilterSettings(element));
	}

	@Test
	public void testSearchTermInDescription() {
		element.setDescription("42");
		filteringManager.getFilterSettings().setSearchTerm("42");
		assertTrue(filteringManager.isElementMatchingSubStringFilter(element));
	}

	@Test
	public void testSearchTermNotInDescriptionButInSummary() {
		element.setDescription("42");
		filteringManager.getFilterSettings().setSearchTerm("WI");
		assertTrue(filteringManager.isElementMatchingSubStringFilter(element));
	}

	@Test
	public void testSearchTermMatchesKey() {
		element.setDescription(null);
		element.setSummary(null);
		filteringManager.getFilterSettings().setSearchTerm("TEST-1");
		assertTrue(filteringManager.isElementMatchingSubStringFilter(element));
	}

	@Test
	public void testSearchTermIsJiraQuery() {
		filteringManager.getFilterSettings().setSearchTerm("?jql=project=CONDEC");
		assertTrue(filteringManager.isElementMatchingSubStringFilter(element));
	}

}