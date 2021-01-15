package de.uhd.ifi.se.decision.management.jira.config;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueTypes;

public class TestPluginInitializer {

	@BeforeClass
	public static void setUpBeforeClass() {
		TestSetUp.init();
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeNullProjectKeyNull() {
		PluginInitializer.addIssueTypeToScheme(null, null);
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeNullProjectKeyFilled() {
		PluginInitializer.addIssueTypeToScheme(null, "TEST");
	}

	@Test
	public void testAddIssueTypeToSchemeIssueTypeFilledProjectKeyNull() {
		PluginInitializer.addIssueTypeToScheme(JiraIssueTypes.getTestTypes().get(0), null);
	}

	@Ignore
	@Test
	public void testAddIssueTypeToSchemeIssueTypeFilledProjectKeyFilled() {
		PluginInitializer.addIssueTypeToScheme(JiraIssueTypes.getTestTypes().get(0), "TEST");
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeNullProjectKeyNull() {
		PluginInitializer.removeIssueTypeFromScheme(null, null);
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeNullProjectKeyFilled() {
		PluginInitializer.removeIssueTypeFromScheme(null, "TEST");
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeFilledProjectKeyNull() {
		PluginInitializer.removeIssueTypeFromScheme("Decision", null);
	}

	@Test
	public void testRemoveIssueTypeToSchemeIssueTypeFilledProjectKeyFilled() {
		PluginInitializer.removeIssueTypeFromScheme("Decision", "TEST");
	}

	@Test
	public void testRemoveLinkTypeFromSchemeLinkTypeNullProjectKeyNull() {
		PluginInitializer.removeLinkTypeFromScheme(null, null);
	}

	@Test
	public void testRemoveLinkTypeFromSchemeLinkTypeNullProjectKeyFilled() {
		PluginInitializer.removeLinkTypeFromScheme(null, "TEST");
	}

	@Test
	public void testRemoveLinkTypeFromSchemeLinkTypeFilledProjectKeyNull() {
		PluginInitializer.removeLinkTypeFromScheme("Decision", null);
	}

	@Test
	public void testRemoveLinkTypeFromSchemeLinkTypeFilledProjectKeyFilled() {
		PluginInitializer.removeLinkTypeFromScheme("Decision", "TEST");
	}

	@Test
	public void testAddLinkTypeToSchemeLinkTypeNullProjectKeyNull() {
		PluginInitializer.addLinkTypeToScheme(null, null);
	}

	@Test
	public void testAddLinkTypeToSchemeLinkTypeNullProjectKeyFilled() {
		PluginInitializer.addLinkTypeToScheme(null, "TEST");
	}

	@Test
	public void testAddLinkTypeToSchemeLinkTypeFilledProjectKeyNull() {
		PluginInitializer.addLinkTypeToScheme("Decision", null);
	}

	@Test
	public void testAddLinkTypeToSchemeLinkTypeFilledProjectKeyFilled() {
		PluginInitializer.addLinkTypeToScheme("Decision", "TEST");
	}

}