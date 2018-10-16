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
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;

/**
 * Renders the administration page to change the plug-in configuration of a
 * single project
 */
public class SettingsOfSingleProject extends AbstractSettingsServlet {

	private static final long serialVersionUID = 8699708658914306058L;
	private static final String TEMPLATEPATH = "templates/settingsForSingleProject.vm";

	@Inject
	public SettingsOfSingleProject(@ComponentImport UserManager userManager,
			@ComponentImport LoginUriProvider loginUriProvider, @ComponentImport TemplateRenderer renderer) {
		super(userManager, loginUriProvider, renderer);
	}

	@Override
	protected boolean isValidUser(HttpServletRequest request) {
		String projectKey = request.getParameter("projectKey");
		String username = userManager.getRemoteUsername(request);
		return isProjectAdmin(username, projectKey);
	}

	private boolean isProjectAdmin(String username, String projectKey) {
		if (username == null || projectKey == null) {
			LOGGER.error("Username or project key in SettingsOfSingleProject is null.");
			return false;
		}
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(username);
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);

		ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager.class);
		Collection<ProjectRole> roles = projectRoleManager.getProjectRoles(user, project);
		if (roles == null) {
			LOGGER.error("User roles are not set correctly.");
			return false;
		}
		for (ProjectRole role : roles) {
			if (role.getName().equalsIgnoreCase("Administrators")) {
				return true;
			}
		}
		return false;
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

		IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);
		Collection<IssueType> types = issueTypeSchemeManager.getIssueTypesForProject(project);
		Set<String> issueTypes = new HashSet<String>();
		for (IssueType type : types) {
			issueTypes.add(type.getName());
		}

		Map<String, Object> velocityParameters = new ConcurrentHashMap<String, Object>();
		velocityParameters.put("projectKey", projectKey);
		velocityParameters.put("project", decisionKnowledgeProject);
		velocityParameters.put("issueTypes", issueTypes);
		velocityParameters.put("imageFolderUrl", ComponentGetter.getUrlOfImageFolder());
		velocityParameters.put("requestUrl", request.getRequestURL());
		
		return velocityParameters;
	}
}