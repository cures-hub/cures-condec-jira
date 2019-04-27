package de.uhd.ifi.se.decision.management.jira.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.ClassificationTrainer;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.ClassificationTrainerImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Renders the administration page to change the plug-in configuration of a
 * single project
 */
public class SettingsOfSingleProject extends AbstractSettingsServlet {

	private static final long serialVersionUID = 8699708658914306058L;
	private static final String TEMPLATEPATH = "templates/settingsForSingleProject.vm";

	@Inject
	public SettingsOfSingleProject(TemplateRenderer renderer) {
		super(renderer);
	}

	@Override
	public boolean isValidUser(HttpServletRequest request) {
		return AuthenticationManager.isProjectAdmin(request);
	}

	@Override
	protected String getTemplatePath() {
		return TEMPLATEPATH;
	}

	@Override
	protected Map<String, Object> getVelocityParameters(HttpServletRequest request) {
		if (request == null) {
			return new ConcurrentHashMap<String, Object>();
		}
		String projectKey = request.getParameter("projectKey");
		DecisionKnowledgeProject decisionKnowledgeProject = new DecisionKnowledgeProjectImpl(projectKey);

		Set<String> issueTypes = getJiraIssueTypeNames(projectKey);

		ClassificationTrainer.copyDefaultTrainingDataToFile(ClassificationTrainer.DEFAULT_TRAINING_DATA);
		ClassificationTrainer trainer = new ClassificationTrainerImpl(projectKey);		

		Map<String, Object> velocityParameters = new ConcurrentHashMap<String, Object>();
		velocityParameters.put("projectKey", projectKey);
		velocityParameters.put("project", decisionKnowledgeProject);
		velocityParameters.put("issueTypes", issueTypes);
		velocityParameters.put("imageFolderUrl", ComponentGetter.getUrlOfImageFolder());
		velocityParameters.put("requestUrl", request.getRequestURL());
		velocityParameters.put("rootTypes", ConfigPersistenceManager.getEnabledWebhookTypes(projectKey));
		velocityParameters.put("arffFiles", trainer.getArffFileNames());
		velocityParameters.put("selectedArffFile", ConfigPersistenceManager.getArffFileForClassifier(projectKey));
		return velocityParameters;
	}
	
	private Set<String> getJiraIssueTypeNames(String projectKey) {
		IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);
		Collection<IssueType> types = issueTypeSchemeManager.getIssueTypesForProject(project);
		Set<String> issueTypes = new HashSet<String>();
		for (IssueType type : types) {
			issueTypes.add(type.getName());
		}
		return issueTypes;
	}

}