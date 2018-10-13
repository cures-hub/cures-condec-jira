package de.uhd.ifi.se.decision.management.jira.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;

/**
 * Renders the administration page to change the plug-in configuration of all
 * projects
 */
public class SettingsOfAllProjects extends AbstractSettingsServlet {

	private static final long serialVersionUID = 4640871992639394730L;
	private static final String TEMPLATEPATH = "templates/settingsForAllProjects.vm";

	@Inject
	public SettingsOfAllProjects(@ComponentImport UserManager userManager,
			@ComponentImport LoginUriProvider loginUriProvider, @ComponentImport TemplateRenderer renderer) {
		super(userManager, loginUriProvider, renderer);
	}

	@Override
	protected boolean isValidUser(HttpServletRequest request) {
		String username = userManager.getRemoteUsername(request);
		return username != null && userManager.isSystemAdmin(username);
	}

	@Override
	protected String getTemplatePath() {
		return TEMPLATEPATH;
	}

	@Override
	protected Map<String, Object> getVelocityParameters(HttpServletRequest request) {
		Map<String, Object> velocityParameters = new ConcurrentHashMap<String, Object>();
		if (request == null) {
			return velocityParameters;
		}
		Map<String, DecisionKnowledgeProject> configMap = getProjectsMap();
		velocityParameters.put("requestUrl", request.getRequestURL());
		velocityParameters.put("projectsMap", configMap);
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