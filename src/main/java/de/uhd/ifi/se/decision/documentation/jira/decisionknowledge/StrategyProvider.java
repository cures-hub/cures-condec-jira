package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StrategyProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(IssueStrategy.class);

	public IDecisionStorageStrategy getStrategy(final String projectKey) {
		if(projectKey==null){
			LOGGER.error("ProjectKey == null");
			return null;
		}
		TransactionTemplate transactionTemplate = ComponentGetter.getTransactionTemplate();
		final PluginSettingsFactory pluginSettingsFactory = ComponentGetter.getPluginSettingsFactory();
		final String pluginStorageKey = ComponentGetter.getPluginStorageKey();
		Object ob = transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction() {
				PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
				Object o = settings.get(pluginStorageKey + ".isIssueStrategy");
				return o;
			}
		});
		if (ob instanceof String) {
			String strategyType = (String) ob;
			if (strategyType.equalsIgnoreCase("true")) {
				return new IssueStrategy();
			} else {
				return new ActiveObjectStrategy();
			}
		} else {
			return new ActiveObjectStrategy();
		}
	}
}