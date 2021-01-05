package de.uhd.ifi.se.decision.management.jira.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.atlassian.templaterenderer.TemplateRenderer;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.DecisionKnowledgeClassifier;
import de.uhd.ifi.se.decision.management.jira.classification.FileManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesMapping;

/**
 * Renders the administration page to change the plug-in configuration of a
 * single project
 */
public class SettingsOfSingleProject extends AbstractSettingsServlet {

	private static final long serialVersionUID = 8699708658914306058L;
	private static final String TEMPLATEPATH = "templates/settings/settingsForSingleProject.vm";

	@Inject
	public SettingsOfSingleProject(TemplateRenderer renderer) {
		super(renderer);
	}

	@Override
	public boolean isValidUser(HttpServletRequest request) {
		return AuthenticationManager.isProjectAdmin(request);
	}

	@Override
	protected String getTemplatePath() {
		return TEMPLATEPATH;
	}

	@Override
	protected Map<String, Object> getVelocityParameters(HttpServletRequest request) {
		Map<String, Object> velocityParameters = new ConcurrentHashMap<String, Object>();
		if (request == null) {
			return velocityParameters;
		}

		String projectKey = request.getParameter("projectKey");
		DecisionKnowledgeProject decisionKnowledgeProject = new DecisionKnowledgeProject(projectKey);
		velocityParameters.put("request", request);
		velocityParameters.put("project", decisionKnowledgeProject);
		velocityParameters.put("imageFolderUrl", ComponentGetter.getUrlOfImageFolder());

		velocityParameters.put("definitionOfDone", ConfigPersistenceManager.getDefinitionOfDone(projectKey));

		velocityParameters.put("criteriaQuery", ConfigPersistenceManager.getDecisionTableCriteriaQuery(projectKey));

		velocityParameters.put("rootTypes", ConfigPersistenceManager.getEnabledWebhookTypes(projectKey));
		velocityParameters.put("trainingFiles", FileManager.getTrainingFileNames());
		velocityParameters.put("selectedTrainingFile",
				ConfigPersistenceManager.getTrainingFileForClassifier(projectKey));
		velocityParameters.put("isClassifierTraining", DecisionKnowledgeClassifier.getInstance().isTraining());
		velocityParameters.put("isClassifierTrained", DecisionKnowledgeClassifier.getInstance().isTrained());

		velocityParameters.put("releaseNotesMapping", new ReleaseNotesMapping(projectKey));

		velocityParameters.put("minLengthDuplicate", ConfigPersistenceManager.getFragmentLength(projectKey));
		velocityParameters.put("minProbabilityLink", ConfigPersistenceManager.getMinLinkSuggestionScore(projectKey));

		velocityParameters.put("maxNumberRecommendations",
				ConfigPersistenceManager.getMaxNumberRecommendations(projectKey));

		velocityParameters.put("rdfSources", ConfigPersistenceManager.getRDFKnowledgeSource(projectKey));
		velocityParameters.put("projectSources",
				ConfigPersistenceManager.getProjectSourcesForActiveProjects(projectKey));

		velocityParameters.put("addRecommendationDirectly", ConfigPersistenceManager.getAddRecommendationDirectly(projectKey));
		velocityParameters.put("recommendationInput", ConfigPersistenceManager.getRecommendationInputAsMap(projectKey));

		return velocityParameters;
	}

}