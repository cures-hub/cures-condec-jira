package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.sal.api.pluginsettings.PluginSettings;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.TestConfigPersistenceManager;

/**
 * Mock class for plugin settings necessary to test the
 * {@link ConfigPersistenceManager} class.
 * 
 * @see TestConfigPersistenceManager
 */
public class MockPluginSettings implements PluginSettings {
	private Map<String, Object> settings;
	private final Map<String, Object> DEFAULT_SETTINGS = getDefaultSettings();

	public MockPluginSettings() {
		settings = DEFAULT_SETTINGS;
	}

	/**
	 * Returns the map of parameter name and default value, e.g.: isActivated:true
	 * 
	 * @return map of parameter name and default value, e.g.: isActivated:true
	 */
	public static Map<String, Object> getDefaultSettings() {
		String subfix = ComponentGetter.PLUGIN_KEY + ".";
		Map<String, Object> settings = new HashMap<String, Object>();
		settings.put(subfix + "webhookUrl", "http://true");
		settings.put(subfix + "webhookSecret", "myhoneybee");
		settings.put(subfix + "isWebhookEnabled", "false");
		settings.put(subfix + "users", "user");
		settings.put(subfix + "criteriaQuery", "project=CONDEC and type = Non functional requirement");
		return settings;
	}

	@Override
	public Object get(String parameter) {
		Object returnVal = settings.get(parameter);
		if (returnVal != null) {
			return returnVal;
		}
		returnVal = settings.get(ComponentGetter.PLUGIN_KEY + "." + parameter);
		if (returnVal != null) {
			return returnVal;
		}
		return "true";
	}

	@Override
	public Object put(String parameter, Object object) {
		settings.put(parameter, object);
		settings.put(ComponentGetter.PLUGIN_KEY + "." + parameter, object);
		return object;
	}

	@Override
	public Object remove(String parameter) {
		return null;
	}
}