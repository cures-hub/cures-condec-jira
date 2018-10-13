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

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.StrategyProvider;

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
		
		String projectKey = request.getParameter("projectKey");
		String elementKey = request.getParameter("elementKey");
		
		AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
		DecisionKnowledgeElement element = strategy.getDecisionKnowledgeElement(elementKey);
		
		Map<String, Object> velocityParameters = new ConcurrentHashMap<String, Object>();
		velocityParameters.put("projectKey", projectKey);
		velocityParameters.put("elementKey", elementKey);
		velocityParameters.put("elementId", element.getId());
		
		templateRenderer.render("templates/decisionKnowledgePage.vm", velocityParameters, response.getWriter());
	}
}
