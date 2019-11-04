package de.uhd.ifi.se.decision.management.jira.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;

/**
 * Renders the administration page to change the plug-in configuration of all
 * projects
 */
public class SettingsOfAllProjects extends AbstractSettingsServlet {

	private static final long serialVersionUID = 4640871992639394730L;
	private static final String TEMPLATEPATH = "templates/settings/settingsForAllProjects.vm";

	@Inject
	public SettingsOfAllProjects(TemplateRenderer renderer) {
		super(renderer);
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

		//AutoCreateDummyProject createPr = new AutoCreateDummyProject();
		Map<String, DecisionKnowledgeProject> configMap = getProjectsMap();
		Map<String, Object> velocityParameters = new ConcurrentHashMap<String, Object>();
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