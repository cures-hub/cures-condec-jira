package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

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
		assertEquals(7, filterSettings.getKnowledgeTypes().size());
		filterSettings = new FilterSettings("TEST", "?jql=issuetype in (Decision, Issue)", null);
		assertEquals(2, filterSettings.getKnowledgeTypes().size());
	}

	@Test
	public void testSetJiraIssueTypes() {
		filterSettings.setKnowledgeTypes(null);
		assertEquals(7, filterSettings.getKnowledgeTypes().size());
	}

	@Test
	public void testGetNamesOfLinkTypes() {
		assertEquals(11, filterSettings.getLinkTypes().size());
		List<String> selectedLinkTypes = new ArrayList<>();
		selectedLinkTypes.add("Forbids");
		selectedLinkTypes.add("Relates");
		filterSettings.setLinkTypes(selectedLinkTypes);
		assertEquals(2, filterSettings.getLinkTypes().size());
	}

	@Test
	public void testSetLinkTypes() {
		filterSettings.setKnowledgeTypes(null);
		assertEquals(11, filterSettings.getLinkTypes().size());
	}

	@Test
	public void testGetStatus() {
		assertEquals(8, filterSettings.getStatus().size());
	}

	@Test
	public void testSetStatus() {
		filterSettings.setStatus(null);
		assertEquals(8, filterSettings.getStatus().size());

		List<String> status = new ArrayList<String>();
		status.add(KnowledgeStatus.UNRESOLVED.toString());
		filterSettings.setStatus(status);
		assertEquals(1, filterSettings.getStatus().size());
	}

	@Test
	public void testIsIncompleteElementsShown() {
		assertTrue(filterSettings.isIncompleteKnowledgeShown());
	}

	@Test
	public void testSetIncompleteElementsShown() {
		filterSettings.setIncompleteKnowledgeShown(true);
		assertTrue(filterSettings.isIncompleteKnowledgeShown());
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

	@Test
	public void testGetIsOnlyDecisionKnowledgeShown() {
		assertEquals(false, filterSettings.isOnlyDecisionKnowledgeShown());
	}

	@Test
	public void testSetOnlyDecisionKnowledgeShown() {
		filterSettings.setOnlyDecisionKnowledgeShown(true);
		assertEquals(true, filterSettings.isOnlyDecisionKnowledgeShown());
	}

	@Test
	public void testIsTestCodeShown() {
		assertEquals(false, filterSettings.isTestCodeShown());
	}

	@Test
	public void testSetTestCodeShown() {
		filterSettings.setTestCodeShown(true);
		assertEquals(true, filterSettings.isTestCodeShown());
	}

	@Test
	public void testGetLinkDistance() {
		assertEquals(3, filterSettings.getLinkDistance());
	}

	@Test
	public void testSetLinkDistance() {
		filterSettings.setLinkDistance(2);
		assertEquals(2, filterSettings.getLinkDistance());
	}

	@Test
	public void testGetMinAndMaxDegree() {
		assertEquals(0, filterSettings.getMinDegree());
		assertEquals(50, filterSettings.getMaxDegree());
	}

	@Test
	public void testSetMinAndMaxDegree() {
		filterSettings.setMinDegree(5);
		filterSettings.setMaxDegree(10);
		assertEquals(5, filterSettings.getMinDegree());
		assertEquals(10, filterSettings.getMaxDegree());
	}

	@Test
	public void testGetSelectedElement() {
		assertEquals(null, filterSettings.getSelectedElement());
	}

	@Test
	public void testSetSelectedElement() {
		filterSettings.setSelectedElement("TEST-1");
		assertEquals("TEST-1", filterSettings.getSelectedElement().getKey());

		filterSettings.setSelectedElement(KnowledgeElements.getTestKnowledgeElement());
		assertEquals("TEST-1", filterSettings.getSelectedElement().getKey());

		filterSettings.setSelectedElement("TEST-1:123");
		assertNull(filterSettings.getSelectedElement());
	}

}
