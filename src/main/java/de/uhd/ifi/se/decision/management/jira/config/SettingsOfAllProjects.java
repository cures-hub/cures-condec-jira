package de.uhd.ifi.se.decision.management.jira.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;

/**
 * Renders the administration page to change the plug-in configuration of all
 * projects
 */
public class SettingsOfAllProjects extends AbstractSettingsServlet {

	private static final long serialVersionUID = 4640871992639394730L;
	private static final String TEMPLATEPATH = "templates/settingsForAllProjects.vm";

	@Inject
	public SettingsOfAllProjects(@ComponentImport LoginUriProvider loginUriProvider,
			@ComponentImport TemplateRenderer renderer) {
		super(loginUriProvider, renderer);
	}

	@Override
	protected boolean isValidUser(HttpServletRequest request) {
		return AuthenticationManager.isSystemAdmin(request);
	}

	@Override
	protected String getTemplatePath() {
		return TEMPLATEPATH;
	}

	@Override
	protected Map<String, Object> getVelocityParameters(HttpServletRequest request) {
		if (request == null) {
			return new ConcurrentHashMap<>();
		}
		Map<String, DecisionKnowledgeProject> configMap = getProjectsMap();
		Map<String, Object> velocityParameters = new ConcurrentHashMap<String, Object>();
		velocityParameters.put("requestUrl", request.getRequestURL());
		velocityParameters.put("projectsMap", configMap);

		velocityParameters.put("jiraHomeForGitAuthentication", ConfigPersistence.getOauthJiraHome());
		velocityParameters.put("requestTokenForGitAuthentication", ConfigPersistence.getRequestToken());
		velocityParameters.put("privateKeyForGitAuthentication", ConfigPersistence.getPrivateKey());
		velocityParameters.put("consumerKeyForGitAuthentication", ConfigPersistence.getConsumerKey());
		velocityParameters.put("secretForGitAuthentication", ConfigPersistence.getSecretForOAuth());
		velocityParameters.put("accessTokenForGitAuthentication", ConfigPersistence.getAccessToken());

		return velocityParameters;
	}

	public static Map<String, DecisionKnowledgeProject> getProjectsMap() {
		Map<String, DecisionKnowledgeProject> configMap = new ConcurrentHashMap<String, DecisionKnowledgeProject>();
		for (Project project : ComponentAccessor.getProjectManager().getProjects()) {
			String projectKey = project.getKey();
			String projectName = project.getName();
			DecisionKnowledgeProject jiraProject = new DecisionKnowledgeProjectImpl(projectKey, projectName);
			configMap.put(projectKey, jiraProject);
		}
		return configMap;
	}
}