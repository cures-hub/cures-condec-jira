package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestFilteringManager extends TestSetUp {

	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
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
		FilteringManager filteringManager = new FilteringManager(user, null);
		FilterSettings settings = new FilterSettings("TEST", "search term");
		filteringManager.setFilterSettings(settings);
		assertEquals("search term", filteringManager.getFilterSettings().getSearchTerm());
	}

	@Test
	public void testIsElementMatchingDecisionGroupFilter() {
		FilterSettings settings = new FilterSettings("TEST", "");
		List<String> decGroups = List.of("Low", "Medium", "High");
		settings.setDecisionGroups(decGroups);
		FilteringManager filteringManager = new FilteringManager(settings);
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		element.addDecisionGroup("Low");
		element.addDecisionGroup("Medium");
		element.addDecisionGroup("High");
		assertTrue(filteringManager.isElementMatchingDecisionGroupFilter(element));
	}

	@Test
	public void testIsElementMatchingDegreeFilter() {
		FilterSettings settings = new FilterSettings("TEST", "");
		FilteringManager filteringManager = new FilteringManager(settings);
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		assertTrue(filteringManager.isElementMatchingDegreeFilter(element));

		settings.setMinDegree(20);
		filteringManager.setFilterSettings(settings);
		assertFalse(filteringManager.isElementMatchingDegreeFilter(element));
	}

	@Test
	public void testIsElementMatchingStatusFilter() {
		FilterSettings settings = new FilterSettings("TEST", "");
		FilteringManager filteringManager = new FilteringManager(settings);
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		assertTrue(filteringManager.isElementMatchingStatusFilter(element));

		settings.setStatus(new ArrayList<>());
		filteringManager.setFilterSettings(settings);
		assertFalse(filteringManager.isElementMatchingStatusFilter(element));
	}

	@Test
	public void testIsElementMatchingDocumentationCompletenessFilter() {
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		FilterSettings settings = new FilterSettings("TEST", "");

		settings.setIncompleteKnowledgeShown(false);
		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertFalse(filteringManager.isElementMatchingDocumentationIncompletenessFilter(element));

		settings.setIncompleteKnowledgeShown(true);
		filteringManager.setFilterSettings(settings);
		assertFalse(filteringManager.isElementMatchingDocumentationIncompletenessFilter(element));
	}

	@Test
	public void testIsElementMatchingTimeFilter() {
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setStartDate(1);
		settings.setEndDate(new Date().getTime());

		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertTrue(filteringManager.isElementMatchingTimeFilter(element));
	}

	@Test
	@NonTransactional
	public void testIsIrrelevantTextShownFilter() {
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setOnlyDecisionKnowledgeShown(true);
		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertTrue(filteringManager.isElementMatchingKnowledgeTypeFilter(KnowledgeElements.getDecision()));
		assertFalse(filteringManager.isElementMatchingKnowledgeTypeFilter(KnowledgeElements.getOtherWorkItem()));
		assertFalse(filteringManager.isElementMatchingKnowledgeTypeFilter(KnowledgeElements.getCodeFile()));
	}

	@Test
	@NonTransactional
	public void testIsElementMatchingKnowledgeTypeFilter() {
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setIrrelevantTextShown(true);
		// Add irrelevant sentence
		JiraIssues.getSentencesForCommentText("Irrelevant text");
		JiraIssues.addElementToDataBase(1, KnowledgeType.OTHER);

		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertTrue(filteringManager.getFilteredGraph().vertexSet().size() > 0);
	}
}