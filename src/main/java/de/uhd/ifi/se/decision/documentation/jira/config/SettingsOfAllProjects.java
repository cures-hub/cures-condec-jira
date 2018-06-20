package de.uhd.ifi.se.decision.documentation.jira.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeProject;

/**
 * @description Renders the administration page to change the plug-in configuration of all projects
 */
@Scanned
public class SettingsOfAllProjects extends AbstractSettingsServlet {

	private static final long serialVersionUID = 4640871992639394730L;
	private static final String TEMPLATEPATH = "templates/settingsForAllProjects.vm";

	@Inject
	public SettingsOfAllProjects(@ComponentImport UserManager userManager,
								 @ComponentImport LoginUriProvider loginUriProvider, @ComponentImport TemplateRenderer renderer) {
		super(userManager, loginUriProvider, renderer);
	}

	protected boolean isValidUser(HttpServletRequest request) {
		String username = userManager.getRemoteUsername(request);
		if (username == null || !userManager.isSystemAdmin(username)) {
			return false;
		}
		return true;
	}

	protected String getTemplatePath() {
		return TEMPLATEPATH;
	}

	protected Map<String, Object> getVelocityParameters(HttpServletRequest request) {
		Map<String, DecisionKnowledgeProject> configMap = Projects.getProjectsMap();
		Map<String, Object> velocityParameters = new ConcurrentHashMap<String, Object>();
		velocityParameters.put("requestUrl", request.getRequestURL());
		velocityParameters.put("projectsMap", configMap);
		return velocityParameters;
	}
}