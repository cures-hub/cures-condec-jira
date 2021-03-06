package de.uhd.ifi.se.decision.management.jira.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Renders the administration page to change the plug-in's activation of all
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

		List<DecisionKnowledgeProject> projects = getProjects();
		Map<String, Object> velocityParameters = new ConcurrentHashMap<>();
		velocityParameters.put("request", request);
		velocityParameters.put("projects", projects);

		return velocityParameters;
	}

	public static List<DecisionKnowledgeProject> getProjects() {
		List<DecisionKnowledgeProject> projects = new ArrayList<>();
		for (Project project : ComponentAccessor.getProjectManager().getProjects()) {
			DecisionKnowledgeProject jiraProject = new DecisionKnowledgeProject(project);
			projects.add(jiraProject);
		}
		return projects;
	}
}