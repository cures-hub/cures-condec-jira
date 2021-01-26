package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueLinks;
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
		assertNull(filteringManager.getSubgraphMatchingFilterSettings());
	}

	@Test
	public void testConstructorWithFilterSettingsInvalid() {
		FilteringManager filteringManager = new FilteringManager(null);
		assertNull(filteringManager.getFilterSettings());
	}

	@Test
	public void testConstructorValidQueryEmpty() {
		FilteringManager filteringManager = new FilteringManager("TEST", user, "");
		assertEquals(JiraIssues.getTestJiraIssueCount(), filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	public void testConstructorWithFilterSettingsValidQueryEmpty() {
		FilteringManager manager = new FilteringManager(user, new FilterSettings("TEST", ""));
		// can be > 10 because of code classes in the graph
		assertTrue(manager.getElementsMatchingFilterSettings().size() >= 10);
	}

	@Test
	public void testConstructorValidQueryFilter() {
		FilteringManager filteringManager = new FilteringManager("TEST", user, "?filter=allopenissues");
		assertEquals(JiraIssues.getTestJiraIssueCount(), filteringManager.getElementsMatchingFilterSettings().size());
	}

	@Test
	// TODO
	public void testConstructorValidQueryJQL() {
		FilteringManager filteringManager = new FilteringManager("TEST", user, "?jql=project=TEST");
		assertEquals("?jql=project=TEST", filteringManager.getFilterSettings().getSearchTerm());
		assertEquals(JiraIssues.getTestJiraIssueCount(), filteringManager.getElementsMatchingFilterSettings().size());
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
		Set<String> knowledgeTypes = new HashSet<>();
		knowledgeTypes.add("Decision");
		FilterSettings settings = new FilterSettings("TEST", "TEST");
		settings.setKnowledgeTypes(knowledgeTypes);

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
		FilterSettings settings = new FilterSettings("TEST", "TEST");

		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertEquals(JiraIssues.getTestJiraIssueCount(), filteringManager.getSubgraphMatchingFilterSettings().vertexSet().size());
		// Currently, the mock links all have the "relate" type.
		assertEquals(JiraIssueLinks.getTestJiraIssueLinkCount(), filteringManager.getSubgraphMatchingFilterSettings().edgeSet().size());
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
		assertEquals(10, filteringManager.getSubgraphMatchingFilterSettings().vertexSet().size());
	}

	@Test
	public void testGetSubgraphForLinkDistanceThree() {
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement("TEST-1");
		settings.setLinkDistance(3);

		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertEquals(10, filteringManager.getSubgraphMatchingFilterSettings().vertexSet().size());
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
		settings.setIrrelevantTextShown(true);
		// Add irrelevant sentence
		JiraIssues.getSentencesForCommentText("Irrelevant text");
		JiraIssues.addElementToDataBase(1, KnowledgeType.OTHER);

		FilteringManager filteringManager = new FilteringManager(user, settings);
		assertTrue(filteringManager.getSubgraphMatchingFilterSettings().vertexSet().size() > 0);
	}

	@Test
	public void testTransitiveLinks() {
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement("TEST-31");
		settings.setKnowledgeTypes(new HashSet<String>(Arrays.asList("Issue", "Argument", "Pro", "Con")));
		settings.setCreateTransitiveLinks(true);
		FilteringManager filteringManager = new FilteringManager(user, settings);
		Graph<KnowledgeElement, Link> subgraph = filteringManager.getSubgraphMatchingFilterSettings();
		Set<Link> transitiveLinks = new HashSet<Link>();
		transitiveLinks.addAll(subgraph.edgeSet());
		transitiveLinks.removeIf(link -> link.getType() != LinkType.TRANSITIVE);
		assertTrue(transitiveLinks.size() == 4);

		Set<Link> otherLinks1 = new HashSet<Link>();
		otherLinks1.addAll(subgraph.edgeSet());
		otherLinks1.removeIf(link -> link.getType() == LinkType.TRANSITIVE);

		settings.setCreateTransitiveLinks(false);
		filteringManager = new FilteringManager(user, settings);
		subgraph = filteringManager.getSubgraphMatchingFilterSettings();
		Set<Link> noTransitiveLinks = new HashSet<Link>();
		noTransitiveLinks.addAll(subgraph.edgeSet());
		noTransitiveLinks.removeIf(link -> link.getType() != LinkType.TRANSITIVE);
		assertTrue(noTransitiveLinks.size() == 0);

		Set<Link> otherLinks2 = new HashSet<Link>();
		otherLinks2.addAll(subgraph.edgeSet());
		otherLinks2.removeIf(link -> link.getType() == LinkType.TRANSITIVE);
		assertEquals(otherLinks1, otherLinks2);
	}

}