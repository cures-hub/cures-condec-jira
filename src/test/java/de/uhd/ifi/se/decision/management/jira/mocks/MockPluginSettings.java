package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.sal.api.pluginsettings.PluginSettings;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;

public class MockPluginSettings implements PluginSettings {
	private Map<String, Object> settings;

	public final Map<String, Object> DEFAULT_SETTINGS = getDefaultSettings();

	public MockPluginSettings() {
		settings = DEFAULT_SETTINGS;
	}

	public static Map<String, Object> getDefaultSettings() {
		String subfix = ComponentGetter.PLUGIN_KEY + ".";
		Map<String, Object> settings = new HashMap<String, Object>();
		settings.put(subfix + "gitUri", TestSetUpGit.GIT_URI);
		settings.put(subfix + "webhookUrl", "http://true");
		settings.put(subfix + "isIssueStrategy", "true");
		settings.put(subfix + "isKnowledgeExtractedFromIssues", "true");
		settings.put(subfix + "isIconParsing", "true");
		return settings;
	}

	@Override
	public Object get(String parameter) {
		Object returnVal = settings.get(parameter);
		if (returnVal != null) {
			return returnVal;
		}
		return "true";
	}

	@Override
	public Object put(String parameter, Object object) {
		settings.put(ComponentGetter.PLUGIN_KEY + "." + parameter, object);
		return object;
	}

	@Override
	public Object remove(String parameter) {
		return null;
	}
}