package de.uhd.ifi.se.decision.management.jira.decXtract.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
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
import com.atlassian.jira.util.PathUtils;
import com.atlassian.jira.util.VelocityParamFactory;
import com.atlassian.velocity.VelocityManager;
import com.opensymphony.util.ClassLoaderUtil;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.decXtract.connector.ViewConnector;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import meka.classifiers.multilabel.LC;
import weka.classifiers.meta.FilteredClassifier;

/**
 * Renders the issue tab panel
 */
public class IssueTabPanelRenderer extends AbstractIssueTabPanel implements IssueTabPanel {
	private static final Logger LOGGER = LoggerFactory.getLogger(IssueTabPanelRenderer.class);

	private ViewConnector viewConnector;


	public List<IssueAction> getActions(Issue issue, ApplicationUser remoteUser) {
		if(issue == null || remoteUser == null){
			LOGGER.error("Issue tab panel cannot be rendered correctly since no issue or user are provided.");
			return new ArrayList<>();
		}
		//Initialize viewConnector with the current shown Issue
		viewConnector = new ViewConnector(issue);

		GenericMessageAction messageAction = new GenericMessageAction(getVelocityTemplate());
		List<IssueAction> issueActions = new ArrayList<IssueAction>();
		issueActions.add(messageAction);

		return issueActions;
	}

	public boolean showPanel(Issue issue, ApplicationUser remoteUser) {
		if(issue == null || remoteUser == null){
			LOGGER.error("Issue tab panel cannot be rendered correctly since no issue or user are provided.");
			return false;
		}
		String projectKey = this.getProjectKey(issue.getKey());
		return ConfigPersistence.isActivated((String) projectKey) && ConfigPersistence.isKnowledgeExtractedFromIssues((String) projectKey);
	}

	private String getProjectKey(String issueKey) {
		return issueKey.split("-")[0];
	}

	private String getVelocityTemplate() {
		ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
		String baseUrl = applicationProperties.getString(APKeys.JIRA_BASEURL);
		String webworkEncoding = applicationProperties.getString(APKeys.JIRA_WEBWORK_ENCODING);

		VelocityManager velocityManager = ComponentAccessor.getVelocityManager();
		VelocityParamFactory velocityParamFactory = ComponentAccessor.getVelocityParamFactory();

		Map<String, Object> context = velocityParamFactory.getDefaultVelocityParams();

		context = addParamsToContext(context);


		return(velocityManager.getEncodedBody("templates/", "tabPanel.vm", baseUrl, webworkEncoding, context));

	}

	private Map<String, Object> addParamsToContext(Map<String, Object> context) {
		context.put("comments",this.viewConnector.getAllTaggedComments());
		context.put("authorNames",this.viewConnector.getAllCommentsAuthorNames());
		context.put("dates", this.viewConnector.getAllCommentsDates());
		return context;
	}


}