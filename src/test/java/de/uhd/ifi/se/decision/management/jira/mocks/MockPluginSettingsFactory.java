package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class MockPluginSettingsFactory implements PluginSettingsFactory{
    @Override
    public PluginSettings createSettingsForKey(String s) {
        PluginSettings settings = new MockPluginSettings();
        return settings;
    }

    @Override
    public PluginSettings createGlobalSettings() {
    	 PluginSettings settings = new MockPluginSettings();
         return settings;
    }
}
