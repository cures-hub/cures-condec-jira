package de.uhd.ifi.se.decision.documentation.jira.config;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.model.JiraProject;

public class Config {

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
		if (isIssueStrategy instanceof String && isIssueStrategy.equals("true")) {
			return true;
		}
		return false;
	}

	public static void setIssueStrategy(final String projectKey, boolean isIssueStrategy) {
		transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				settings.put(pluginStorageKey + ".isIssueStrategy", Boolean.toString(isIssueStrategy));
				return null;
			}
		});
	}

	public static boolean isActivated(String projectKey) {
		Object isActivated = transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + ".isActivated");
			}
		});
		if (isActivated instanceof String && isActivated.equals("true")) {
			return true;
		}
		return false;
	}

	public static void setActivated(String projectKey, boolean isActivated) {
		transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				settings.put(pluginStorageKey + ".isActivated", Boolean.toString(isActivated));
				return null;
			}
		});
	}

	public static Map<String, JiraProject> createConfigMap() {
		Map<String, JiraProject> configMap = new HashMap<String, JiraProject>();
		for (Project project : ComponentAccessor.getProjectManager().getProjects()) {
			String projectKey = project.getKey();
			String projectName = project.getName();
			JiraProject jiraProject = new JiraProject(projectKey, projectName, Config.isActivated(projectKey), Config.isIssueStrategy(projectKey));
			configMap.put(projectKey, jiraProject);
		}
		return configMap;
	}
}
