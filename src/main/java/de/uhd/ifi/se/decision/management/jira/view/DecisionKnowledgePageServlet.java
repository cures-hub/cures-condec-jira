package de.uhd.ifi.se.decision.management.jira.view;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;

public class DecisionKnowledgePageServlet extends HttpServlet {

	private static final long serialVersionUID = 5841622939653557805L;
	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(DecisionKnowledgePageServlet.class);

	@ComponentImport
	protected TemplateRenderer templateRenderer;
	
	@Inject
	public DecisionKnowledgePageServlet(@ComponentImport TemplateRenderer renderer) {
		super();
		this.templateRenderer = renderer;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Map<String, Object> velocityParameters = new ConcurrentHashMap<String, Object>();
		String projectKey = request.getParameter("projectKey");
		velocityParameters.put("projectKey", projectKey);
		templateRenderer.render("templates/decisionKnowledgePage.vm", velocityParameters, response.getWriter());
	}
}
