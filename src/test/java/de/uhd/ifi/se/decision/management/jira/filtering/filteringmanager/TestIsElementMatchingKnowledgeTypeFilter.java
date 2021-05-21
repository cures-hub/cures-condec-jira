package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

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

public class TestIsElementMatchingKnowledgeTypeFilter extends TestSetUp {

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
	public void testAllKnowledgeTypesInFilter() {
		assertTrue(filteringManager.isElementMatchingKnowledgeTypeFilter(element));
		assertTrue(filteringManager.isElementMatchingFilterSettings(element));
	}

	@Test
	public void testNoStatusInFilter() {
		filteringManager.getFilterSettings().setKnowledgeTypes(new HashSet<>());
		assertFalse(filteringManager.isElementMatchingKnowledgeTypeFilter(element));
		assertFalse(filteringManager.isElementMatchingFilterSettings(element));
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