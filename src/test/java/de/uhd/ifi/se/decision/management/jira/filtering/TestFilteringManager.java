package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilteringManagerImpl;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestFilteringManager extends TestSetUp {

	private ApplicationUser user;
	private FilterSettings settings;
	private List<DecisionKnowledgeElement> allDecisions;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		settings = new FilterSettingsImpl();
		allDecisions = new ArrayList<>();
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(244, "TEST", "", "Decision", "TEST",
				"TEST-244", "i", "decided");
		allDecisions.add(element);
		List<String> doculoco = new ArrayList<>();
		for (DocumentationLocation location : DocumentationLocation.getAllDocumentationLocations()) {
			doculoco.add(location.getName());
		}
		settings.setDocumentationLocations(doculoco);
		settings.setSearchString("TEST");
		settings.setCreatedEarliest(System.currentTimeMillis() - 10000);
		settings.setCreatedLatest(System.currentTimeMillis());
		settings.setProjectKey("TEST");
		settings.setSelectedJiraIssueTypes(KnowledgeType.toList());
		settings.setSelectedStatus(KnowledgeStatus.toList());
		settings.setSelectedLinkTypes(LinkType.toList());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringNullNullNull() {
		FilteringManager extractor = new FilteringManagerImpl(null, null, (String) null);
		assertNull(extractor.getFilterSettings());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringFilledNullNull() {
		FilteringManager extractor = new FilteringManagerImpl("TEST", null, (String) null);
		assertNull(extractor.getFilterSettings());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringNullFilledNull() {
		FilteringManager extractor = new FilteringManagerImpl(null, user, (String) null);
		assertNull(extractor.getFilterSettings());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringNullNullEmpty() {
		FilteringManager extractor = new FilteringManagerImpl(null, null, "");
		assertNull(extractor.getFilterSettings());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringNullNullFilledString() {
		String filter = "allopenissues";
		FilteringManager extractor = new FilteringManagerImpl(null, null, "\\?filter=(.)+" + filter);
		assertNull(extractor.getFilterSettings());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringNullNullFilledJQL() {
		FilteringManager extractor = new FilteringManagerImpl(null, null, "\\?filter=allissues?jql=project=TEST");
		assertNull(extractor.getFilterSettings());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringFilledFilledEmpty() {
		FilteringManager extractor = new FilteringManagerImpl("TEST", user, "");
		// the empty query will be changed to "allissues", i.e. "type != null"
		assertEquals(8, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringFilledFilledString() {
		FilteringManager extractor = new FilteringManagerImpl("TEST", user, "?filter=allopenissues");
		assertEquals(8, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterStringFilledFilledFilledJQL() {
		FilteringManager extractor = new FilteringManagerImpl("TEST", user, "?jql=project=TEST");
		assertEquals("?jql=project=TEST", extractor.getFilterSettings().getSearchString());
		assertEquals(8, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterOwnNullProject() {
		FilteringManager extractor = new FilteringManagerImpl(null, (FilterSettings) null);
		assertNull(extractor.getFilterSettings());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterOwnNullSearch() {
		FilteringManager extractor = new FilteringManagerImpl(user, (FilterSettings) null);
		assertNull(extractor.getFilterSettings());
	}

	@Test
	@NonTransactional
	public void testConstructorFilterOwnFilled() {
		FilteringManager extractor = new FilteringManagerImpl(user, new FilterSettingsImpl("TEST", ""));
		// the empty query will be changed to "allissues", i.e. "type != null"
		// no, it is changed to "type = null" currently!
		assertEquals(8, extractor.getAllElementsMatchingQuery().size());
	}

	@Test
	@NonTransactional
	public void testGetGraphsMatchingQueryEmpty() {
		FilteringManager extractor = new FilteringManagerImpl("TEST", user, "");
		// the empty query will be changed to "allissues", i.e. "type != null"
		// no, it is changed to "type = null" currently!
		assertEquals(1, extractor.getAllGraphs().size());
	}

	@Test
	@NonTransactional
	public void testGetGraphsMatchingQueryFilled() {
		FilteringManager extractor = new FilteringManagerImpl("TEST", user, "?jql=project=TEST");
		assertEquals(5, extractor.getAllGraphs().size());
	}

	@Test
	public void testGetFilterSettings() {
		FilteringManager extractor = new FilteringManagerImpl("Test", user, "?jql=project=TEST");
		assertEquals("?jql=project=TEST", extractor.getFilterSettings().getSearchString());
	}

	@Test
	public void testGetAllElementsMatchingCompareFilterSettingsEmpty() {
		FilterSettings newSettings = new FilterSettingsImpl();
		FilteringManager extractor = new FilteringManagerImpl(user, newSettings);
		assertEquals(0, extractor.getAllElementsMatchingFilterSettings().size(), 0.0);
	}

	@Test
	public void testGetAllElementsMatchingCompareFilterSettingsFilledCreated() {
		settings.setCreatedLatest(-1);
		settings.setCreatedEarliest(System.currentTimeMillis() - 100000);
		FilteringManager extractor = new FilteringManagerImpl(user, settings);
		assertEquals(5, extractor.getAllElementsMatchingFilterSettings().size());
	}

	@Test
	public void testGetAllElementsMatchingCompareFilterSettingsFilledClosed() {
		settings.setCreatedEarliest(-1);
		settings.setCreatedLatest(System.currentTimeMillis() + 1000);
		FilteringManager extractor = new FilteringManagerImpl(user, settings);
		assertEquals(5, extractor.getAllElementsMatchingFilterSettings().size(), 0.0);
	}

	@Test
	public void testGetAllElementsMatchingCompareFilterSettingsFilled() {
		settings.setCreatedLatest(System.currentTimeMillis() + 1000);
		settings.setCreatedEarliest(System.currentTimeMillis() - 100000);
		FilteringManager extractor = new FilteringManagerImpl(user, settings);
		assertEquals(5, extractor.getAllElementsMatchingFilterSettings().size());
	}

	@Test
	public void testGetAllElementsLinkTypeFilterMatches() {
		List<String> types = new ArrayList<String>();
		types.add("Decision");
		settings.setSelectedJiraIssueTypes(types);
		FilteringManager extractor = new FilteringManagerImpl(user, settings);
		assertEquals(allDecisions.size(), extractor.getAllElementsMatchingFilterSettings().size());
		settings.setSelectedJiraIssueTypes(KnowledgeType.toList());
	}

	@Test
	public void testGetAllElementsLinkTypeFilterMatchesSearchStringFilled() {
		settings.setSearchString("TEST123");
		FilteringManager extractor = new FilteringManagerImpl(user, settings);
		assertEquals(0, extractor.getAllElementsMatchingFilterSettings().size());
	}
}