package de.uhd.ifi.se.decision.management.jira.config;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;

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

}