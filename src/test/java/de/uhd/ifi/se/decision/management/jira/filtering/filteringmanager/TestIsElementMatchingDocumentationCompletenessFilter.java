package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestIsElementMatchingDocumentationCompletenessFilter extends TestSetUp {

	private FilteringManager filteringManager;

	@Before
	public void setUp() {
		init();
		FilterSettings settings = new FilterSettings("TEST", "");
		filteringManager = new FilteringManager(settings);
		filteringManager.getFilterSettings().setStatus(new ArrayList<>());
	}

	@Test
	public void testIncompleteElementAndAllElementsShouldBeShown() {
		filteringManager.getFilterSettings().setOnlyIncompleteKnowledgeShown(false);
		assertTrue(filteringManager
				.isElementMatchingDocumentationCompletenessFilter(KnowledgeElements.getUnsolvedDecisionProblem()));
		assertFalse(filteringManager.isElementMatchingFilterSettings(KnowledgeElements.getUnsolvedDecisionProblem()));
	}

	@Test
	public void testCompleteElementAndAllElementsShouldBeShown() {
		filteringManager.getFilterSettings().setOnlyIncompleteKnowledgeShown(false);
		assertTrue(
				filteringManager.isElementMatchingDocumentationCompletenessFilter(KnowledgeElements.getDecision()));
		assertFalse(filteringManager.isElementMatchingFilterSettings(KnowledgeElements.getDecision()));
	}

	@Test
	public void testIncompleteElementAndOnlyIncompleteElementsShouldBeShown() {
		filteringManager.getFilterSettings().setOnlyIncompleteKnowledgeShown(true);
		assertTrue(filteringManager
				.isElementMatchingDocumentationCompletenessFilter(KnowledgeElements.getUnsolvedDecisionProblem()));
		assertTrue(filteringManager.isElementMatchingFilterSettings(KnowledgeElements.getUnsolvedDecisionProblem()));
	}

	@Test
	public void testCompleteElementAndOnlyIncompleteElementsShouldBeShown() {
		filteringManager.getFilterSettings().setOnlyIncompleteKnowledgeShown(true);
		assertTrue(
				filteringManager.isElementMatchingDocumentationCompletenessFilter(KnowledgeElements.getDecision()));
		assertFalse(filteringManager.isElementMatchingFilterSettings(KnowledgeElements.getDecision()));
	}
}