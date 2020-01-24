package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilteringManagerImpl;
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
		FilteringManager filteringManager = new FilteringManagerImpl(null, null, (String) null);
		assertNull(filteringManager.getFilterSettings());
		assertEquals(0, filteringManager.getAllElementsMatchingFilterSettings().size());
	}

	@Test
	public void testConstructorWithFilterSettingsInvalid() {
		FilteringManager filteringManager = new FilteringManagerImpl(null, (FilterSettings) null);
		assertNull(filteringManager.getFilterSettings());
	}

	@Test
	public void testConstructorValidQueryEmpty() {
		FilteringManager filteringManager = new FilteringManagerImpl("TEST", user, "");
		assertEquals(8, filteringManager.getAllElementsMatchingFilterSettings().size());
	}

	@Test
	public void testConstructorWithFilterSettingsValidQueryEmpty() {
		FilteringManager extractor = new FilteringManagerImpl(user, new FilterSettingsImpl("TEST", ""));
		assertEquals(8, extractor.getAllElementsMatchingFilterSettings().size());
	}

	@Test
	public void testConstructorValidQueryFilter() {
		FilteringManager filteringManager = new FilteringManagerImpl("TEST", user, "?filter=allopenissues");
		assertEquals(8, filteringManager.getAllElementsMatchingFilterSettings().size());
	}

	@Test
	// TODO
	public void testConstructorValidQueryJQL() {
		FilteringManager filteringManager = new FilteringManagerImpl("TEST", user, "?jql=project=TEST");
		assertEquals("?jql=project=TEST", filteringManager.getFilterSettings().getSearchString());
		assertEquals(8, filteringManager.getAllElementsMatchingFilterSettings().size());
	}

	@Test
	public void testSetFilterSettings() {
		FilteringManager filteringManager = new FilteringManagerImpl(user, null);
		FilterSettings settings = new FilterSettingsImpl("TEST", "TEST");
		filteringManager.setFilterSettings(settings);
		assertEquals("TEST", filteringManager.getFilterSettings().getSearchString());
	}

	@Test(expected = NullPointerException.class)
	public void testFilterSettingsEmpty() {
		FilteringManager filteringManager = new FilteringManagerImpl(user, new FilterSettingsImpl());
		assertEquals(0, filteringManager.getAllElementsMatchingFilterSettings().size());
	}

	@Test
	public void testGetElementsByType() {
		List<String> knowledgeTypes = new ArrayList<String>();
		knowledgeTypes.add("Decision");
		FilterSettings settings = new FilterSettingsImpl("TEST", "TEST");
		settings.setSelectedJiraIssueTypes(knowledgeTypes);

		FilteringManager filteringManager = new FilteringManagerImpl(user, settings);
		assertEquals(1, filteringManager.getAllElementsMatchingFilterSettings().size());
	}

	@Test
	public void testGetElementsBySubstring() {
		FilterSettings settings = new FilterSettingsImpl("TEST", "Implement feature");
		FilteringManager filteringManager = new FilteringManagerImpl(user, settings);
		assertEquals(1, filteringManager.getAllElementsMatchingFilterSettings().size());
	}
}