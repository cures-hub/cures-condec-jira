package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.impl.FilterSettingsImpl;

public class TestFilterSettings extends TestSetUpWithIssues {
	private FilterSettings filterData;
	private String[] knowledgeTypesStringArray;
	private String[] doc;
	private String searchString;
	private long createDate;

	@Before
	public void setUp() {
		initialization();
		createDate = System.currentTimeMillis();
		searchString = "project%20%3D%20CONDEC%20AND%20assignee%20%3D%20currentUser()%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20updated%20DESC";
		filterData = new FilterSettingsImpl("TEST", searchString, createDate - 100, createDate);
		knowledgeTypesStringArray = new String[KnowledgeType.toList().size()];
		List<String> typeList = KnowledgeType.toList();
		for (int i = 0; i < typeList.size(); i++) {
			knowledgeTypesStringArray[i] = typeList.get(i);
		}
		doc = new String[DocumentationLocation.toList().size()];
		List<String> docList = DocumentationLocation.toList();
		for (int i = 0; i < docList.size(); i++) {
			doc[i] = docList.get(i);
		}
		filterData.setIssueTypes(knowledgeTypesStringArray);
		filterData.setDocumentationLocations(doc);
	}

	@Test
	public void testEmptyConstructor() {
		assertNotNull(new FilterSettingsImpl());
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
		assertEquals(filterData.getProjectKey(), "TEST");
	}

	@Test
	public void testSetProjectKey() {
		filterData.setProjectKey("NEWTEST");
		assertEquals(filterData.getProjectKey(), "NEWTEST");
	}

	@Test
	public void testGetSearchString() {
		assertEquals(filterData.getSearchString(), searchString);
	}

	@Test
	public void testSetSearchString() {
		filterData.setSearchString(filterData.getSearchString() + "TEST ENDING");
		assertEquals(filterData.getSearchString(), searchString + "TEST ENDING");
	}

	@Test
	public void testGetCreatedEarliest() {
		assertEquals(createDate - 100, filterData.getCreatedEarliest(), 0.0);
	}

	@Test
	public void testSetCreatedEarliest() {
		filterData.setCreatedEarliest(createDate - 50);
		assertEquals(createDate - 50, filterData.getCreatedEarliest(), 0.0);
	}

	@Test
	public void testGetCreatedLatest() {
		assertEquals(createDate, filterData.getCreatedLatest(), 0.0);
	}

	@Test
	public void testSetCreatedLatest() {
		filterData.setCreatedLatest(createDate + 10);
		assertEquals(createDate + 10, filterData.getCreatedLatest(), 0.0);
	}

	@Test
	public void testGetDocumentationLocation() {
		for (String location : doc) {
			assertTrue(filterData.getDocumentationLocations()
					.contains(DocumentationLocation.getDocumentationLocationFromString(location)));
		}
	}

	@Test
	public void testSetDocumentationLocation() {
		String[] newLocations = new String[2];
		int position = 0;
		for (String location : DocumentationLocation.toList()) {
			if (position < 2) {
				newLocations[position] = location;
			} else {
				break;
			}
			position++;
		}
		filterData.setIssueTypes(newLocations);
		for (String location : newLocations) {
			assertTrue(filterData.getDocumentationLocations()
					.contains(DocumentationLocation.getDocumentationLocationFromString(location)));
		}
	}

	@Test
	public void testGetIssueTypes() {
		for (String type : knowledgeTypesStringArray) {
			assertTrue(filterData.getIssueTypes().contains(KnowledgeType.getKnowledgeType(type)));
		}
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
		filterData.setIssueTypes(newIssueTypes);
		for (String type : newIssueTypes) {
			assertTrue(filterData.getIssueTypes().contains(KnowledgeType.getKnowledgeType(type)));
		}
	}
}
