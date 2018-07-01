package de.uhd.ifi.se.decision.management.jira.config;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

/**
 * Abstract super class to render the administration pages
 */
public abstract class AbstractSettingsServlet extends HttpServlet {

	private static final long serialVersionUID = 7361128574751637582L;
	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractSettingsServlet.class);

	@ComponentImport
	protected UserManager userManager;
	@ComponentImport
	protected LoginUriProvider loginUriProvider;
	@ComponentImport
	protected TemplateRenderer templateRenderer;

	@Inject
	public AbstractSettingsServlet(@ComponentImport UserManager userManager, @ComponentImport LoginUriProvider loginUriProvider,
								   @ComponentImport TemplateRenderer renderer) {
		super();
		this.userManager = userManager;
		this.loginUriProvider = loginUriProvider;
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
		response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
		LOGGER.info("User with name('{}') tried to change the project settings and wsa redirected to login.",
				userManager.getRemoteUsername(request));
	}

	private URI getUri(HttpServletRequest request) {
		StringBuffer builder = request.getRequestURL();
		if (request.getQueryString() != null) {
			builder.append('?');
			builder.append(request.getQueryString());
		}
		return URI.create(builder.toString());
	}

	protected abstract String getTemplatePath();

	protected abstract Map<String, Object> getVelocityParameters(HttpServletRequest request);
}
