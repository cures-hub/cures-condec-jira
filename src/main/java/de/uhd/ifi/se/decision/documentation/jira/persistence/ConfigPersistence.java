package de.uhd.ifi.se.decision.documentation.jira.persistence;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;

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
		PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
		settings.put(pluginStorageKey + ".isActivated", Boolean.toString(isActivated));
	}
}
