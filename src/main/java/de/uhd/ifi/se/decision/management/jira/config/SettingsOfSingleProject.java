package de.uhd.ifi.se.decision.management.jira.config;

import com.atlassian.templaterenderer.TemplateRenderer;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.FileTrainer;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineFileTrainerImpl;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesMapping;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

		// TODO -- Start
		// TODO: check if directory exists (last folder) and create it if not!
		FileTrainer.copyDefaultTrainingDataToFile();
		Preprocessor.copyDefaultPreprocessingDataToFile();
		// TODO -- End

		String projectKey = request.getParameter("projectKey");
		DecisionKnowledgeProject decisionKnowledgeProject = new DecisionKnowledgeProject(projectKey);
		FileTrainer trainer = new OnlineFileTrainerImpl(projectKey);

		velocityParameters.put("request", request);
		velocityParameters.put("project", decisionKnowledgeProject);
		velocityParameters.put("imageFolderUrl", ComponentGetter.getUrlOfImageFolder());

		velocityParameters.put("definitionOfDone", ConfigPersistenceManager.getDefinitionOfDone(projectKey));

		velocityParameters.put("criteriaQuery", ConfigPersistenceManager.getDecisionTableCriteriaQuery(projectKey));

		velocityParameters.put("rootTypes", ConfigPersistenceManager.getEnabledWebhookTypes(projectKey));
		velocityParameters.put("arffFiles", trainer.getTrainingFileNames());
		velocityParameters.put("selectedArffFile", ConfigPersistenceManager.getArffFileForClassifier(projectKey));
		velocityParameters.put("isClassifierTraining", trainer.getClassifier().isTraining());
		velocityParameters.put("isClassifierTrained", trainer.getClassifier().isTrained());

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