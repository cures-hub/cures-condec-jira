package de.uhd.ifi.se.decision.management.jira.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestGitClient;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettings;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteCategory;

/**
 * Test class for the persistence of the plugin settings. The plugin settings
 * are mocked in the {@link MockPluginSettings} class.
 * 
 * @see MockPluginSettings
 * @see MockPluginSettingsFactory
 * 
 * @issue How can we enable that settings can be set during testing?
 * @decision Implement MockPluginSettings and MockPluginSettingsFactory classes
 *           to enable that settings can be set during testing!
 */
public class TestConfigPersistenceManager extends TestSetUp {

	@BeforeClass
	public static void setUpBeforeClass() {
		init();
	}

	// test global (=across project) configuration
	@Test
	public void testSetGlobalValue() {
		ConfigPersistenceManager.setValue("isActivated", "true");
		assertEquals("true", ConfigPersistenceManager.getValue("isActivated"));
	}

	// configure persistence in Jira issues
	@Test
	public void testSetIssueStrategyNullFalse() {
		ConfigPersistenceManager.setIssueStrategy(null, false);
		assertFalse(ConfigPersistenceManager.isIssueStrategy(null));
	}

	@Test
	public void testSetIssueStrategyValidTrue() {
		ConfigPersistenceManager.setIssueStrategy("TEST", true);
		assertTrue(ConfigPersistenceManager.isIssueStrategy("TEST"));
	}

	// plugin activation
	@Test
	public void testSetActivatedNullFalse() {
		ConfigPersistenceManager.setActivated("TEST", false);
		assertFalse(ConfigPersistenceManager.isActivated("TEST"));
	}

	@Test
	public void testSetActivatedValid() {
		ConfigPersistenceManager.setActivated("TEST", true);
		assertTrue(ConfigPersistenceManager.isActivated("TEST"));
	}

	// knowledge extraction from git
	@Test
	public void testIsKnowledgeExtractedFilled() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit(null, false);
		assertFalse(ConfigPersistenceManager.isKnowledgeExtractedFromGit(null));

		ConfigPersistenceManager.setKnowledgeExtractedFromGit("TEST", false);
		assertFalse(ConfigPersistenceManager.isKnowledgeExtractedFromGit("TEST"));

		ConfigPersistenceManager.setKnowledgeExtractedFromGit("TEST", true);
		assertTrue(ConfigPersistenceManager.isKnowledgeExtractedFromGit("TEST"));
	}

	// isKnowledgeTypeEnabled
	@Test
	public void testIsKnowledgeTypeEnabledKeyNullTypeFilled() {
		assertFalse(ConfigPersistenceManager.isKnowledgeTypeEnabled(null, KnowledgeType.SOLUTION.toString()));
	}

	@Test
	public void testIsKnowledgeTypeEnabledKeyFilledTypeFilled() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString(), true);
		assertTrue(ConfigPersistenceManager.isKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString()));
	}

	// setKnowledgeTypeEnabled
	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeNullEnabledFalse() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled(null, null, false);
		assertFalse(ConfigPersistenceManager.isKnowledgeTypeEnabled(null, ""));
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeNullEnabledTrue() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled(null, null, true);
		assertFalse(ConfigPersistenceManager.isKnowledgeTypeEnabled(null, ""));
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeFilledEnabledFalse() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled(null, KnowledgeType.SOLUTION.toString(), false);
		assertFalse(ConfigPersistenceManager.isKnowledgeTypeEnabled(null, ""));
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeFilledEnabledTrue() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled(null, KnowledgeType.SOLUTION.toString(), true);
		assertFalse(ConfigPersistenceManager.isKnowledgeTypeEnabled(null, ""));
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeNullEnabledFalse() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled("TEST", null, false);
		assertFalse(ConfigPersistenceManager.isKnowledgeTypeEnabled("TEST", ""));
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeNullEnabledTrue() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled("TEST", null, true);
		assertFalse(ConfigPersistenceManager.isKnowledgeTypeEnabled("TEST", ""));
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeFilledEnabledFalse() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString(), false);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeFilledEnabledTrue() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString(), true);
		assertTrue(ConfigPersistenceManager.isKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString()));
	}

	@Test
	public void testSetIconParsingTrue() {
		ConfigPersistenceManager.setIconParsing("TEST", true);
		assertTrue(ConfigPersistenceManager.isIconParsing("TEST"));
	}

	@Test
	public void testSetIconParsingFalse() {
		ConfigPersistenceManager.setIconParsing("TEST", false);
	}

	@Test
	public void testIsIconParsingTrue() {
		assertNotNull(ConfigPersistenceManager.isIconParsing("TEST"));
	}

	@Test
	public void testSetWebhookUrlNullNull() {
		ConfigPersistenceManager.setWebhookUrl(null, null);
		assertEquals("", ConfigPersistenceManager.getWebhookUrl(null));
	}

	@Test
	public void testSetWebhookUrlFilledNull() {
		ConfigPersistenceManager.setWebhookUrl("TEST", null);
	}

	@Test
	public void testSetWebhookUrlNullFilled() {
		ConfigPersistenceManager.setWebhookUrl(null, "http://true");
		assertEquals("", ConfigPersistenceManager.getWebhookUrl(null));
	}

	@Test
	public void testSetWebhookUrlFilledFilled() {
		ConfigPersistenceManager.setWebhookUrl("TEST", "http://true");
		assertEquals("http://true", ConfigPersistenceManager.getWebhookUrl("TEST"));
	}

	@Test
	public void testGetWebhookUrlNull() {
		assertEquals("", ConfigPersistenceManager.getWebhookUrl(null));
	}

	@Test
	public void testSetWebhookSecretNullNull() {
		ConfigPersistenceManager.setWebhookSecret(null, null);
		assertEquals("", ConfigPersistenceManager.getWebhookSecret(null));
	}

	@Test
	public void testSetWebhookSecretFilledNull() {
		ConfigPersistenceManager.setWebhookSecret("TEST", null);
	}

	@Test
	public void testSetWebhookSecretNullFilled() {
		ConfigPersistenceManager.setWebhookSecret(null, "http://true");
		assertEquals("", ConfigPersistenceManager.getWebhookSecret(null));
	}

	@Test
	public void testSetWebhookSecretFilledFilled() {
		ConfigPersistenceManager.setWebhookSecret("TEST", "myhoneybee");
		assertEquals("myhoneybee", ConfigPersistenceManager.getWebhookSecret("TEST"));
	}

	@Test
	public void testGetWebhookSecretNull() {
		assertEquals("", ConfigPersistenceManager.getWebhookSecret(null));
	}

	@Test
	public void testSetWebhookEnabledNullFalse() {
		ConfigPersistenceManager.setWebhookEnabled(null, false);
		assertFalse(ConfigPersistenceManager.isWebhookEnabled(null));
	}

	@Test
	public void testSetWebhookEnabledNullTrue() {
		ConfigPersistenceManager.setWebhookEnabled(null, true);
		assertFalse(ConfigPersistenceManager.isWebhookEnabled(null));
	}

	@Test
	public void testSetWebhookEnabledFilledFalse() {
		ConfigPersistenceManager.setWebhookEnabled("TEST", false);
	}

	@Test
	public void testSetWebhookEnabledFiledTrue() {
		ConfigPersistenceManager.setWebhookEnabled("TEST", true);
		assertTrue(ConfigPersistenceManager.isWebhookEnabled("TEST"));
	}

	@Test
	public void testIsWebhookEnabledNull() {
		assertFalse(ConfigPersistenceManager.isWebhookEnabled(null));
	}

	@Test
	public void testIsWebhookEnabledEmpty() {
		assertFalse(ConfigPersistenceManager.isWebhookEnabled(""));
	}

	@Test
	public void testIsWebhookEnabledFilled() {
		ConfigPersistenceManager.setWebhookEnabled("TEST", true);
		assertTrue(ConfigPersistenceManager.isWebhookEnabled("TEST"));
	}

	@Test
	public void testSetWebhookTypeNullNullFalse() {
		ConfigPersistenceManager.setWebhookType(null, null, false);
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, null));
	}

	@Test
	public void testSetWebhookTypeNullNullTrue() {
		ConfigPersistenceManager.setWebhookType(null, null, true);
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, null));
	}

	@Test
	public void testSetWebhookTypeNullEmptyFalse() {
		ConfigPersistenceManager.setWebhookType(null, "", false);
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, ""));
	}

	@Test
	public void testSetWebhookTypeNullEmptyTrue() {
		ConfigPersistenceManager.setWebhookType(null, "", true);
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, ""));
	}

	@Test
	public void testSetWebhookTypeNullFilledFalse() {
		ConfigPersistenceManager.setWebhookType(null, "Task", false);
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, "Task"));
	}

	@Test
	public void testSetWebhookTypeNullFilledTrue() {
		ConfigPersistenceManager.setWebhookType(null, "Task", true);
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, "Task"));
	}

	@Test
	public void testSetWebhookTypeFilledNullFalse() {
		ConfigPersistenceManager.setWebhookType("TEST", null, false);
	}

	@Test
	public void testSetWebhookTypeFilledNullTrue() {
		ConfigPersistenceManager.setWebhookType("TEST", null, true);
	}

	@Test
	public void testSetWebhookTypeFilledEmptyFalse() {
		ConfigPersistenceManager.setWebhookType("TEST", "", false);
	}

	@Test
	public void testSetWebhookTypeFilledEmptyTrue() {
		ConfigPersistenceManager.setWebhookType("TEST", "", true);
	}

	@Test
	public void testSetWebhookTypeFilledFilledFalse() {
		ConfigPersistenceManager.setWebhookType("TEST", "Task", false);
	}

	@Test
	public void testSetWebhookTypeFilledFilledTrue() {
		ConfigPersistenceManager.setWebhookType("TEST", "Task", true);
		assertNotNull(ConfigPersistenceManager.getEnabledWebhookTypes("TEST"));
	}

	@Test
	public void testIsWebhookTypeEnabledNullNull() {
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, null));
	}

	@Test
	public void testIsWebhookTypeEnabledNullEmpty() {
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, ""));
	}

	@Test
	public void testIsWebhookTypeEnabledEmptyNull() {
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled("", null));
	}

	@Test
	public void testIsWebhookTypeEnabledNullFilled() {
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, "Task"));
	}

	@Test
	public void testIsWebhookTypeEnabledFilledNull() {
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled("TEST", null));
	}

	@Test
	public void testIsWebhookTypeEnabledEmptyFilled() {
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled("", "Task"));
	}

	@Test
	public void testIsWebhookTypeEnabledFilledEmpty() {
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled("TEST", ""));
	}

	@Test
	public void testIsWebhookTypeEnabledFilledFilled() {
		ConfigPersistenceManager.setWebhookType("TEST", "Task", true);
		assertTrue(ConfigPersistenceManager.isWebhookTypeEnabled("TEST", "Task"));
	}

	@Test
	public void testGetEnabledWebhookTypesNull() {
		assertEquals(0, ConfigPersistenceManager.getEnabledWebhookTypes(null).size());
	}

	@Test
	public void testGetEnabledWebhookTypesEmpty() {
		assertEquals(0, ConfigPersistenceManager.getEnabledWebhookTypes("").size());
	}

	@Test
	public void testGetEnabledWebhookTypesFilled() {
		assertEquals(13, ConfigPersistenceManager.getEnabledWebhookTypes("TEST").size());
	}

	@Test
	public void testGetGitUri() {
		ConfigPersistenceManager.setGitUri("TEST", TestGitClient.GIT_URI);
		assertEquals(TestGitClient.GIT_URI, ConfigPersistenceManager.getGitUri("TEST"));
	}

	@Test
	public void testSetAndGetReleaseNoteMapping() {
		List<String> input = new ArrayList<String>();
		input.add("someOtherString");
		ReleaseNoteCategory category = ReleaseNoteCategory.IMPROVEMENTS;
		ConfigPersistenceManager.setReleaseNoteMapping("TEST", category, input);
		assertEquals(input, ConfigPersistenceManager.getReleaseNoteMapping("TEST", category));
	}

	@AfterClass
	public static void tearDown() {
		// reset plugin settings to default settings
		MockPluginSettingsFactory.pluginSettings = new MockPluginSettings();
	}
}
