package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;

public class TestFilterSettings extends TestSetUp {
	private FilterSettings filterSettings;
	private long createDate;

	@Before
	public void setUp() {
		init();
		createDate = -1;
		filterSettings = new FilterSettings("TEST", "?jql=project%20%3D%20CONDEC", null);
	}

	@Test
	public void testKeySearchConstructor() {
		assertNotNull(new FilterSettings("TEST", "search term"));
	}

	@Test
	public void testUnknownProject() {
		FilterSettings settings = new FilterSettings(null, "");
		assertNotNull(settings);
		assertEquals("", settings.getProjectKey());
	}

	@Test
	public void testGetProjectKey() {
		assertEquals(filterSettings.getProjectKey(), "TEST");
	}

	@Test
	public void testSetProjectKey() {
		filterSettings.setProjectKey("TEST");
		assertEquals(filterSettings.getProjectKey(), "TEST");
	}

	@Test
	public void testGetSearchTerm() {
		assertEquals("?jql=project = CONDEC", filterSettings.getSearchTerm());
	}

	@Test
	public void testSetSearchTerm() {
		filterSettings.setSearchTerm(filterSettings.getSearchTerm() + "TEST ENDING");
		assertEquals("?jql=project = CONDEC" + "TEST ENDING", filterSettings.getSearchTerm());
		filterSettings.setSearchTerm(null);
		assertEquals("", filterSettings.getSearchTerm());
	}

	@Test
	public void testGetCreatedEarliest() {
		assertEquals(createDate, filterSettings.getCreatedEarliest());
	}

	@Test
	public void testSetCreatedEarliest() {
		filterSettings.setCreatedEarliest(createDate - 50);
		assertEquals(createDate - 50, filterSettings.getCreatedEarliest());
	}

	@Test
	public void testGetCreatedLatest() {
		assertEquals(createDate, filterSettings.getCreatedLatest());
	}

	@Test
	public void testSetCreatedLatest() {
		filterSettings.setCreatedLatest(createDate + 10);
		assertEquals(createDate + 10, filterSettings.getCreatedLatest());
	}

	@Test
	public void testGetDocumentationLocations() {
		assertEquals(4, filterSettings.getDocumentationLocations().size());
		assertEquals(4, filterSettings.getNamesOfDocumentationLocations().size());
	}

	@Test
	public void testSetDocumentationLocations() {
		List<String> documentationLocations = DocumentationLocation.getAllDocumentationLocations().stream()
				.map(DocumentationLocation::toString).collect(Collectors.toList());
		filterSettings.setDocumentationLocations(documentationLocations);
		assertEquals(4, filterSettings.getDocumentationLocations().size());
		filterSettings.setDocumentationLocations(null);
		assertEquals(4, filterSettings.getDocumentationLocations().size());
	}

	@Test
	public void testGetNamesOfJiraIssueTypes() {
		assertEquals(5, filterSettings.getJiraIssueTypes().size());
		filterSettings = new FilterSettings("TEST", "?jql=issuetype in (Decision, Issue)", null);
		assertEquals(2, filterSettings.getJiraIssueTypes().size());
	}

	@Test
	public void testSetJiraIssueTypes() {
		filterSettings.setJiraIssueTypes(null);
		assertEquals(5, filterSettings.getJiraIssueTypes().size());
	}

	@Test
	public void testGetNamesOfLinkTypes() {
		assertEquals(10, filterSettings.getLinkTypes().size());
		List<String> selectedLinkTypes = new ArrayList<>();
		selectedLinkTypes.add("Forbids");
		selectedLinkTypes.add("Relates");
		filterSettings.setLinkTypes(selectedLinkTypes);
		assertEquals(2, filterSettings.getLinkTypes().size());
	}

	@Test
	public void testSetLinkTypes() {
		filterSettings.setJiraIssueTypes(null);
		assertEquals(10, filterSettings.getLinkTypes().size());
	}

	@Test
	public void testGetStatus() {
		assertEquals(7, filterSettings.getStatus().size());
	}

	@Test
	public void testSetStatus() {
		filterSettings.setStatus(null);
		assertEquals(7, filterSettings.getStatus().size());

		List<String> status = new ArrayList<String>();
		status.add(KnowledgeStatus.UNRESOLVED.toString());
		filterSettings.setStatus(status);
		assertEquals(1, filterSettings.getStatus().size());
	}

	@Test
	public void testGetGroups() {
		assertEquals(0, filterSettings.getDecisionGroups().size());
	}

	@Test
	public void testSetGroups() {
		List<String> groups = new ArrayList<>();
		groups.add("High Level");
		filterSettings.setDecisionGroups(groups);
		assertEquals(1, filterSettings.getDecisionGroups().size());
	}
}
