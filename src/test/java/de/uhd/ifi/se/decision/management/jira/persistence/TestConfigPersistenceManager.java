package de.uhd.ifi.se.decision.management.jira.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettings;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCategory;

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
		ConfigPersistenceManager.isIconParsing("TEST");
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
	public void testGetGitRepos() {
		GitRepositoryConfiguration gitConf1 = new GitRepositoryConfiguration(TestSetUpGit.GIT_URI, "master", "HTTP",
				"user", "secret👀");
		GitRepositoryConfiguration gitConf2 = new GitRepositoryConfiguration(TestSetUpGit.GIT_URI, "develop", "GITHUB",
				"githubuser", "token👀");

		ConfigPersistenceManager.setGitRepositoryConfigurations("TEST", Arrays.asList(gitConf1, gitConf2));

		GitRepositoryConfiguration gitConf = ConfigPersistenceManager.getGitRepositoryConfigurations("TEST").get(0);
		assertEquals(TestSetUpGit.GIT_URI, gitConf.getRepoUri());
		assertEquals("master", gitConf.getDefaultBranch());
		assertEquals("HTTP", gitConf.getAuthMethod());
		assertEquals("user", gitConf.getUsername());
		assertEquals("secret👀", gitConf.getToken());

		gitConf = ConfigPersistenceManager.getGitRepositoryConfigurations("TEST").get(1);
		assertEquals(TestSetUpGit.GIT_URI, gitConf.getRepoUri());
		assertEquals("develop", gitConf.getDefaultBranch());
		assertEquals("GITHUB", gitConf.getAuthMethod());
		assertEquals("githubuser", gitConf.getUsername());
		assertEquals("token👀", gitConf.getToken());
	}

	@Test
	public void testGetEmptyOrCorruptConfInfo() {
		GitRepositoryConfiguration gitConf1 = new GitRepositoryConfiguration(TestSetUpGit.GIT_URI, "", "Cheesecake", "",
				"");
		GitRepositoryConfiguration gitConf2 = new GitRepositoryConfiguration(TestSetUpGit.GIT_URI, "develop", "GITHUB",
				"githubuser", "token👀");

		ConfigPersistenceManager.setGitRepositoryConfigurations("TEST", Arrays.asList(gitConf1, gitConf2));

		GitRepositoryConfiguration gitConf = ConfigPersistenceManager.getGitRepositoryConfigurations("TEST").get(0);
		assertEquals(TestSetUpGit.GIT_URI, gitConf.getRepoUri());
		assertEquals("master", gitConf.getDefaultBranch());
		assertEquals("NONE", gitConf.getAuthMethod());
		assertEquals("", gitConf.getUsername());
		assertEquals("", gitConf.getToken());

		gitConf = ConfigPersistenceManager.getGitRepositoryConfigurations("TEST").get(1);
		assertEquals(TestSetUpGit.GIT_URI, gitConf.getRepoUri());
		assertEquals("develop", gitConf.getDefaultBranch());
		assertEquals("GITHUB", gitConf.getAuthMethod());
		assertEquals("githubuser", gitConf.getUsername());
		assertEquals("token👀", gitConf.getToken());
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
	public void testSetAndGetFragmentLength() {
		int input = 4;
		ConfigPersistenceManager.setFragmentLength("TEST", input);
		assertEquals("Activated should be 4.", 4, ConfigPersistenceManager.getFragmentLength("TEST"));
		// Cannot be tested because the MockPluginSettingsFactory does not support
		// multiple projects
		/*
		 * input = 3; ConfigPersistenceManager.setFragmentLength("NOTTEST", input);
		 * assertEquals("Activated should still be 4.", 4,
		 * ConfigPersistenceManager.getFragmentLength("TEST"));
		 * 
		 */
	}

	@Test
	public void testSetAndGetMinLinkSuggestionScore() {
		double input = 0.4;
		ConfigPersistenceManager.setMinLinkSuggestionScore("TEST", input);
		assertEquals("Activated should be 0.4.", input, ConfigPersistenceManager.getMinLinkSuggestionScore("TEST"),
				0.0);
		// Cannot be tested because the MockPluginSettingsFactory does not support
		// multiple projects
		/*
		 * input = 0.3; ConfigPersistenceManager.setMinLinkSuggestionScore("NOTTEST",
		 * input); assertTrue("Activated should still be 0.4.", 0.4 ==
		 * ConfigPersistenceManager.getMinLinkSuggestionScore("TEST"));
		 * 
		 */
	}

	@Test
	public void testSetAndGetActivationStatusOfQualityEvent() {
		String consistencyEvent = "done";
		ConfigPersistenceManager.setActivationStatusOfQualityEvent("TEST", consistencyEvent, true);
		assertTrue("Activated should be true.",
				ConfigPersistenceManager.getActivationStatusOfQualityEvent("TEST", consistencyEvent));

		ConfigPersistenceManager.setActivationStatusOfQualityEvent("TEST", consistencyEvent, false);
		assertFalse("Activated should be false.",
				ConfigPersistenceManager.getActivationStatusOfQualityEvent("TEST", consistencyEvent));

		String otherConsistencyEvent = "none";
		ConfigPersistenceManager.setActivationStatusOfQualityEvent("TEST", otherConsistencyEvent, true);
		assertFalse("Activated for 'done' should still be false.",
				ConfigPersistenceManager.getActivationStatusOfQualityEvent("TEST", consistencyEvent));

		// Cannot be tested because the MockPluginSettingsFactory does not support
		// multiple projects
		/*
		 * isActivated = true;
		 * ConfigPersistenceManager.setActivationStatusOfConsistencyEvent("NOTTEST",
		 * consistencyEvent, isActivated);
		 * assertFalse("Activated for 'done' of project 'TEST' should still be false.",
		 * ConfigPersistenceManager.getActivationStatusOfConsistencyEvent("TEST",
		 * consistencyEvent));
		 */
	}

	@Test
	public void testSetAndGetRDFKnowledgeSource() {
		RDFSource rdfSource = new RDFSource("TEST", "service", "query", "RDF Name", "30000");
		ConfigPersistenceManager.setRDFKnowledgeSource("TEST", rdfSource);
		assertEquals("Number of Knowledge sources should be 1", 1,
				ConfigPersistenceManager.getRDFKnowledgeSource("TEST").size());

		RDFSource rdfSourceUpdated = new RDFSource("TEST", "service2", "query2", "RDF Name2", "10000");
		ConfigPersistenceManager.updateKnowledgeSource("TEST", "RDF Name", rdfSourceUpdated);
		assertEquals("service2", ConfigPersistenceManager.getRDFKnowledgeSource("TEST").get(0).getService());
		assertEquals("query2", ConfigPersistenceManager.getRDFKnowledgeSource("TEST").get(0).getQueryString());
		assertEquals("10000", ConfigPersistenceManager.getRDFKnowledgeSource("TEST").get(0).getTimeout());
		assertEquals("RDF Name2", ConfigPersistenceManager.getRDFKnowledgeSource("TEST").get(0).getName());

		// Test invalid Source
		ConfigPersistenceManager.setRDFKnowledgeSource("TEST", null);
		assertEquals("Size of existing Knowledge sources should be 1: No error!", 1,
				ConfigPersistenceManager.getRDFKnowledgeSource("TEST").size());

		// Test deactivation
		ConfigPersistenceManager.setRDFKnowledgeSourceActivation("TEST", "RDF Name2", false);
		assertFalse("The knowledge source should be dectivated!",
				ConfigPersistenceManager.getRDFKnowledgeSource("TEST").get(0).isActivated());

		// Delete KnowledgeSource
		ConfigPersistenceManager.deleteKnowledgeSource("TEST", "RDF Name2");
		assertEquals("The knowledge source should be 0!", 0,
				ConfigPersistenceManager.getRDFKnowledgeSource("TEST").size());
	}

	@Test
	public void testSetAndGetProjectKnowledgeSources() {
		ConfigPersistenceManager.setProjectSource("TEST", "OTHERPRORJECT", true);
		assertTrue(ConfigPersistenceManager.getProjectSource("TEST", "OTHERPRORJECT"));
		ConfigPersistenceManager.setProjectSource("TEST", "OTHERPRORJECT", false);
		assertFalse(ConfigPersistenceManager.getProjectSource("TEST", "OTHERPRORJECT"));
	}

	@Test
	public void testGetProjectSourceIfInitial() {
		assertTrue(ConfigPersistenceManager.getProjectSource("TEST", "THIS PROJECT DOES NOT EXIST"));
	}

	@Test
	public void testSetAndGetMaxRecommendations() {
		ConfigPersistenceManager.setMaxNumberRecommendations("TEST", 10);
		assertEquals(10, ConfigPersistenceManager.getMaxNumberRecommendations("TEST"));
	}

	@Test
	public void testGetActiveProjects() {
		ConfigPersistenceManager.setProjectSource("TEST", "TEST", true);
		assertEquals(1, ConfigPersistenceManager.getProjectSourcesForActiveProjects("TEST").size());
	}

	@Test
	public void testGetActiveProjectsSourcesInvalid() {
		assertEquals(0, ConfigPersistenceManager.getProjectSourcesForActiveProjects("PROJECT DOES NOT EXIST").size());
	}

	@Test
	public void testGetAllKnowledgeSources() {
		assertEquals(1, ConfigPersistenceManager.getAllKnowledgeSources("TEST").size());
	}

	@Test
	public void testGetAllKnowledgeSourcesInvalidProject() {
		assertEquals(0, ConfigPersistenceManager.getAllKnowledgeSources("PROJECT DOES NOT EXIST").size());
	}

	@Test
	public void testGetAllKnowledgeSourcesEmptyProject() {
		assertEquals(0, ConfigPersistenceManager.getAllKnowledgeSources("").size());
	}

	@Test
	public void testGetAllKnowledgeSourcesNullProject() {
		assertEquals(0, ConfigPersistenceManager.getAllKnowledgeSources(null).size());
	}

	@Test
	public void testSetAndGetAddRecommendationDirectly() {
		ConfigPersistenceManager.setAddRecommendationDirectly("TEST", true);
		assertTrue(ConfigPersistenceManager.getAddRecommendationDirectly("TEST"));
		ConfigPersistenceManager.setAddRecommendationDirectly("TEST", false);
		assertFalse(ConfigPersistenceManager.getAddRecommendationDirectly("TEST"));
	}

	@Test
	public void testSetAndGetRecommendationInput() {
		// assertEquals(RecommenderType.getDefault(),
		// ConfigPersistenceManager.getRecommendationInput("TEST"));
		ConfigPersistenceManager.setRecommendationInput("TEST", "KEYWORD");
		assertEquals(RecommenderType.KEYWORD, ConfigPersistenceManager.getRecommendationInput("TEST"));
		ConfigPersistenceManager.setRecommendationInput(null, "KEYWORD");
		assertEquals(RecommenderType.getDefault(), ConfigPersistenceManager.getRecommendationInput(null));
	}

	@Test
	public void testSetAndGetDefinitionOfDone() {
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		ConfigPersistenceManager.setDefinitionOfDone("TEST", definitionOfDone);
		assertFalse(ConfigPersistenceManager.getDefinitionOfDone("TEST").isAlternativeIsLinkedToArgument());
		assertFalse(ConfigPersistenceManager.getDefinitionOfDone("TEST").isDecisionIsLinkedToPro());
		assertFalse(ConfigPersistenceManager.getDefinitionOfDone("TEST").isIssueIsLinkedToAlternative());
		definitionOfDone.setAlternativeLinkedToArgument(true);
		ConfigPersistenceManager.setDefinitionOfDone("TEST", definitionOfDone);
		assertTrue(ConfigPersistenceManager.getDefinitionOfDone("TEST").isAlternativeIsLinkedToArgument());
	}

	@AfterClass
	public static void tearDown() {
		// reset plugin settings to default settings
		MockPluginSettingsFactory.pluginSettings = new MockPluginSettings();
	}
}
