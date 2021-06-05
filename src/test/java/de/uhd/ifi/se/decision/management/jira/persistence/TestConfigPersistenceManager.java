package de.uhd.ifi.se.decision.management.jira.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.config.BasicConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettings;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.CiaSettings;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCategory;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConfiguration;

/**
 * Test class for the persistence of the plugin settings. The plugin settings
 * are mocked in the {@link MockPluginSettings} class.
 *
 * @issue How can we enable that settings can be set during testing?
 * @decision Implement MockPluginSettings and MockPluginSettingsFactory classes
 *           to enable that settings can be set during testing!
 * @see MockPluginSettings
 * @see MockPluginSettingsFactory
 */
public class TestConfigPersistenceManager extends TestSetUp {

	@BeforeClass
	public static void setUpBeforeClass() {
		init();
	}

	@Test
	public void testConstructor() {
		assertNotNull(new ConfigPersistenceManager());
	}

	@Test
	public void testProjectKeyNull() {
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setMaximumLinkDistanceToDecisions(42);
		ConfigPersistenceManager.saveDefinitionOfDone(null, definitionOfDone);
		definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone(null);
		assertFalse(definitionOfDone.getMaximumLinkDistanceToDecisions() == 42);
	}

	@Test
	public void testValueNull() {
		ConfigPersistenceManager.saveDefinitionOfDone("TEST", null);
		assertNotNull(ConfigPersistenceManager.getDefinitionOfDone("TEST"));
	}

	@Test
	public void testParameterUnknown() {
		// because of MockPluginSettings, true is returned, not ""
		assertEquals("true", ConfigPersistenceManager.getValue("TEST", "unknown"));
	}

	@Test
	public void testGetAndSaveBasicConfiguration() {
		BasicConfiguration basicConfig = ConfigPersistenceManager.getBasicConfiguration("TEST");
		assertTrue(basicConfig.isActivated());
		ConfigPersistenceManager.saveBasicConfiguration("TEST", basicConfig);
		basicConfig = ConfigPersistenceManager.getBasicConfiguration("TEST");
		assertTrue(basicConfig.isActivated());
	}

	@Test
	public void testGetAndSaveGitConfiguration() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		assertFalse(gitConfig.isActivated());
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);
		gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		assertFalse(gitConfig.isActivated());
	}

	@Test
	public void testGetAndSaveDefinitionOfDone() {
		DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone("TEST");
		assertFalse(definitionOfDone.isDecisionIsLinkedToPro());
		ConfigPersistenceManager.saveDefinitionOfDone("TEST", definitionOfDone);
		definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone("TEST");
		assertFalse(definitionOfDone.isDecisionIsLinkedToPro());
	}

	@Test
	public void testSetAndGetCiaSettings() {
		CiaSettings settings = new CiaSettings();
		assertEquals(0.75, settings.getDecayValue(), 0.01);
		assertEquals(0.25, settings.getThreshold(), 0.01);
		assertEquals(9, settings.getLinkImpact().size());
		settings.setDecayValue(0.75f);
		settings.setThreshold(0.2f);
		settings.setLinkImpact(Map.of("comment", 0.5f));
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration("TEST", settings);
		CiaSettings loaded = ConfigPersistenceManager.getChangeImpactAnalysisConfiguration("TEST");
		assertEquals(0.75, loaded.getDecayValue(), 0.01);
		assertEquals(0.2, loaded.getThreshold(), 0.01);
		assertEquals(1, loaded.getLinkImpact().size());
		assertEquals(0.5f, loaded.getLinkImpact().getOrDefault("comment", 0.0f), 0.01);
	}

	@Test
	public void testSetAndGetReleaseNoteMapping() {
		List<String> input = new ArrayList<>();
		input.add("someOtherString");
		ReleaseNotesCategory category = ReleaseNotesCategory.IMPROVEMENTS;
		ConfigPersistenceManager.setReleaseNoteMapping("TEST", category, input);
		assertEquals(input, ConfigPersistenceManager.getReleaseNoteMapping("TEST", category));
	}

	@Test
	public void testGetAndSaveWebhookConfiguration() {
		WebhookConfiguration webhookConfig = ConfigPersistenceManager.getWebhookConfiguration("TEST");
		assertFalse(webhookConfig.isActivated());
		ConfigPersistenceManager.saveWebhookConfiguration("TEST", webhookConfig);
		webhookConfig = ConfigPersistenceManager.getWebhookConfiguration("TEST");
		assertFalse(webhookConfig.isActivated());
	}

	@AfterClass
	public static void tearDown() {
		// reset plugin settings to default settings
		MockPluginSettingsFactory.pluginSettings = new MockPluginSettings();
	}
}
