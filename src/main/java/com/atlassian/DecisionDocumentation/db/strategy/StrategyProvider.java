package com.atlassian.DecisionDocumentation.db.strategy;

import com.atlassian.DecisionDocumentation.db.strategy.impl.AoStrategy;
import com.atlassian.DecisionDocumentation.db.strategy.impl.IssueStrategy;
import com.atlassian.DecisionDocumentation.util.ComponentGetter;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class StrategyProvider {

	public Strategy getStrategy(final String projectKey) {
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
		if(ob instanceof String) {
			String strategyType = (String) ob;
			if (strategyType.equalsIgnoreCase("true")) {
				return new IssueStrategy();
			} else {
				return new AoStrategy();
			}
		} else {
			return new AoStrategy();
		}
	}
}