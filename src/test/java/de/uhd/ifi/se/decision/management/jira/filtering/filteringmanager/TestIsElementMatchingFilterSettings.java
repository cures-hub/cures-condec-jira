package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestIsElementMatchingFilterSettings extends TestSetUp {

	private FilteringManager filteringManager;

	@Before
	public void setUp() {
		init();
		FilterSettings settings = new FilterSettings("TEST", "");
		filteringManager = new FilteringManager(settings);
	}

	@Test
	public void testIsElementMatchingDegreeFilter() {
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		assertTrue(filteringManager.isElementMatchingDegreeFilter(element));

		filteringManager.getFilterSettings().setMinDegree(20);
		assertFalse(filteringManager.isElementMatchingDegreeFilter(element));
	}

	@Test
	public void testIsElementMatchingStatusFilter() {
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		assertTrue(filteringManager.isElementMatchingStatusFilter(element));

		filteringManager.getFilterSettings().setStatus(new ArrayList<>());
		assertFalse(filteringManager.isElementMatchingStatusFilter(element));
	}

	@Test
	public void testIsElementMatchingDocumentationCompletenessFilter() {
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();

		filteringManager.getFilterSettings().setIncompleteKnowledgeShown(false);
		assertFalse(filteringManager.isElementMatchingDocumentationIncompletenessFilter(element));

		filteringManager.getFilterSettings().setIncompleteKnowledgeShown(true);
		assertFalse(filteringManager.isElementMatchingDocumentationIncompletenessFilter(element));
	}

	@Test
	public void testIsElementMatchingTimeFilter() {
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		filteringManager.getFilterSettings().setStartDate(1);
		filteringManager.getFilterSettings().setEndDate(new Date().getTime());

		assertTrue(filteringManager.isElementMatchingTimeFilter(element));
	}

	@Test
	@NonTransactional
	public void testIsIrrelevantTextShownFilter() {
		filteringManager.getFilterSettings().setOnlyDecisionKnowledgeShown(true);
		assertTrue(filteringManager.isElementMatchingKnowledgeTypeFilter(KnowledgeElements.getDecision()));
		assertFalse(filteringManager.isElementMatchingKnowledgeTypeFilter(KnowledgeElements.getOtherWorkItem()));
		assertFalse(filteringManager.isElementMatchingKnowledgeTypeFilter(KnowledgeElements.getCodeFile()));
	}

	@Test
	@NonTransactional
	public void testIsElementMatchingKnowledgeTypeFilter() {
		filteringManager.getFilterSettings().setIrrelevantTextShown(true);
		// Add irrelevant sentence
		JiraIssues.getSentencesForCommentText("Irrelevant text");
		JiraIssues.addElementToDataBase(1, KnowledgeType.OTHER);

		assertTrue(filteringManager.getFilteredGraph().vertexSet().size() > 0);
	}
}