package de.uhd.ifi.se.decision.management.jira.persistence;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.TextClassificationConfiguration;
import de.uhd.ifi.se.decision.management.jira.extraction.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.checktriggers.PromptingEventConfiguration;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.CiaSettings;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.DecisionGuidanceConfiguration;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendationConfiguration;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCategory;

/**
 * Stores and reads configuration settings such as whether the ConDec plug-in is
 * activated for a specific project.
 */
public class ConfigPersistenceManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigPersistenceManager.class);
	private static PluginSettingsFactory pluginSettingsFactory = ComponentAccessor
			.getOSGiComponentInstanceOfType(PluginSettingsFactory.class);
	private static TransactionTemplate transactionTemplate = ComponentAccessor
			.getOSGiComponentInstanceOfType(TransactionTemplate.class);

	public static Collection<String> getEnabledWebhookTypes(String projectKey) {
		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		Collection<IssueType> issueTypes = issueTypeManager.getIssueTypes();
		Collection<String> issueTypeNames = new ArrayList<>();
		for (IssueType issueType : issueTypes) {
			if (isWebhookTypeEnabled(projectKey, issueType.getName())) {
				issueTypeNames.add(issueType.getName());
			}
		}
		return issueTypeNames;
	}

	public static String getValue(String projectKey, String parameter) {
		if (projectKey == null || projectKey.isBlank()) {
			return "";
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);

		if (parameter == null || parameter.isBlank()) {
			return "";
		}
		Object value = transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				return settings.get(ComponentGetter.PLUGIN_KEY + "." + parameter);
			}
		});
		return value != null ? value.toString() : "";
	}

	public static Object getSavedObject(String projectKey, String parameter, Type type) {
		Gson gson = new Gson();
		Object object = null;
		try {
			object = gson.fromJson(getValue(projectKey, parameter), type);
		} catch (Exception e) {
			LOGGER.error("Saved config could not be read: " + e.getMessage());
		}
		return object;
	}

	public static String getWebhookSecret(String projectKey) {
		return getValue(projectKey, "webhookSecret");
	}

	public static String getWebhookUrl(String projectKey) {
		return getValue(projectKey, "webhookUrl");
	}

	public static String getDecisionTableCriteriaQuery(String projectKey) {
		return getValue(projectKey, "criteriaQuery");
	}

	public static boolean isActivated(String projectKey) {
		String isActivated = getValue(projectKey, "isActivated");
		return "true".equals(isActivated);
	}

	public static boolean isIssueStrategy(String projectKey) {
		String isIssueStrategy = getValue(projectKey, "isIssueStrategy");
		return "true".equals(isIssueStrategy);
	}

	// TODO Testing
	public static boolean isKnowledgeTypeEnabled(String projectKey, KnowledgeType knowledgeType) {
		return isKnowledgeTypeEnabled(projectKey, knowledgeType.toString());
	}

	public static boolean isKnowledgeTypeEnabled(String projectKey, String knowledgeType) {
		String isKnowledgeTypeEnabled = getValue(projectKey, knowledgeType);
		return "true".equals(isKnowledgeTypeEnabled);
	}

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

	public static boolean isWebhookEnabled(String projectKey) {
		String isWebhookEnabled = getValue(projectKey, "isWebhookEnabled");
		return "true".equals(isWebhookEnabled);
	}

	public static boolean isWebhookTypeEnabled(String projectKey, String webhookType) {
		if (webhookType == null || webhookType.isBlank()) {
			return false;
		}
		String isWebhookTypeEnabled = getValue(projectKey, "webhookType" + "." + webhookType);
		return "true".equals(isWebhookTypeEnabled);
	}

	public static void setActivated(String projectKey, boolean isActivated) {
		setValue(projectKey, "isActivated", Boolean.toString(isActivated));
	}

	public static void setIssueStrategy(String projectKey, boolean isIssueStrategy) {
		setValue(projectKey, "isIssueStrategy", Boolean.toString(isIssueStrategy));
	}

	public static void setDecisionTableCriteriaQuery(String projectKey, String query) {
		setValue(projectKey, "criteriaQuery", query);
	}

	public static void saveGitConfiguration(String projectKey, GitConfiguration gitConfiguration) {
		Type type = new TypeToken<GitConfiguration>() {
		}.getType();
		saveObject(projectKey, "gitConfiguration", gitConfiguration, type);
	}

	public static GitConfiguration getGitConfiguration(String projectKey) {
		Type type = new TypeToken<GitConfiguration>() {
		}.getType();
		GitConfiguration gitConfiguration = (GitConfiguration) getSavedObject(projectKey, "gitConfiguration", type);
		if (gitConfiguration == null) {
			return new GitConfiguration();
		}
		return gitConfiguration;
	}

	public static void setKnowledgeTypeEnabled(String projectKey, String knowledgeType,
			boolean isKnowledgeTypeEnabled) {
		setValue(projectKey, knowledgeType, Boolean.toString(isKnowledgeTypeEnabled));
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

	public static void saveTextClassificationConfiguration(String projectKey,
			TextClassificationConfiguration textClassificationConfiguration) {
		Type type = new TypeToken<TextClassificationConfiguration>() {
		}.getType();
		saveObject(projectKey, "textClassificationConfiguration", textClassificationConfiguration, type);
	}

	public static void setValue(String projectKey, String parameter, String value) {
		if (projectKey == null || value == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(ComponentGetter.PLUGIN_KEY + "." + parameter, value);
	}

	public static void saveObject(String projectKey, String parameter, Object value, Type type) {
		Gson gson = new Gson();
		setValue(projectKey, parameter, gson.toJson(value, type));
	}

	public static void setWebhookEnabled(String projectKey, boolean isWebhookEnabled) {
		setValue(projectKey, "isWebhookEnabled", Boolean.toString(isWebhookEnabled));
	}

	public static void setWebhookSecret(String projectKey, String webhookSecret) {
		setValue(projectKey, "webhookSecret", webhookSecret);
	}

	public static void setWebhookType(String projectKey, String webhookType, boolean isWebhookTypeEnabled) {
		if (webhookType == null || webhookType.isBlank()) {
			return;
		}
		setValue(projectKey, "webhookType" + "." + webhookType, Boolean.toString(isWebhookTypeEnabled));
	}

	public static void setWebhookUrl(String projectKey, String webhookUrl) {
		setValue(projectKey, "webhookUrl", webhookUrl);
	}

	public static void setReleaseNoteMapping(String projectKey, ReleaseNotesCategory category,
			List<String> selectedIssueNames) {
		String joinedIssueNames = String.join(",", selectedIssueNames);
		setValue(projectKey, "releaseNoteMapping" + "." + category, joinedIssueNames);
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

	public static void setCiaSettings(String projectKey, CiaSettings ciaSettings) {
		Type type = new TypeToken<CiaSettings>() {
		}.getType();
		saveObject(projectKey, "ciaSettings", ciaSettings, type);
	}

	public static CiaSettings getCiaSettings(String projectKey) {
		Type type = new TypeToken<CiaSettings>() {
		}.getType();
		CiaSettings ciaSettings = (CiaSettings) getSavedObject(projectKey, "ciaSettings", type);
		if (ciaSettings == null) {
			ciaSettings = new CiaSettings();
		}
		return ciaSettings;
	}
}
