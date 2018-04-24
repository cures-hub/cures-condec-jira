package de.uhd.ifi.se.decision.documentation.jira.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;

import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.persistence.ConfigPersistence;

/**
 * @description Condition for side bar link to plug-in. Determines whether link
 *              is displayed for a project. Is used in atlassian-plugin.xml
 */
public class ActivationCondition implements Condition {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActivationCondition.class);

	@Override
	public void init(Map<String, String> params) throws PluginParseException {
		ComponentGetter.getPluginStorageKey();
	}

	@Override
	public boolean shouldDisplay(Map<String, Object> context) {
		if (context == null) {
			LOGGER.error("Plugin settings could not be retrieved.");
			return false;
		}
		Object projectKey = context.get("projectKey");
		if (projectKey instanceof String) {
			return ConfigPersistence.isActivated((String) projectKey);	
		}
		return false;
	}
}