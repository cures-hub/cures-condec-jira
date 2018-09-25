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

	public static boolean isActivated(String projectKey) {
		Object isActivated = transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + ".isActivated");
			}
		});
		return isActivated instanceof String && "true".equals(isActivated);
	}

	public static boolean isIssueStrategy(String projectKey) {
		if (projectKey == null) {
			return false;
		}
		Object isIssueStrategy = transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + ".isIssueStrategy");
			}
		});
		return isIssueStrategy instanceof String && "true".equals(isIssueStrategy);
	}

	public static boolean isKnowledgeExtractedFromGit(String projectKey) {
		if (projectKey == null) {
			return false;
		}
		Object isKnowledgeExtractedFromGit = transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + ".isKnowledgeExtractedFromGit");
			}
		});
		return isKnowledgeExtractedFromGit instanceof String && "true".equals(isKnowledgeExtractedFromGit);
	}

	public static boolean isKnowledgeExtractedFromIssues(String projectKey) {
		if (projectKey == null) {
			return false;
		}
		Object isKnowledgeExtractedFromIssues = transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + ".isKnowledgeExtractedFromIssues");
			}
		});
		return isKnowledgeExtractedFromIssues instanceof String && "true".equals(isKnowledgeExtractedFromIssues);
	}

	public static boolean isKnowledgeTypeEnabled(String projectKey, KnowledgeType knowledgeType) {
		return isKnowledgeTypeEnabled(projectKey, knowledgeType.toString());
	}

	public static boolean isKnowledgeTypeEnabled(String projectKey, String knowledgeType) {
		if (projectKey == null) {
			return false;
		}
		Object isKnowledgeTypeEnabled = transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + "." + knowledgeType);
			}
		});
		return isKnowledgeTypeEnabled instanceof String && "true".equals(isKnowledgeTypeEnabled);
	}

	public static void setActivated(String projectKey, boolean isActivated) {
		if (projectKey == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + ".isActivated", Boolean.toString(isActivated));
	}

	public static void setIssueStrategy(String projectKey, boolean isIssueStrategy) {
		if (projectKey == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + ".isIssueStrategy", Boolean.toString(isIssueStrategy));
	}

	public static void setKnowledgeExtractedFromGit(String projectKey, boolean setKnowledgeExtractedFromGit) {
		if (projectKey == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + ".isKnowledgeExtractedFromGit", Boolean.toString(setKnowledgeExtractedFromGit));
	}

	public static void setKnowledgeExtractedFromIssues(String projectKey, boolean setKnowledgeExtractedFromIssues) {
		if (projectKey == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + ".isKnowledgeExtractedFromIssues",
				Boolean.toString(setKnowledgeExtractedFromIssues));
	}

	public static void setKnowledgeTypeEnabled(String projectKey, String knowledgeType,
			boolean isKnowledgeTypeEnabled) {
		if (projectKey == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + "." + knowledgeType, Boolean.toString(isKnowledgeTypeEnabled));
	}

	// TODO Testing
	public static void setGitAddress(String projectKey, String gitAddress) {
		if (projectKey == null || gitAddress == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + ".gitAddress", gitAddress);
	}

	// TODO Testing
	public static String getGitAddress(String projectKey) {
		if (projectKey == null) {
			return "";
		}
		Object gitAddress = transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + ".gitAddress");
			}
		});
		if (gitAddress instanceof String) {
			return (String) gitAddress;
		}
		return "";
	}

	// TODO Testing
	public static void setWebhookUrl(String projectKey, String webhookUrl) {
		if (projectKey == null || webhookUrl == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + ".webhookUrl", webhookUrl);
	}

	// TODO Testing
	public static String getWebhookUrl(String projectKey) {
		if (projectKey == null) {
			return "";
		}
		Object webhookUrl = transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + ".webhookUrl");
			}
		});
		if (webhookUrl instanceof String) {
			return (String) webhookUrl;
		}
		return "";
	}

	// TODO Testing
	public static void setWebhookSecret(String projectKey, String webhookSecret) {
		if (projectKey == null || webhookSecret == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + ".webhookSecret", webhookSecret);
	}

	// TODO Testing
	public static String getWebhookSecret(String projectKey) {
		if (projectKey == null) {
			return "";
		}
		Object webhookSecret = transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + ".webhookSecret");
			}
		});
		if (webhookSecret instanceof String) {
			return (String) webhookSecret;
		}
		return "";
	}

	// TODO Testing
	public static void setWebhookEnabled(String projectKey, boolean isActivated) {
		if (projectKey == null) {
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + ".isWebhookEnabled", Boolean.toString(isActivated));
	}

	public static boolean isWebhookEnabled(String projectKey) {
		if (projectKey == null) {
			return false;
		}
		Object isWebhookEnabled = transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + ".isWebhookEnabled");
			}
		});
		return isWebhookEnabled instanceof String && "true".equals(isWebhookEnabled);
	}

	//TODO Testing
	public static void setWebhookType(String projectKey, String webhookType){
		if(projectKey == null || webhookType == null){
			return;
		}
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + ".webhookType", webhookType);
	}
}
