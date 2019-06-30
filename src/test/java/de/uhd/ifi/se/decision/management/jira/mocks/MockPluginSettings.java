package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.sal.api.pluginsettings.PluginSettings;

import java.util.HashMap;
import java.util.Map;

public class MockPluginSettings implements PluginSettings {
	private Map<String, Object> settings = new HashMap<>();

	@Override
	public Object get(String s) {
		Object returnVal = settings.get(s);
		return returnVal;
	}

	@Override
	public Object put(String s, Object o) {
		settings.put(s,o);
		return o;
	}

	@Override
	public Object remove(String s) {
		return null;
	}
}