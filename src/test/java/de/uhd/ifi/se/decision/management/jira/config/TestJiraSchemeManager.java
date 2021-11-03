package de.uhd.ifi.se.decision.management.jira.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.issuetype.IssueType;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueTypes;

public class TestJiraSchemeManager extends TestSetUp {

	private JiraSchemeManager jiraSchemeManager;

	@Before
	public void setUp() {
		init();
		jiraSchemeManager = new JiraSchemeManager("TEST");
	}

	@Test
	public void testCreateIssueTypeAlreadyExisting() {
		assertEquals("Decision", JiraSchemeManager.createIssueType("Decision").getName());
	}

	@Test
	public void testCreateIssueTypeNew() {
		assertEquals("Brand new issue type", JiraSchemeManager.createIssueType("Brand new issue type").getName());
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeNull() {
		assertFalse(jiraSchemeManager.addIssueTypeToScheme(null));
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeFilled() {
		assertTrue(jiraSchemeManager.addIssueTypeToScheme(JiraIssueTypes.getTestTypes().get(0)));
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeNull() {
		assertFalse(jiraSchemeManager.removeIssueTypeFromScheme(null));
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeFilled() {
		assertTrue(jiraSchemeManager.removeIssueTypeFromScheme("Decision"));
	}

	@Test
	public void testRemoveLinkTypeFromSchemeLinkTypeNull() {
		assertFalse(jiraSchemeManager.removeLinkTypeFromScheme(null));
	}

	@Test
	public void testRemoveLinkTypeFromSchemeLinkTypeFilled() {
		assertTrue(jiraSchemeManager.removeLinkTypeFromScheme("Decision"));
	}

	@Test
	public void testAddLinkTypeToSchemeLinkTypeNull() {
		assertFalse(jiraSchemeManager.addLinkTypeToScheme(null));
	}

	@Test
	public void testAddLinkTypeToSchemeLinkTypeFilled() {
		assertTrue(jiraSchemeManager.addLinkTypeToScheme("Decision"));
	}

	@Test
	public void testGetIconUrl() {
		assertEquals(
				"null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/decision.png",
				JiraSchemeManager.getIconUrl("decision"));
	}

	@Test
	public void testGetJiraIssueTypesZero() {
		assertEquals(7, JiraSchemeManager.getJiraIssueTypes(1).size());
	}

	@Test
	public void testGetJiraIssueTypesByProjectKeyValid() {
		assertEquals(7, jiraSchemeManager.getJiraIssueTypes().size());
	}

	@Test
	public void testGetJiraIssueTypesByProjectKeyInvalid() {
		assertEquals(0, new JiraSchemeManager(null).getJiraIssueTypes().size());
	}

	@Test
	public void testGetJiraIssueTypesOk() {
		assertEquals(7, JiraSchemeManager.getJiraIssueTypes(1).size());
	}

	@Test
	public void testGetJiraIssueTypeNameNull() {
		assertEquals("", JiraSchemeManager.getJiraIssueTypeName(null));
	}

	@Test
	public void testGetJiraIssueTypeNameEmpty() {
		assertEquals("", JiraSchemeManager.getJiraIssueTypeName(""));
	}

	@Test
	public void testGetJiraIssueTypeNameFilled() {
		Collection<IssueType> issueTypes = JiraSchemeManager.getJiraIssueTypes(1);
		for (IssueType type : issueTypes) {
			IssueType issueType = ComponentAccessor.getConstantsManager().getIssueType(type.getId());
			assertEquals(issueType.getName(), JiraSchemeManager.getJiraIssueTypeName(type.getId()));
		}
	}

}