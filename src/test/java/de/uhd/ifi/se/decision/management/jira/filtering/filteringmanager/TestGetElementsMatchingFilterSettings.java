package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetElementsMatchingFilterSettings extends TestSetUp {

	private ApplicationUser user;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElement("TEST-1");
	}

	@Test
	public void testFilterSettingsNull() {
		FilteringManager filteringManager = new FilteringManager(user, null);
		assertEquals(0, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testGraphNull() {
		FilteringManager filteringManager = new FilteringManager(user, new FilterSettings(null, ""));
		assertEquals(0, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testNoSelectedElement() {
		filterSettings.setSelectedElement((KnowledgeElement) null);
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		assertEquals(JiraIssues.getTestJiraIssueCount(), filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testLinkDistanceZero() {
		filterSettings.setLinkDistance(0);
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		assertEquals(1, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testLinkDistanceOne() {
		filterSettings.setLinkDistance(1);
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		assertEquals(6, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testLinkDistanceTwo() {
		filterSettings.setLinkDistance(2);
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		assertEquals(10, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testGetElementsByType() {
		Set<String> knowledgeTypes = new HashSet<>();
		knowledgeTypes.add("Decision");
		filterSettings.setKnowledgeTypes(knowledgeTypes);

		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		assertEquals(2, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testGetElementsBySearchTerm() {
		filterSettings.setSearchTerm("Implement feature");
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		assertEquals(1, filteringManager.getElementsMatchingFilterSettings().size());
	}
}