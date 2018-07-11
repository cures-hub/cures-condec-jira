package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.Set;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class ConfigPersistence {
	private static PluginSettingsFactory pluginSettingsFactory = ComponentGetter.getPluginSettingsFactory();
	private static TransactionTemplate transactionTemplate = ComponentGetter.getTransactionTemplate();
	private static String pluginStorageKey = ComponentGetter.getPluginStorageKey();

	public static boolean isIssueStrategy(String projectKey) {
		Object isIssueStrategy = transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + ".isIssueStrategy");
			}
		});
		return isIssueStrategy instanceof String && "true".equals(isIssueStrategy);
	}

	public static void setIssueStrategy(String projectKey, boolean isIssueStrategy) {
		if (projectKey == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + ".isIssueStrategy", Boolean.toString(isIssueStrategy));
	}

	public static boolean isActivated(String projectKey) {
		Object isActivated = transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + ".isActivated");
			}
		});
		return isActivated instanceof String && "true".equals(isActivated);
	}

	public static void setActivated(String projectKey, boolean isActivated) {
		if (projectKey == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + ".isActivated", Boolean.toString(isActivated));
		if (isActivated) {
			activateDefaultKnowledgeTypes(projectKey);
		}
	}

	public static void activateDefaultKnowledgeTypes(String projectKey) {
		Set<KnowledgeType> defaultKnowledgeTypes = KnowledgeType.getDefaulTypes();
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		for (KnowledgeType knowledgeType : defaultKnowledgeTypes) {
			settings.put(pluginStorageKey + "." + knowledgeType, "true");
		}
	}

	public static boolean isKnowledgeExtractedFromGit(String projectKey) {
		if (projectKey == null) {
			return false;
		}
		Object isKnowledgeExtractedFromGit = transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + ".isKnowledgeExtractedFromGit");
			}
		});
		return isKnowledgeExtractedFromGit instanceof String && "true".equals(isKnowledgeExtractedFromGit);
	}

	public static void setKnowledgeExtractedFromGit(String projectKey, boolean setKnowledgeExtractedFromGit) {
		if (projectKey == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + ".isKnowledgeExtractedFromGit", Boolean.toString(setKnowledgeExtractedFromGit));
	}

	public static boolean isKnowledgeExtractedFromIssues(String projectKey) {
		if (projectKey == null) {
			return false;
		}
		Object isKnowledgeExtractedFromIssues = transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + ".isKnowledgeExtractedFromIssues");
			}
		});
		return isKnowledgeExtractedFromIssues instanceof String && "true".equals(isKnowledgeExtractedFromIssues);
	}

	public static void setKnowledgeExtractedFromIssues(String projectKey, boolean setKnowledgeExtractedFromIssues) {
		if (projectKey == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + ".isKnowledgeExtractedFromIssues", Boolean.toString(setKnowledgeExtractedFromIssues));
	}

	public static boolean isKnowledgeTypeEnabled(String projectKey, String knowledgeType) {
		Object isKnowledgeTypeEnabled = transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + "." + knowledgeType);
			}
		});
		return isKnowledgeTypeEnabled instanceof String && "true".equals(isKnowledgeTypeEnabled);
	}

	public static boolean isKnowledgeTypeEnabled(String projectKey, KnowledgeType knowledgeType) {
		return isKnowledgeTypeEnabled(projectKey, knowledgeType.toString());
	}

	public static void setKnowledgeTypeEnabled(String projectKey, String knowledgeTypes,
			boolean isKnowledgeTypeEnabled) {
		if (projectKey == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + "." + knowledgeTypes, Boolean.toString(isKnowledgeTypeEnabled));
	}
}
