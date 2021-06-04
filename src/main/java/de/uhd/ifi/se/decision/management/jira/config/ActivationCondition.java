package de.uhd.ifi.se.decision.management.jira.config;

import java.util.Map;

import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Condition for the activation of the plugin. Determines whether the plugin is
 * displayed for a project. Is used in atlassian-plugin.xml
 */
public class ActivationCondition extends AbstractWebCondition {

	@Override
	public void init(Map<String, String> params) {
		// method is not used in this plug-in
	}

	@Override
	public boolean shouldDisplay(ApplicationUser applicationUser, JiraHelper jiraHelper) {
		String projectKey = jiraHelper.getProject().getKey();
		return ConfigPersistenceManager.getBasicConfiguration(projectKey).isActivated();
	}
}