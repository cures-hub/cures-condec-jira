package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

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
		assertEquals(0, filteringManager.getElementsMatchingFilterSettings().size());
		assertNull(filteringManager.getSubgraphMatchingFilterSettings());
	}

	@Test
	public void testConstructorWithFilterSettingsInvalid() {
		FilteringManager filteringManager = new FilteringManager((FilterSettings) null);
		assertNull(filteringManager.getFilterSettings());
	}

	@Test
	public void testConstructorValidQueryEmpty() {
		FilteringManager filteringManager = new FilteringManager("TEST", user, "");
		assertEquals(9, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testConstructorWithFilterSettingsValidQueryEmpty() {
		FilteringManager extractor = new FilteringManager(user, new FilterSettings("TEST", ""));
		assertEquals(9, extractor.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testConstructorValidQueryFilter() {
		FilteringManager filteringManager = new FilteringManager("TEST", user, "?filter=allopenissues");
		assertEquals(9, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	// TODO
	public void testConstructorValidQueryJQL() {
		FilteringManager filteringManager = new FilteringManager("TEST", user, "?jql=project=TEST");
		assertEquals("?jql=project=TEST", filteringManager.getFilterSettings().getSearchTerm());
		assertEquals(9, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testSetFilterSettings() {
		FilteringManager filteringManager = new FilteringManager(user, null);
		FilterSettings settings = new FilterSettings("TEST", "TEST");
		filteringManager.setFilterSettings(settings);
		assertEquals("TEST", filteringManager.getFilterSettings().getSearchTerm());
	}

	@Test
	public void testFilterSettingsEmpty() {
		FilteringManager filteringManager = new FilteringManager(user, new FilterSettings(null, null));
		assertEquals(0, filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testGetElementsByType() {
		Set<String> knowledgeTypes = new HashSet<String>();
		knowledgeTypes.add("Decision");
		FilterSettings settings = new FilterSettings("TEST", "TEST");
		settings.setJiraIssueTypes(knowledgeTypes);

		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertEquals(1, filteringManager.getElementsMatchingFilterSettings().size());
		assertEquals(1, filteringManager.getSubgraphMatchingFilterSettings().vertexSet().size());
	}

	@Test
	public void testGetElementsBySubstring() {
		FilterSettings settings = new FilterSettings("TEST", "Implement feature");
		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertEquals(1, filteringManager.getElementsMatchingFilterSettings().size());
		assertEquals(1, filteringManager.getSubgraphMatchingFilterSettings().vertexSet().size());
	}

	@Test
	public void testGetSubgraph() {
		List<String> linkTypes = new ArrayList<String>();
		linkTypes.add("support");
		FilterSettings settings = new FilterSettings("TEST", "TEST");
		settings.setLinkTypes(linkTypes);

		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertEquals(9, filteringManager.getSubgraphMatchingFilterSettings().vertexSet().size());
		// Currently, the mock links all have the "relate" type.
		assertEquals(0, filteringManager.getSubgraphMatchingFilterSettings().edgeSet().size());
	}

	@Test
	public void testGetSubgraphForLinkDistanceZero() {
		FilterSettings settings = new FilterSettings("TEST", "TEST");
		settings.setSelectedElement("TEST-1");
		settings.setLinkDistance(0);

		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertEquals(1, filteringManager.getSubgraphMatchingFilterSettings().vertexSet().size());
		assertEquals(0, filteringManager.getSubgraphMatchingFilterSettings().edgeSet().size());
	}

	@Test
	public void testGetSubgraphForLinkDistanceOne() {
		FilterSettings settings = new FilterSettings("TEST", "TEST");
		settings.setSelectedElement("TEST-1");
		settings.setLinkDistance(1);

		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertEquals(6, filteringManager.getSubgraphMatchingFilterSettings().vertexSet().size());
	}

	@Test
	public void testGetSubgraphForLinkDistanceTwo() {
		FilterSettings settings = new FilterSettings("TEST", "TEST");
		settings.setSelectedElement("TEST-1");
		settings.setLinkDistance(2);

		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertEquals(8, filteringManager.getSubgraphMatchingFilterSettings().vertexSet().size());
	}

	@Test
	public void testGetSubgraphForLinkDistanceThree() {
		FilterSettings settings = new FilterSettings("TEST", "TEST");
		settings.setSelectedElement("TEST-1");
		settings.setLinkDistance(3);

		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertEquals(9, filteringManager.getSubgraphMatchingFilterSettings().vertexSet().size());
	}

	@Test
	public void testIsElementMatchingDegreeFilter() {
		FilteringManager filteringManager = new FilteringManager(user, new FilterSettings("TEST", ""));
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		assertTrue(filteringManager.isElementMatchingDegreeFilter(element));
	}
}