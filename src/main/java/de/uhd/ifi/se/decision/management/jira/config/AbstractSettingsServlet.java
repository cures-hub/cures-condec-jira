package de.uhd.ifi.se.decision.management.jira.config;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Abstract super class to render the administration pages
 */
public abstract class AbstractSettingsServlet extends HttpServlet {

	private static final long serialVersionUID = 7361128574751637582L;
	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractSettingsServlet.class);

	@ComponentImport
	protected TemplateRenderer templateRenderer;

	@Inject
	public AbstractSettingsServlet(TemplateRenderer renderer) {
		super();
		this.templateRenderer = renderer;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (!isValidParameters(request, response)) {
			return;
		}

		if (!isValidUser(request)) {
			redirectToLogin(request, response);
			return;
		}

		response.setContentType("text/html;charset=utf-8");

		templateRenderer.render(getTemplatePath(), getVelocityParameters(request), response.getWriter());
	}

	protected boolean isValidParameters(HttpServletRequest request, HttpServletResponse response) {
		if (request == null || response == null) {
			LOGGER.error("Request or response in settings servlet is null.");
			return false;
		} else {
			return true;
		}
	}

	protected abstract boolean isValidUser(HttpServletRequest request);

	private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(
				ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + "/login.jsp");
		LOGGER.info("User with name('{}') tried to change the project settings and was redirected to login.",
				AuthenticationManager.getUsername(request));
	}

	protected abstract String getTemplatePath();

	protected abstract Map<String, Object> getVelocityParameters(HttpServletRequest request);
}
