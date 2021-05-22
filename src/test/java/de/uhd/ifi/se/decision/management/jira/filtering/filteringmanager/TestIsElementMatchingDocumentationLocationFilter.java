package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestIsElementMatchingDocumentationLocationFilter extends TestSetUp {

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
	public void testAllDocumentationLocationsInFilter() {
		assertTrue(filteringManager.isElementMatchingDocumentationLocationFilter(element));
		assertTrue(filteringManager.isElementMatchingFilterSettings(element));
	}

	@Test
	public void testNoDocumentationLocationInFilter() {
		filteringManager.getFilterSettings().setDocumentationLocations(new ArrayList<>());
		assertFalse(filteringManager.isElementMatchingDocumentationLocationFilter(element));
		assertFalse(filteringManager.isElementMatchingFilterSettings(element));
	}
}