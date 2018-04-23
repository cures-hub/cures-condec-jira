package de.uhd.ifi.se.decision.documentation.jira.persistence;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;

/**
 * @description Provides the persistence strategy for a project
 */
public class StrategyProvider {

	public PersistenceStrategy getStrategy(String projectKey) {
		if (projectKey == null) {
			throw new IllegalArgumentException("The project key cannot be null.");
		}
		boolean isIssueStrategy = isIssueStrategy(projectKey);
		if (isIssueStrategy) {
			return new IssueStrategy();
		}
		return new ActiveObjectStrategy();
	}

	public boolean isIssueStrategy(String projectKey) {
		TransactionTemplate transactionTemplate = ComponentGetter.getTransactionTemplate();
		PluginSettingsFactory pluginSettingsFactory = ComponentGetter.getPluginSettingsFactory();
		String pluginStorageKey = ComponentGetter.getPluginStorageKey();
		Object isIssueStrategy = transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				return settings.get(pluginStorageKey + ".isIssueStrategy");
			}
		});
		if (isIssueStrategy instanceof String && isIssueStrategy.equals("true")) {
			return Boolean.valueOf((String) isIssueStrategy);
		}
		return false;
	}
}