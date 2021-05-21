package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestIsElementMatchingDocumentationCompletenessFilter extends TestSetUp {

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
	public void testIsElementMatchingDocumentationCompletenessFilter() {
		filteringManager.getFilterSettings().setIncompleteKnowledgeShown(false);
		assertFalse(filteringManager.isElementMatchingDocumentationIncompletenessFilter(element));

		filteringManager.getFilterSettings().setIncompleteKnowledgeShown(true);
		assertFalse(filteringManager.isElementMatchingDocumentationIncompletenessFilter(element));
	}
}