package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class MockPluginSettingsFactory implements PluginSettingsFactory {

	public static PluginSettings pluginSettings = new MockPluginSettings();

	@Override
	public PluginSettings createSettingsForKey(String projectKey) {
		return pluginSettings;
	}

	@Override
	public PluginSettings createGlobalSettings() {
		return pluginSettings;
	}
}
