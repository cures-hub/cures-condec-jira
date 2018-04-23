package de.uhd.ifi.se.decision.documentation.jira.config;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.documentation.jira.model.JiraProject;

/**
 * @description Renders the administration page to change plug-in configuration
 */
@Scanned
public class AdminServlet extends HttpServlet {

	private static final long serialVersionUID = 4640871992639394730L;

	private static final Logger LOGGER = LoggerFactory.getLogger(AdminServlet.class);

	@ComponentImport
	private UserManager userManager;
	@ComponentImport
	private LoginUriProvider loginUriProvider;
	@ComponentImport
	private TemplateRenderer renderer;

	@Inject
	public AdminServlet(@ComponentImport UserManager userManager, @ComponentImport LoginUriProvider loginUriProvider,
			@ComponentImport TemplateRenderer renderer) {
		this.userManager = userManager;
		this.loginUriProvider = loginUriProvider;
		this.renderer = renderer;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if (request == null || response == null) {
			LOGGER.error("Request or response in AdminServlet is null");
			return;
		}
		String username = userManager.getRemoteUsername(request);
		if (username == null || !userManager.isSystemAdmin(username)) {
			redirectToLogin(request, response);
			return;
		}
		Map<String, JiraProject> configMap = createConfigMap();
		Map<String, Object> velocityParams = new HashMap<String, Object>();
		response.setContentType("text/html;charset=utf-8");
		velocityParams.put("requestUrl", request.getRequestURL());
		velocityParams.put("projectsMap", configMap);
		renderer.render("templates/admin.vm", velocityParams, response.getWriter());
	}

	public Map<String, JiraProject> createConfigMap() {
		Map<String, JiraProject> configMap = new HashMap<String, JiraProject>();
		for (Project project : ComponentAccessor.getProjectManager().getProjects()) {
			String projectKey = project.getKey();
			String projectName = project.getName();
			JiraProject jiraProject = new JiraProject(projectKey, projectName, Config.isActivated(projectKey), Config.isIssueStrategy(projectKey));
			configMap.put(projectKey, jiraProject);
		}
		return configMap;
	}

	private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
		LOGGER.info("User with Name('{}') tried to access AdminServlet and has been redirected to Login.",
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