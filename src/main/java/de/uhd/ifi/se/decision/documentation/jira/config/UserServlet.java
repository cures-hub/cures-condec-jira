package de.uhd.ifi.se.decision.documentation.jira.config;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.documentation.jira.persistence.ConfigPersistence;

public class UserServlet extends HttpServlet {

	private static final long serialVersionUID = 8458078096750655786L;
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServlet.class);

	@ComponentImport
	private UserManager userManager;
	@ComponentImport
	private LoginUriProvider loginUriProvider;
	@ComponentImport
	private TemplateRenderer templateRenderer;

	@Inject
	public UserServlet(@ComponentImport UserManager userManager, @ComponentImport LoginUriProvider loginUriProvider,
			@ComponentImport TemplateRenderer renderer) {
		this.userManager = userManager;
		this.loginUriProvider = loginUriProvider;
		this.templateRenderer = renderer;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (request == null || response == null) {
			LOGGER.error("Request or response in UserServlet is null.");
			return;
		}

		String projectKey = request.getParameter("projectKey");
		String username = userManager.getRemoteUsername(request);

		if (!isProjectAdmin(username, projectKey)) {
			redirectToLogin(request, response);
		}

		boolean isActivated = ConfigPersistence.isActivated(projectKey);
		boolean isIssueStrategy = ConfigPersistence.isIssueStrategy(projectKey);

		response.setContentType("text/html;charset=utf-8");

		Map<String, Object> velocityParams = new HashMap<String, Object>();
		velocityParams.put("requestUrl", request.getRequestURL());
		velocityParams.put("projectKey", projectKey);
		velocityParams.put("isActivated", isActivated);
		velocityParams.put("isIssueStrategy", isIssueStrategy);
		templateRenderer.render("templates/projectSettings.vm", velocityParams, response.getWriter());
	}

	private boolean isProjectAdmin(String username, String projectKey) {
		if (username == null || projectKey == null) {
			LOGGER.error("Username or project key in UserServlet is null.");
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

	private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
		LOGGER.info("User with Name('{}') tried to access UserServlet and has been redirected to Login.",
				userManager.getRemoteUsername(request));
	}

	private URI getUri(HttpServletRequest request) {
		StringBuffer builder = request.getRequestURL();
		if (request.getQueryString() != null) {
			builder.append("?");
			builder.append(request.getQueryString());
		}
		return URI.create(builder.toString());
	}

}
