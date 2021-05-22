package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestIsElementMatchingTimeFilter extends TestSetUp {

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
	public void testNoTimeFilterSet() {
		assertTrue(filteringManager.isElementMatchingTimeFilter(element));
	}

	@Test
	public void testTimeFilterSetAndElementCreatedInBetween() {
		filteringManager.getFilterSettings().setStartDate(1);
		filteringManager.getFilterSettings().setEndDate(new Date().getTime());

		assertTrue(filteringManager.isElementMatchingTimeFilter(element));
	}

	@Test
	public void testTimeFilterSetAndElementCreatedAfterwards() {
		filteringManager.getFilterSettings().setStartDate(1);
		filteringManager.getFilterSettings().setEndDate(2);

		assertFalse(filteringManager.isElementMatchingTimeFilter(element));
		assertFalse(filteringManager.isElementMatchingFilterSettings(element));
	}

	@Test
	public void testTimeFilterSetAndElementCreatedBefore() {
		filteringManager.getFilterSettings().setStartDate(element.getCreationDate().getTime() + 8640000);
		filteringManager.getFilterSettings().setEndDate(2);

		assertFalse(filteringManager.isElementMatchingTimeFilter(element));
	}

	@Test
	public void testTimeFilterSetAndElementCreatedExactlyThen() {
		filteringManager.getFilterSettings().setStartDate(element.getUpdatingDate().getTime());
		filteringManager.getFilterSettings().setEndDate(element.getCreationDate().getTime() + 86400000);

		assertTrue(filteringManager.isElementMatchingTimeFilter(element));
	}
}