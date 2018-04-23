package de.uhd.ifi.se.decision.documentation.jira.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;

/**
 * @description Condition for side bar link to plug-in. Determines whether link
 *              is displayed for a project. Is used in atlassian-plugin.xml
 */
public class ActivationCondition implements Condition {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActivationCondition.class);
	private PluginSettingsFactory pluginSettingsFactory;
	private TransactionTemplate transactionTemplate;
	private String pluginStorageKey;

	@Override
	public void init(Map<String, String> params) throws PluginParseException {
		this.pluginSettingsFactory = ComponentGetter.getPluginSettingsFactory();
		this.transactionTemplate = ComponentGetter.getTransactionTemplate();
		this.pluginStorageKey = ComponentGetter.getPluginStorageKey();
	}

	@Override
	public boolean shouldDisplay(Map<String, Object> context) {
		if (context == null) {
			LOGGER.error("Plugin settings could not be retrieved.");
			return false;
		}
		Object projectKey = context.get("projectKey");
		if (projectKey instanceof String) {
			return isActivated((String) projectKey);			
		}
		return false;
	}
	
	public boolean isActivated(String projectKey) {
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
}