package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestFilteringManager extends TestSetUp {

	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	public void testConstructorWithQueryInvalid() {
		FilteringManager filteringManager = new FilteringManager(null, null, (String) null);
		assertNull(filteringManager.getFilterSettings());
		assertEquals(0, filteringManager.getAllElementsMatchingFilterSettings().size());
		assertNull(filteringManager.getSubgraphMatchingFilterSettings());
	}

	@Test
	public void testConstructorWithFilterSettingsInvalid() {
		FilteringManager filteringManager = new FilteringManager(null, (FilterSettings) null);
		assertNull(filteringManager.getFilterSettings());
	}

	@Test
	public void testConstructorValidQueryEmpty() {
		FilteringManager filteringManager = new FilteringManager("TEST", user, "");
		assertEquals(8, filteringManager.getAllElementsMatchingFilterSettings().size());
	}

	@Test
	public void testConstructorWithFilterSettingsValidQueryEmpty() {
		FilteringManager extractor = new FilteringManager(user, new FilterSettings("TEST", ""));
		assertEquals(8, extractor.getAllElementsMatchingFilterSettings().size());
	}

	@Test
	public void testConstructorValidQueryFilter() {
		FilteringManager filteringManager = new FilteringManager("TEST", user, "?filter=allopenissues");
		assertEquals(8, filteringManager.getAllElementsMatchingFilterSettings().size());
	}

	@Test
	// TODO
	public void testConstructorValidQueryJQL() {
		FilteringManager filteringManager = new FilteringManager("TEST", user, "?jql=project=TEST");
		assertEquals("?jql=project=TEST", filteringManager.getFilterSettings().getSearchString());
		assertEquals(8, filteringManager.getAllElementsMatchingFilterSettings().size());
	}

	@Test
	public void testSetFilterSettings() {
		FilteringManager filteringManager = new FilteringManager(user, null);
		FilterSettings settings = new FilterSettings("TEST", "TEST");
		filteringManager.setFilterSettings(settings);
		assertEquals("TEST", filteringManager.getFilterSettings().getSearchString());
	}

	public void testFilterSettingsEmpty() {
		FilteringManager filteringManager = new FilteringManager(user, new FilterSettings(null, null));
		assertEquals(0, filteringManager.getAllElementsMatchingFilterSettings().size());
	}

	@Test
	public void testGetElementsByType() {
		List<String> knowledgeTypes = new ArrayList<String>();
		knowledgeTypes.add("Decision");
		FilterSettings settings = new FilterSettings("TEST", "TEST");
		settings.setSelectedJiraIssueTypes(knowledgeTypes);

		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertEquals(1, filteringManager.getAllElementsMatchingFilterSettings().size());
		assertEquals(1, filteringManager.getSubgraphMatchingFilterSettings().vertexSet().size());
	}

	@Test
	public void testGetElementsBySubstring() {
		FilterSettings settings = new FilterSettings("TEST", "Implement feature");
		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertEquals(1, filteringManager.getAllElementsMatchingFilterSettings().size());
		assertEquals(1, filteringManager.getSubgraphMatchingFilterSettings().vertexSet().size());
	}

	@Test
	public void testGetSubgraph() {
		List<String> linkTypes = new ArrayList<String>();
		linkTypes.add("support");
		FilterSettings settings = new FilterSettings("TEST", "TEST");
		settings.setSelectedLinkTypes(linkTypes);

		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertEquals(8, filteringManager.getSubgraphMatchingFilterSettings().vertexSet().size());
		// Currently, the mock links all have the "relate" type.
		assertEquals(0, filteringManager.getSubgraphMatchingFilterSettings().edgeSet().size());
	}
}