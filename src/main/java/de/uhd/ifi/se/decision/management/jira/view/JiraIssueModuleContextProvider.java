package de.uhd.ifi.se.decision.management.jira.view;

import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.VelocityParamFactory;

/**
 * Provides content for the issue module (currently not used)
 */
public class IssueModuleContextProvider extends AbstractJiraContextProvider {

	@Override
	public Map<String, Object> getContextMap(ApplicationUser applicationUser, JiraHelper jiraHelper) {
		VelocityParamFactory velocityParamFactory = ComponentAccessor.getVelocityParamFactory();
		Map<String, Object> context = velocityParamFactory.getDefaultVelocityParams();
		return context;
	}
}
