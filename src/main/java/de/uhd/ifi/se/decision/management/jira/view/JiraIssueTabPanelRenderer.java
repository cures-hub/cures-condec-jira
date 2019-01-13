package de.uhd.ifi.se.decision.management.jira.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Renders the JIRA issue tab panel.
 */
public class JiraIssueTabPanelRenderer extends AbstractIssueTabPanel implements IssueTabPanel {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraIssueTabPanelRenderer.class);

	@Override
	public List<IssueAction> getActions(Issue issue, ApplicationUser remoteUser) {
		List<IssueAction> issueActions = new ArrayList<IssueAction>();
		if (issue == null || remoteUser == null) {
			LOGGER.error("Issue tab panel cannot be rendered correctly since no issue or user are provided.");
			return issueActions;
		}
		GenericMessageAction messageAction = new GenericMessageAction(getVelocityTemplate());
		issueActions.add(messageAction);
		return issueActions;
	}

	@Override
	public boolean showPanel(Issue issue, ApplicationUser remoteUser) {
		if (issue == null || remoteUser == null) {
			LOGGER.error("Issue tab panel cannot be rendered correctly since no issue or user are provided.");
			return false;
		}

		String projectKey = issue.getProjectObject().getKey();
		return ConfigPersistenceManager.isActivated(projectKey)
				&& ConfigPersistenceManager.isKnowledgeExtractedFromIssues(projectKey);
	}

	private String getVelocityTemplate() {
		ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
		String baseUrl = applicationProperties.getString(APKeys.JIRA_BASEURL);
		String webworkEncoding = applicationProperties.getString(APKeys.JIRA_WEBWORK_ENCODING);

		VelocityManager velocityManager = ComponentAccessor.getVelocityManager();
		VelocityParamFactory velocityParamFactory = ComponentAccessor.getVelocityParamFactory();

		Map<String, Object> context = velocityParamFactory.getDefaultVelocityParams();

		return (velocityManager.getEncodedBody("templates/", "tabPanel.vm", baseUrl, webworkEncoding, context));
	}

}