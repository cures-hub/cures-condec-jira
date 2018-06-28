package de.uhd.ifi.se.decision.documentation.jira.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.tabpanels.GenericMessageAction;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanel;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.VelocityParamFactory;
import com.atlassian.velocity.VelocityManager;

public class IssueTabPanelRenderer extends AbstractIssueTabPanel implements IssueTabPanel {
	public List<IssueAction> getActions(Issue issue, ApplicationUser remoteUser) {

		ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
		String baseUrl = applicationProperties.getString(APKeys.JIRA_BASEURL);
		String webworkEncoding = applicationProperties.getString(APKeys.JIRA_WEBWORK_ENCODING);

		VelocityManager velocityManager = ComponentAccessor.getVelocityManager();
		VelocityParamFactory velocityParamFactory = ComponentAccessor.getVelocityParamFactory();

		Map<String, Object> context = velocityParamFactory.getDefaultVelocityParams();

		String renderedText = velocityManager.getEncodedBody("templates/", "tabPanel.vm", baseUrl, webworkEncoding, context);

		GenericMessageAction messageAction = new GenericMessageAction(renderedText);

		List<IssueAction> issueActions = new ArrayList<IssueAction>();
		issueActions.add(messageAction);

		return issueActions;
	}

	public boolean showPanel(Issue issue, ApplicationUser remoteUser) {
		return true;
	}
}