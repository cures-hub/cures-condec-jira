package de.uhd.ifi.se.decision.management.jira.config;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueTypes;

public class TestPluginInitializer extends TestSetUp {

	@BeforeClass
	public static void setUpBeforeClass() {
		init();
	}

	@Test
	public void testCreateIssueTypeAlreadyExisting() {
		assertEquals("Decision", PluginInitializer.createIssueType("Decision").getName());
	}

	@Test
	public void testCreateIssueTypeNew() {
		assertEquals("Brand new issue type", PluginInitializer.createIssueType("Brand new issue type").getName());
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeNullProjectKeyNull() {
		assertFalse(PluginInitializer.addIssueTypeToScheme(null, null));
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeNullProjectKeyFilled() {
		assertFalse(PluginInitializer.addIssueTypeToScheme(null, "TEST"));
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeFilledProjectKeyNull() {
		assertFalse(PluginInitializer.addIssueTypeToScheme(JiraIssueTypes.getTestTypes().get(0), null));
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeFilledProjectKeyFilled() {
		assertTrue(PluginInitializer.addIssueTypeToScheme(JiraIssueTypes.getTestTypes().get(0), "TEST"));
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeNullProjectKeyNull() {
		assertFalse(PluginInitializer.removeIssueTypeFromScheme(null, null));
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeNullProjectKeyFilled() {
		assertFalse(PluginInitializer.removeIssueTypeFromScheme(null, "TEST"));
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeFilledProjectKeyNull() {
		assertFalse(PluginInitializer.removeIssueTypeFromScheme("Decision", null));
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeFilledProjectKeyFilled() {
		assertTrue(PluginInitializer.removeIssueTypeFromScheme("Decision", "TEST"));
	}

	@Test
	public void testRemoveLinkTypeFromSchemeLinkTypeNullProjectKeyNull() {
		assertFalse(PluginInitializer.removeLinkTypeFromScheme(null, null));
	}

	@Test
	public void testRemoveLinkTypeFromSchemeLinkTypeNullProjectKeyFilled() {
		assertFalse(PluginInitializer.removeLinkTypeFromScheme(null, "TEST"));
	}

	@Test
	public void testRemoveLinkTypeFromSchemeLinkTypeFilledProjectKeyNull() {
		assertFalse(PluginInitializer.removeLinkTypeFromScheme("Decision", null));
	}

	@Test
	public void testRemoveLinkTypeFromSchemeLinkTypeFilledProjectKeyFilled() {
		assertTrue(PluginInitializer.removeLinkTypeFromScheme("Decision", "TEST"));
	}

	@Test
	public void testAddLinkTypeToSchemeLinkTypeNullProjectKeyNull() {
		assertFalse(PluginInitializer.addLinkTypeToScheme(null, null));
	}

	@Test
	public void testAddLinkTypeToSchemeLinkTypeNullProjectKeyFilled() {
		assertFalse(PluginInitializer.addLinkTypeToScheme(null, "TEST"));
	}

	@Test
	public void testAddLinkTypeToSchemeLinkTypeFilledProjectKeyNull() {
		assertFalse(PluginInitializer.addLinkTypeToScheme("Decision", null));
	}

	@Test
	public void testAddLinkTypeToSchemeLinkTypeFilledProjectKeyFilled() {
		assertTrue(PluginInitializer.addLinkTypeToScheme("Decision", "TEST"));
	}

	@Test
	public void testGetIconUrl() {
		assertEquals(
				"null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/decision.png",
				PluginInitializer.getIconUrl("decision"));
	}

}