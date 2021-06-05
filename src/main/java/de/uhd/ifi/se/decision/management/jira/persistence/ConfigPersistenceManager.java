package de.uhd.ifi.se.decision.management.jira.persistence;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.TextClassificationConfiguration;
import de.uhd.ifi.se.decision.management.jira.config.BasicConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.quality.checktriggers.PromptingEventConfiguration;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.CiaSettings;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.DecisionGuidanceConfiguration;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendationConfiguration;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCategory;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesConfiguration;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConfiguration;

/**
 * Stores and reads configuration settings such as whether the ConDec plug-in is
 * activated for a specific project (see {@link DecisionKnowledgeProject}).
 * 
 * @see BasicConfiguration
 * @see GitConfiguration
 * @see TextClassificationConfiguration
 * @see DecisionGuidanceConfiguration
 * @see LinkRecommendationConfiguration
 * @see PromptingEventConfiguration
 * @see CiaSettings
 * @see WebhookConfiguration
 * @see ReleaseNotesConfiguration
 */
public class ConfigPersistenceManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigPersistenceManager.class);
	private static PluginSettingsFactory pluginSettingsFactory = ComponentAccessor
			.getOSGiComponentInstanceOfType(PluginSettingsFactory.class);
	private static TransactionTemplate transactionTemplate = ComponentAccessor
			.getOSGiComponentInstanceOfType(TransactionTemplate.class);

	/**
	 * @param projectKey
	 *            of the Jira project (see {@link DecisionKnowledgeProject}).
	 * @param parameter
	 *            name of the settings to read, e.g.
	 *            "textClassificationConfiguration".
	 * @param value
	 *            setting/configuration value. Can be an entire object in JSON
	 *            format, see {@link #saveObject(String, String, Object, Type)}.
	 */
	private static void saveValue(String projectKey, String parameter, String value) {
		if (projectKey == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(ComponentGetter.PLUGIN_KEY + "." + parameter, value);
	}

	/**
	 * @param projectKey
	 *            of the Jira project (see {@link DecisionKnowledgeProject}).
	 * @param parameter
	 *            name of the settings to read, e.g.
	 *            "textClassificationConfiguration".
	 * @param value
	 *            setting/configuration value as an entire object in JSON format.
	 * @param type
	 *            class of the object as a {@link TypeToken}, e.g.
	 *            {@code new TypeToken<TextClassificationConfiguration>()}.
	 */
	private static void saveObject(String projectKey, String parameter, Object value, Type type) {
		Gson gson = new Gson();
		saveValue(projectKey, parameter, gson.toJson(value, type));
	}

	/**
	 * @param projectKey
	 *            of the Jira project (see {@link DecisionKnowledgeProject}).
	 * @param parameter
	 *            name of the settings to read, e.g.
	 *            "textClassificationConfiguration".
	 * @return setting/configuration value. Can be an entire object in JSON format,
	 *         see {@link #getSavedObject(String, String, Type)}.
	 */
	private static String getValue(String projectKey, String parameter) {
		if (projectKey == null) {
			return "";
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		Object value = transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				return settings.get(ComponentGetter.PLUGIN_KEY + "." + parameter);
			}
		});
		return value != null ? value.toString() : "";
	}

	/**
	 * @param projectKey
	 *            of the Jira project (see {@link DecisionKnowledgeProject}).
	 * @param parameter
	 *            name of the settings to read, e.g.
	 *            "textClassificationConfiguration".
	 * @param type
	 *            class of the object as a {@link TypeToken}, e.g.
	 *            {@code new TypeToken<TextClassificationConfiguration>()}.
	 * @return object of the class, e.g. an object of
	 *         {@link TextClassificationConfiguration}.
	 */
	private static Object getSavedObject(String projectKey, String parameter, Type type) {
		Gson gson = new Gson();
		Object object = null;
		try {
			object = gson.fromJson(getValue(projectKey, parameter), type);
		} catch (Exception e) {
			LOGGER.error("Saved config could not be read: " + e.getMessage());
		}
		return object;
	}

	/**
	 * @param projectKey
	 *            of the Jira project (see {@link DecisionKnowledgeProject}).
	 * @param basicConfiguration
	 *            basic settings for the ConDec plug-in as a
	 *            {@link BasicConfiguration} object.
	 */
	public static void saveBasicConfiguration(String projectKey, BasicConfiguration basicConfiguration) {
		Type type = new TypeToken<BasicConfiguration>() {
		}.getType();
		saveObject(projectKey, "basicConfiguration", basicConfiguration, type);
	}

	/**
	 * @param projectKey
	 *            of the Jira project (see {@link DecisionKnowledgeProject}).
	 * @return basic settings for the ConDec plug-in as a {@link BasicConfiguration}
	 *         object.
	 */
	public static BasicConfiguration getBasicConfiguration(String projectKey) {
		Type type = new TypeToken<BasicConfiguration>() {
		}.getType();
		BasicConfiguration basicConfiguration = (BasicConfiguration) getSavedObject(projectKey, "basicConfiguration",
				type);
		if (basicConfiguration == null) {
			return new BasicConfiguration();
		}
		return basicConfiguration;
	}

	/**
	 * @param projectKey
	 *            of the Jira project (see {@link DecisionKnowledgeProject}).
	 * @param gitConfiguration
	 *            settings for the git connection as a {@link GitConfiguration}
	 *            object.
	 */
	public static void saveGitConfiguration(String projectKey, GitConfiguration gitConfiguration) {
		Type type = new TypeToken<GitConfiguration>() {
		}.getType();
		saveObject(projectKey, "gitConfiguration", gitConfiguration, type);
	}

	/**
	 * @param projectKey
	 *            of the Jira project (see {@link DecisionKnowledgeProject}).
	 * @return settings for the git connection as a {@link GitConfiguration} object.
	 */
	public static GitConfiguration getGitConfiguration(String projectKey) {
		Type type = new TypeToken<GitConfiguration>() {
		}.getType();
		GitConfiguration gitConfiguration = (GitConfiguration) getSavedObject(projectKey, "gitConfiguration", type);
		if (gitConfiguration == null) {
			return new GitConfiguration();
		}
		return gitConfiguration;
	}

	/**
	 * @param projectKey
	 *            of the Jira project (see {@link DecisionKnowledgeProject}).
	 * @param textClassificationConfiguration
	 *            settings for the automatic text classification as a
	 *            {@link TextClassificationConfiguration} object.
	 */
	public static void saveTextClassificationConfiguration(String projectKey,
			TextClassificationConfiguration textClassificationConfiguration) {
		Type type = new TypeToken<TextClassificationConfiguration>() {
		}.getType();
		saveObject(projectKey, "textClassificationConfiguration", textClassificationConfiguration, type);
	}

	/**
	 * @param projectKey
	 *            of the Jira project (see {@link DecisionKnowledgeProject}).
	 * @return settings for the automatic text classification as a
	 *         {@link TextClassificationConfiguration} object.
	 */
	public static TextClassificationConfiguration getTextClassificationConfiguration(String projectKey) {
		Type type = new TypeToken<TextClassificationConfiguration>() {
		}.getType();
		TextClassificationConfiguration textClassificationConfiguration = (TextClassificationConfiguration) getSavedObject(
				projectKey, "textClassificationConfiguration", type);
		if (textClassificationConfiguration == null) {
			return new TextClassificationConfiguration();
		}
		return textClassificationConfiguration;
	}

	public static void setTextClassifierActivated(String projectKey, boolean isActivated) {
		TextClassificationConfiguration textClassificationConfiguration = getTextClassificationConfiguration(
				projectKey);
		textClassificationConfiguration.setActivated(isActivated);
		saveTextClassificationConfiguration(projectKey, textClassificationConfiguration);
	}

	public static void setTrainingFileForClassifier(String projectKey, String trainingFileName) {
		TextClassificationConfiguration textClassificationConfiguration = getTextClassificationConfiguration(
				projectKey);
		textClassificationConfiguration.setSelectedGroundTruthFile(trainingFileName);
		saveTextClassificationConfiguration(projectKey, textClassificationConfiguration);
	}

	public static void setReleaseNoteMapping(String projectKey, ReleaseNotesCategory category,
			List<String> selectedIssueNames) {
		String joinedIssueNames = String.join(",", selectedIssueNames);
		saveValue(projectKey, "releaseNoteMapping" + "." + category, joinedIssueNames);
	}

	public static List<String> getReleaseNoteMapping(String projectKey, ReleaseNotesCategory category) {
		String joinedIssueNames = getValue(projectKey, "releaseNoteMapping" + "." + category);
		return Arrays.asList(joinedIssueNames.split(","));
	}

	public static void saveLinkSuggestionConfiguration(String projectKey,
			LinkRecommendationConfiguration linkSuggestionConfiguration) {
		Type type = new TypeToken<LinkRecommendationConfiguration>() {
		}.getType();
		saveObject(projectKey, "linkSuggestionConfiguration", linkSuggestionConfiguration, type);
	}

	public static LinkRecommendationConfiguration getLinkRecommendationConfiguration(String projectKey) {
		Type type = new TypeToken<LinkRecommendationConfiguration>() {
		}.getType();
		LinkRecommendationConfiguration linkSuggestionConfiguration = (LinkRecommendationConfiguration) getSavedObject(
				projectKey, "linkSuggestionConfiguration", type);
		if (linkSuggestionConfiguration == null) {
			return new LinkRecommendationConfiguration();
		}
		return linkSuggestionConfiguration;
	}

	public static void saveDecisionGuidanceConfiguration(String projectKey,
			DecisionGuidanceConfiguration decisionGuidanceConfiguration) {
		Type type = new TypeToken<DecisionGuidanceConfiguration>() {
		}.getType();
		saveObject(projectKey, "decisionGuidanceConfiguration", decisionGuidanceConfiguration, type);
	}

	public static DecisionGuidanceConfiguration getDecisionGuidanceConfiguration(String projectKey) {
		Type type = new TypeToken<DecisionGuidanceConfiguration>() {
		}.getType();
		DecisionGuidanceConfiguration decisionGuidanceConfiguration = (DecisionGuidanceConfiguration) getSavedObject(
				projectKey, "decisionGuidanceConfiguration", type);
		if (decisionGuidanceConfiguration == null) {
			return new DecisionGuidanceConfiguration();
		}
		return decisionGuidanceConfiguration;
	}

	public static void saveDefinitionOfDone(String projectKey, DefinitionOfDone definitionOfDone) {
		Type type = new TypeToken<DefinitionOfDone>() {
		}.getType();
		saveObject(projectKey, "definitionOfDone", definitionOfDone, type);
	}

	public static DefinitionOfDone getDefinitionOfDone(String projectKey) {
		Type type = new TypeToken<DefinitionOfDone>() {
		}.getType();
		DefinitionOfDone definitionOfDone = (DefinitionOfDone) getSavedObject(projectKey, "definitionOfDone", type);
		if (definitionOfDone == null) {
			return new DefinitionOfDone();
		}
		return definitionOfDone;
	}

	public static void savePromptingEventConfiguration(String projectKey,
			PromptingEventConfiguration promptingEventConfiguration) {
		Type type = new TypeToken<PromptingEventConfiguration>() {
		}.getType();
		saveObject(projectKey, "promptingEventConfiguration", promptingEventConfiguration, type);
	}

	public static PromptingEventConfiguration getPromptingEventConfiguration(String projectKey) {
		Type type = new TypeToken<PromptingEventConfiguration>() {
		}.getType();
		PromptingEventConfiguration promptingEventConfiguration = (PromptingEventConfiguration) getSavedObject(
				projectKey, "promptingEventConfiguration", type);
		if (promptingEventConfiguration == null) {
			return new PromptingEventConfiguration();
		}
		return promptingEventConfiguration;
	}

	public static void saveChangeImpactAnalysisConfiguration(String projectKey,
			CiaSettings changeImpactAnalysisConfiguration) {
		Type type = new TypeToken<CiaSettings>() {
		}.getType();
		saveObject(projectKey, "changeImpactAnalysisConfiguration", changeImpactAnalysisConfiguration, type);
	}

	public static CiaSettings getChangeImpactAnalysisConfiguration(String projectKey) {
		Type type = new TypeToken<CiaSettings>() {
		}.getType();
		CiaSettings changeImpactAnalysisConfiguration = (CiaSettings) getSavedObject(projectKey,
				"changeImpactAnalysisConfiguration", type);
		if (changeImpactAnalysisConfiguration == null) {
			changeImpactAnalysisConfiguration = new CiaSettings();
		}
		return changeImpactAnalysisConfiguration;
	}

	/**
	 * @param projectKey
	 *            of the Jira project (see {@link DecisionKnowledgeProject}).
	 * @return settings for the webhook (used for knowledge sharing) as a
	 *         {@link WebhookConfiguration} object.
	 */
	public static void saveWebhookConfiguration(String projectKey, WebhookConfiguration webhookConfiguration) {
		Type type = new TypeToken<WebhookConfiguration>() {
		}.getType();
		saveObject(projectKey, "webhookConfiguration", webhookConfiguration, type);
	}

	/**
	 * @param projectKey
	 *            of the Jira project (see {@link DecisionKnowledgeProject}).
	 * @return settings for the webhook (used for knowledge sharing) as a
	 *         {@link WebhookConfiguration} object.
	 */
	public static WebhookConfiguration getWebhookConfiguration(String projectKey) {
		Type type = new TypeToken<WebhookConfiguration>() {
		}.getType();
		WebhookConfiguration webhookConfiguration = (WebhookConfiguration) getSavedObject(projectKey,
				"webhookConfiguration", type);
		if (webhookConfiguration == null) {
			return new WebhookConfiguration();
		}
		return webhookConfiguration;
	}
}
