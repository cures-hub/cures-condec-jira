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

public class TestIsElementMatchingDegreeFilter extends TestSetUp {

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
	public void testMinDegreeZero() {
		assertTrue(filteringManager.isElementMatchingDegreeFilter(element));
		assertTrue(filteringManager.isElementMatchingFilterSettings(element));
	}

	@Test
	public void testMinDegreeHigherThanNodeDegreeOfElement() {
		filteringManager.getFilterSettings().setMinDegree(20);
		assertFalse(filteringManager.isElementMatchingDegreeFilter(element));
		assertFalse(filteringManager.isElementMatchingFilterSettings(element));
	}

	@Test
	public void testMaxDegreeSmallerThanNodeDegreeOfElement() {
		filteringManager.getFilterSettings().setMinDegree(1);
		filteringManager.getFilterSettings().setMaxDegree(2);
		assertFalse(filteringManager.isElementMatchingDegreeFilter(element));
	}

	@Test
	public void testMinDegreeAndMaxDegreeEqualNodeDegreeOfElement() {
		int elementDegree = element.getLinks().size();
		filteringManager.getFilterSettings().setMinDegree(elementDegree);
		filteringManager.getFilterSettings().setMaxDegree(elementDegree);
		assertTrue(filteringManager.isElementMatchingDegreeFilter(element));
	}

}