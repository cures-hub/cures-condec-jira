package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestFilterSettings extends TestSetUpWithIssues {
	private FilterSettings filterSettings;
	private String[] knowledgeTypesStringArray;
	private String[] doc;
	private String searchString;
	private long createDate;

	@Before
	public void setUp() {
		initialization();
		createDate = System.currentTimeMillis();
		searchString = "project%20%3D%20CONDEC%20AND%20assignee%20%3D%20currentUser()%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20updated%20DESC";
		filterSettings = new FilterSettingsImpl("TEST", searchString, createDate - 100, createDate);
		knowledgeTypesStringArray = new String[KnowledgeType.toList().size()];
		List<String> typeList = KnowledgeType.toList();
		for (int i = 0; i < typeList.size(); i++) {
			knowledgeTypesStringArray[i] = typeList.get(i);
		}
		doc = new String[DocumentationLocation.getNamesOfDocumentationLocations().size()];
		List<String> docList = DocumentationLocation.getNamesOfDocumentationLocations();
		for (int i = 0; i < docList.size(); i++) {
			doc[i] = docList.get(i);
		}
		filterSettings.setNamesOfSelectedJiraIssueTypesAsArray(knowledgeTypesStringArray);
		filterSettings.setDocumentationLocations(doc);
	}
	
	@Test
	public void testKeySearchConstructor() {
		FilterSettings data = new FilterSettingsImpl("TEST", "search string");
		assertNotNull(data);
	}

	@Test
	public void testKeySearchTimeConstructor() {
		FilterSettings data = new FilterSettingsImpl("TEST", "search string", System.currentTimeMillis() - 100,
				System.currentTimeMillis());
		assertNotNull(data);
	}

	@Test
	public void testKeySearchTimeLocationConstructor() {
		FilterSettings data = new FilterSettingsImpl("TEST", "search string", System.currentTimeMillis() - 100,
				System.currentTimeMillis(), doc);
		assertNotNull(data);
	}

	@Test
	public void testKeySearchTimeLocationTypeConstructor() {
		FilterSettings data = new FilterSettingsImpl("TEST", "search string", System.currentTimeMillis() - 100,
				System.currentTimeMillis(), doc, knowledgeTypesStringArray);
		assertNotNull(data);
	}

	@Test
	public void testGetProjectKey() {
		assertEquals(filterSettings.getProjectKey(), "TEST");
	}

	@Test
	public void testSetProjectKey() {
		filterSettings.setProjectKey("NEWTEST");
		assertEquals(filterSettings.getProjectKey(), "NEWTEST");
	}

	@Test
	public void testGetSearchString() {
		assertEquals(filterSettings.getSearchString(), searchString);
	}

	@Test
	public void testSetSearchString() {
		filterSettings.setSearchString(filterSettings.getSearchString() + "TEST ENDING");
		assertEquals(filterSettings.getSearchString(), searchString + "TEST ENDING");
	}

	@Test
	public void testGetCreatedEarliest() {
		assertEquals(createDate - 100, filterSettings.getCreatedEarliest(), 0.0);
	}

	@Test
	public void testSetCreatedEarliest() {
		filterSettings.setCreatedEarliest(createDate - 50);
		assertEquals(createDate - 50, filterSettings.getCreatedEarliest(), 0.0);
	}

	@Test
	public void testGetCreatedLatest() {
		assertEquals(createDate, filterSettings.getCreatedLatest(), 0.0);
	}

	@Test
	public void testSetCreatedLatest() {
		filterSettings.setCreatedLatest(createDate + 10);
		assertEquals(createDate + 10, filterSettings.getCreatedLatest(), 0.0);
	}

	@Test
	public void testGetDocumentationLocation() {
		for (String location : doc) {
			assertTrue(filterSettings.getDocumentationLocations()
					.contains(DocumentationLocation.getDocumentationLocationFromString(location)));
		}
	}

	@Test
	public void testSetDocumentationLocation() {
		String[] newLocations = new String[2];
		int position = 0;
		for (String location : DocumentationLocation.getNamesOfDocumentationLocations()) {
			if (position < 2) {
				newLocations[position] = location;
			} else {
				break;
			}
			position++;
		}
		filterSettings.setNamesOfSelectedJiraIssueTypesAsArray(newLocations);
		for (String location : newLocations) {
			assertTrue(filterSettings.getDocumentationLocations()
					.contains(DocumentationLocation.getDocumentationLocationFromString(location)));
		}
	}

	@Test
	public void testGetIssueTypes() {
		assertEquals(18, filterSettings.getNamesOfSelectedJiraIssueTypes().size());
		assertTrue(filterSettings.getNamesOfSelectedJiraIssueTypes().contains("Decision"));
	}

	@Test
	public void testSetIssueTypes() {
		String[] newIssueTypes = new String[3];
		int position = 0;
		for (String type : KnowledgeType.toList()) {
			if (position < 3) {
				newIssueTypes[position] = type;
			} else {
				break;
			}
			position++;
		}
		filterSettings.setNamesOfSelectedJiraIssueTypesAsArray(newIssueTypes);
		for (String type : newIssueTypes) {
			assertTrue(filterSettings.getNamesOfSelectedJiraIssueTypes().contains(KnowledgeType.getKnowledgeType(type).toString()));
		}
	}
}
