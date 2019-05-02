package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.Collection;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Class to store and receive configuration settings
 */
public class ConfigPersistenceManager {
	private static PluginSettingsFactory pluginSettingsFactory = ComponentGetter.getPluginSettingsFactory();
	private static TransactionTemplate transactionTemplate = ComponentGetter.getTransactionTemplate();
	private static String pluginStorageKey = ComponentGetter.getPluginStorageKey();

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

	public static String getValue(String parameter) {
		return getValue(parameter, null, true);
	}

	public static String getValue(String projectKey, String parameter) {
		return getValue(parameter, projectKey, false);
	}

	public static String getValue(String parameter, String projectKey, boolean isGlobalSetting) {
		PluginSettings settings;
		if (isGlobalSetting) {
			settings = pluginSettingsFactory.createGlobalSettings();
		} else {
			if (projectKey == null || projectKey.equals("")) {
				return "";
			}
			settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		}
		if (parameter == null || parameter.equals("")) {
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

	public static String getWebhookSecret(String projectKey) {
		return getValue(projectKey, "webhookSecret");
	}

	public static String getWebhookUrl(String projectKey) {
		return getValue(projectKey, "webhookUrl");
	}

	public static boolean isActivated(String projectKey) {
		String isActivated = getValue(projectKey, "isActivated");
		return "true".equals(isActivated);
	}

	public static boolean isIconParsing(String projectKey) {
		String isIconParsing = getValue(projectKey, "isIconParsing");
		return "true".equals(isIconParsing);
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

	public static boolean isUseClassiferForIssueComments(String projectKey) {
		return getValue(projectKey, "setClassiferForIssueComments").equals("true");
	}

	public static boolean isWebhookEnabled(String projectKey) {
		String isWebhookEnabled = getValue(projectKey, "isWebhookEnabled");
		return "true".equals(isWebhookEnabled);
	}

	public static boolean isWebhookTypeEnabled(String projectKey, String webhookType) {
		if (webhookType == null || webhookType.equals("")) {
			return false;
		}
		String isWebhookTypeEnabled = getValue(projectKey, "webhookType" + "." + webhookType);
		return "true".equals(isWebhookTypeEnabled);
	}

	public static void setActivated(String projectKey, boolean isActivated) {
		setValue(projectKey, "isActivated", Boolean.toString(isActivated));
	}

	public static void setIconParsing(String projectKey, boolean isIconParsing) {
		setValue(projectKey, "isIconParsing", Boolean.toString(isIconParsing));
	}

	public static void setIssueStrategy(String projectKey, boolean isIssueStrategy) {
		setValue(projectKey, "isIssueStrategy", Boolean.toString(isIssueStrategy));
	}

	public static void setKnowledgeExtractedFromGit(String projectKey, boolean isKnowledgeExtractedFromGit) {
		setValue(projectKey, "isKnowledgeExtractedFromGit", Boolean.toString(isKnowledgeExtractedFromGit));
		if (isKnowledgeExtractedFromGit) {
			new GitClientImpl(projectKey);
		}
	}

	// TODO Testing
	public static void setGitUri(String projectKey, String gitUri) {
		setValue(projectKey, "gitUri", gitUri);
	}

	public static String getGitUri(String projectKey) {
		return getValue(projectKey, "gitUri");
	}

	public static void setKnowledgeExtractedFromIssues(String projectKey, boolean isKnowledgeExtractedFromIssues) {
		setValue(projectKey, "isKnowledgeExtractedFromIssues", Boolean.toString(isKnowledgeExtractedFromIssues));
	}

	public static void setKnowledgeTypeEnabled(String projectKey, String knowledgeType,
			boolean isKnowledgeTypeEnabled) {
		setValue(projectKey, knowledgeType, Boolean.toString(isKnowledgeTypeEnabled));
	}

	public static void setUseClassiferForIssueComments(String projectKey, boolean isActivated) {
		setValue(projectKey, "setClassiferForIssueComments", Boolean.toString(isActivated));
	}

	public static void setValue(String parameter, String value) {
		PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
		settings.put(pluginStorageKey + "." + parameter, value);
	}

	public static void setValue(String projectKey, String parameter, String value) {
		if (projectKey == null || value == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + "." + parameter, value);
	}

	public static void setWebhookEnabled(String projectKey, boolean isWebhookEnabled) {
		setValue(projectKey, "isWebhookEnabled", Boolean.toString(isWebhookEnabled));
	}

	public static void setWebhookSecret(String projectKey, String webhookSecret) {
		setValue(projectKey, "webhookSecret", webhookSecret);
	}

	public static void setWebhookType(String projectKey, String webhookType, boolean isWebhookTypeEnabled) {
		if (webhookType == null || webhookType.equals("")) {
			return;
		}
		setValue(projectKey, "webhookType" + "." + webhookType, Boolean.toString(isWebhookTypeEnabled));
	}

	public static void setWebhookUrl(String projectKey, String webhookUrl) {
		setValue(projectKey, "webhookUrl", webhookUrl);
	}

	public static void setArffFileForClassifier(String projectKey, String arffFileName) {
		setValue(projectKey, "arffFileName", arffFileName);
	}

	public static String getArffFileForClassifier(String projectKey) {
		return getValue(projectKey, "arffFileName");
	}
}
