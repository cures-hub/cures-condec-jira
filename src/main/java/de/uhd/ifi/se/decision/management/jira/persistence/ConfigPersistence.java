package de.uhd.ifi.se.decision.management.jira.persistence;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Class to store and receive configuration settings
 */
public class ConfigPersistence {
	private static PluginSettingsFactory pluginSettingsFactory = ComponentGetter.getPluginSettingsFactory();
	private static TransactionTemplate transactionTemplate = ComponentGetter.getTransactionTemplate();
	private static String pluginStorageKey = ComponentGetter.getPluginStorageKey();

	public static String getValue(String parameter, String projectKey, boolean isGlobalSetting) {
		PluginSettings settings;
		if (isGlobalSetting) {
			settings = pluginSettingsFactory.createGlobalSettings();
		} else {
			if (projectKey == null) {
				return "";
			}
			settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		}
		if (parameter == null) {
			return "";
		}
		Object value = transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				return settings.get(pluginStorageKey + "." + parameter);
			}
		});
		if (value instanceof String) {
			return value.toString();
		}
		return "";
	}

	public static String getValue(String parameter) {
		return getValue(parameter, null, true);
	}

	public static String getValue(String projectKey, String parameter) {
		return getValue(parameter, projectKey, false);
	}

	public static void setValue(String projectKey, String parameter, String value) {
		if (projectKey == null || value == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + "." + parameter, value);
	}

	public static void setValue(String parameter, String value) {
		PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
		settings.put(pluginStorageKey + "." + parameter, value);
	}

	public static boolean isActivated(String projectKey) {
		String isActivated = getValue(projectKey, "isActivated");
		return "true".equals(isActivated);
	}

	public static boolean isIssueStrategy(String projectKey) {
		String isIssueStrategy = getValue(projectKey, "isIssueStrategy");
		return "true".equals(isIssueStrategy);
	}

	public static boolean isKnowledgeExtractedFromGit(String projectKey) {
		String isKnowledgeExtractedFromGit = getValue(projectKey, "isKnowledgeExtractedFromGit");
		return "true".equals(isKnowledgeExtractedFromGit);
	}

	public static boolean isKnowledgeExtractedFromIssues(String projectKey) {
		String isKnowledgeExtractedFromIssues = getValue(projectKey, "isKnowledgeExtractedFromIssues");
		return "true".equals(isKnowledgeExtractedFromIssues);
	}

	public static boolean isKnowledgeTypeEnabled(String projectKey, KnowledgeType knowledgeType) {
		return isKnowledgeTypeEnabled(projectKey, knowledgeType.toString());
	}

	public static boolean isKnowledgeTypeEnabled(String projectKey, String knowledgeType) {
		String isKnowledgeTypeEnabled = getValue(projectKey, knowledgeType);
		return "true".equals(isKnowledgeTypeEnabled);
	}

	public static void setActivated(String projectKey, boolean isActivated) {
		setValue(projectKey, "isActivated", Boolean.toString(isActivated));
	}

	public static void setIssueStrategy(String projectKey, boolean isIssueStrategy) {
		setValue(projectKey, "isIssueStrategy", Boolean.toString(isIssueStrategy));
	}

	public static void setKnowledgeExtractedFromGit(String projectKey, boolean isKnowledgeExtractedFromGit) {
		setValue(projectKey, "isKnowledgeExtractedFromGit", Boolean.toString(isKnowledgeExtractedFromGit));
	}

	public static void setKnowledgeExtractedFromIssues(String projectKey, boolean isKnowledgeExtractedFromIssues) {
		setValue(projectKey, "isKnowledgeExtractedFromIssues", Boolean.toString(isKnowledgeExtractedFromIssues));
	}

	public static void setKnowledgeTypeEnabled(String projectKey, String knowledgeType,
			boolean isKnowledgeTypeEnabled) {
		setValue(projectKey, "knowledgeType", Boolean.toString(isKnowledgeTypeEnabled));
	}

	// TODO Testing
	public static void setGitAddress(String projectKey, String gitAddress) {
		setValue(projectKey, "gitAddress", gitAddress);
	}

	// TODO Testing
	public static String getGitAddress(String projectKey) {
		return getValue(projectKey, "gitAddress");
	}

	// TODO Testing
	public static void setWebhookUrl(String projectKey, String webhookUrl) {
		setValue(projectKey, "webhookUrl", webhookUrl);
	}

	// TODO Testing
	public static String getWebhookUrl(String projectKey) {
		return getValue(projectKey, "webhookUrl");
	}

	// TODO Testing
	public static void setWebhookSecret(String projectKey, String webhookSecret) {
		setValue(projectKey, "webhookSecret", webhookSecret);
	}

	// TODO Testing
	public static String getWebhookSecret(String projectKey) {
		return getValue(projectKey, "webhookSecret");
	}

	// TODO Testing
	public static void setWebhookEnabled(String projectKey, boolean isWebhookEnabled) {
		setValue(projectKey, "isWebhookEnabled", Boolean.toString(isWebhookEnabled));
	}

	// TODO Testing
	public static boolean isWebhookEnabled(String projectKey) {
		String isWebhookEnabled = getValue(projectKey, "isWebhookEnabled");
		return "true".equals(isWebhookEnabled);
	}

	// TODO Testing
	public static void setWebhookType(String projectKey, String webhookType) {
		setValue(projectKey, "webhookType", webhookType);
	}

	// TODO Testing
	public static String getWebhookType(String projectKey) {
		return getValue(projectKey, "webhookType");
	}

	public static boolean isIconParsing(String projectKey) {
		String isIconParsing = getValue(projectKey, "isIconParsing");
		return "true".equals(isIconParsing);
	}

	public static void setIconParsing(String projectKey, boolean isIconParsing) {
		setValue(projectKey, "isIconParsing", Boolean.toString(isIconParsing));
	}

	public static void setRequestToken(String requestToken) {
		setValue("requestToken", requestToken);
	}

	public static String getRequestToken() {
		return getValue("requestToken");
	}

	public static void setOauthJiraHome(String oauthJiraHome) {
		setValue("oauthJiraHome", oauthJiraHome);
	}

	public static String getOauthJiraHome() {
		return getValue("oauthJiraHome");
	}

	public static void setPrivateKey(String privateKey) {
		setValue("privateKey", privateKey);
	}

	public static String getPrivateKey() {
		return getValue("privateKey");
	}

	public static String getConsumerKey() {
		return getValue("consumerKey");
	}

	public static void setConsumerKey(String consumerKey) {
		setValue("consumerKey", consumerKey);
	}

	public static void setSecretForOAuth(String gitAuthSecret) {
		setValue("gitAuthSecret", gitAuthSecret);
	}

	public static String getSecretForOAuth() {
		return getValue("gitAuthSecret");
	}

	public static void setAccessToken(String accessToken) {
		setValue("accessToken", accessToken);
	}

	public static String getAccessToken() {
		return getValue("accessToken");
	}
}
