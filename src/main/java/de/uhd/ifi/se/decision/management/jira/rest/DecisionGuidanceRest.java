package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.DecisionGuidanceConfiguration;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.Recommender;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.Evaluator;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.RecommendationEvaluation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;

/**
 * REST resource for configuration and usage of decision guidance
 */
@Path("/decision-guidance")
public class DecisionGuidanceRest {

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param maxNumberOfRecommendations
	 *            maximum number of recommendations from an external knowledge
	 *            source that is shown to the user.
	 * @return ok if the maximal number of recommendations was successfully saved.
	 */
	@Path("/configuration/{projectKey}/max-recommendations")
	@POST
	public Response setMaxNumberOfRecommendations(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, int maxNumberOfRecommendations) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		if (maxNumberOfRecommendations < 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The maximum number of recommendations cannot be negative."))
					.build();
		}

		DecisionGuidanceConfiguration decisionGuidanceConfiguration = ConfigPersistenceManager
				.getDecisionGuidanceConfiguration(projectKey);
		decisionGuidanceConfiguration.setMaxNumberOfRecommendations(maxNumberOfRecommendations);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param threshold
	 *            minimum similarity score for textual similarity. Recommendations
	 *            need to be more textual similar to the original element(s) than
	 *            this threshold.
	 * @return ok if the similarity threshold was successfully saved.
	 */
	@Path("/configuration/{projectKey}/similarity-threshold")
	@POST
	public Response setSimilarityThreshold(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, double threshold) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		if (threshold < 0 || threshold > 1) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The threshold must be between 0 and 1.")).build();
		}

		DecisionGuidanceConfiguration decisionGuidanceConfiguration = ConfigPersistenceManager
				.getDecisionGuidanceConfiguration(projectKey);
		decisionGuidanceConfiguration.setSimilarityThreshold(threshold);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param rdfSource
	 *            {@link RDFSource} object.
	 * @return ok if the RDF knowledge source was successfully created.
	 */
	@Path("/configuration/{projectKey}/create/rdf-source")
	@POST
	public Response createRDFKnowledgeSource(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, RDFSource rdfSource) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		if (rdfSource == null || rdfSource.getName().isBlank()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The name of the knowledge source must not be empty!")).build();
		}
		if (rdfSource.getTimeout() <= 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The timeout must be a number greater than zero!")).build();
		}
		DecisionGuidanceConfiguration decisionGuidanceConfiguration = ConfigPersistenceManager
				.getDecisionGuidanceConfiguration(projectKey);
		if (decisionGuidanceConfiguration.containsRDFKnowledgeSource(rdfSource.getName())) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The name of the knowledge source already exists.")).build();
		}
		decisionGuidanceConfiguration.addRDFKnowledgeSource(rdfSource);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param knowledgeSourceName
	 *            of an {@link RDFSource}.
	 * @return ok if the connection to the RDF knowledge source was successfully
	 *         deleted. Returns a bad request or internal server error otherwise.
	 */
	@Path("/configuration/{projectKey}/rdf-source/{knowledgeSourceName}")
	@DELETE
	public Response deleteRDFKnowledgeSource(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, @PathParam("knowledgeSourceName") String knowledgeSourceName) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}

		DecisionGuidanceConfiguration decisionGuidanceConfiguration = ConfigPersistenceManager
				.getDecisionGuidanceConfiguration(projectKey);
		if (decisionGuidanceConfiguration.deleteRDFKnowledgeSource(knowledgeSourceName)) {
			ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);
			return Response.ok().build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", "The knowledge source could not be deleted.")).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param knowledgeSourceName
	 *            of an {@link RDFSource}.
	 * @param rdfSource
	 *            updated {@link RDFSource} object.
	 * @return ok if the RDF knowledge source was successfully updated.
	 */
	@Path("/configuration/{projectKey}/update/rdf-source/{knowledgeSourceName}")
	@POST
	public Response updateRDFKnowledgeSource(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, @PathParam("knowledgeSourceName") String knowledgeSourceName,
			RDFSource rdfSource) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}

		if (rdfSource.getName().isBlank()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The knowledge source must not be empty.")).build();
		}

		DecisionGuidanceConfiguration decisionGuidanceConfiguration = ConfigPersistenceManager
				.getDecisionGuidanceConfiguration(projectKey);
		decisionGuidanceConfiguration.updateRDFKnowledgeSource(knowledgeSourceName, rdfSource);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param knowledgeSourceName
	 *            name of an existing {@link RDFSource}.
	 * @param isActivated
	 *            true if {@link RDFSource} is activated.
	 * @return ok if the RDF knowledge source was successfully activated or
	 *         deactivated.
	 */
	@Path("/configuration/{projectKey}/activate/rdf-source/{knowledgeSourceName}")
	@POST
	public Response setRDFKnowledgeSourceActivated(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, @PathParam("knowledgeSourceName") String knowledgeSourceName,
			boolean isActivated) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		if (knowledgeSourceName.isBlank()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The knowledge source must not be empty.")).build();
		}

		DecisionGuidanceConfiguration decisionGuidanceConfiguration = ConfigPersistenceManager
				.getDecisionGuidanceConfiguration(projectKey);
		decisionGuidanceConfiguration.setRDFKnowledgeSourceActivation(knowledgeSourceName, isActivated);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param projectSourceKey
	 *            of a Jira project that should be used as a knowledge source.
	 * @param isActivated
	 *            true if {@link ProjectSource} should be activated.
	 * @return ok if the project knowledge source was successfully activated or
	 *         deactivated.
	 */
	@Path("/configuration/{projectKey}/activate/project-source/{projectSourceKey}")
	@POST
	public Response setProjectSource(@Context HttpServletRequest request, @PathParam("projectKey") String projectKey,
			@PathParam("projectSourceKey") String projectSourceKey, boolean isActivated) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		if (projectSourceKey.isBlank()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The Project Source must not be empty.")).build();
		}
		DecisionGuidanceConfiguration decisionGuidanceConfiguration = ConfigPersistenceManager
				.getDecisionGuidanceConfiguration(projectKey);
		decisionGuidanceConfiguration.setProjectKnowledgeSource(projectSourceKey, isActivated);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param addRecommendationDirectly
	 *            true if all recommendations for a decision problem should be
	 *            directly added to the knowledge graph.
	 * @return ok if the setting was successfully saved.
	 */
	@Path("/configuration/{projectKey}/add-recommendations-directly")
	@POST
	public Response setAddRecommendationDirectly(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, boolean addRecommendationDirectly) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}

		DecisionGuidanceConfiguration decisionGuidanceConfiguration = ConfigPersistenceManager
				.getDecisionGuidanceConfiguration(projectKey);
		decisionGuidanceConfiguration.setRecommendationAddedToKnowledgeGraph(addRecommendationDirectly);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param jiraIssueId
	 *            id of a Jira issue
	 * @return number of removed recommendations for the given Jira issue. The
	 *         recommendations are removed from the {@link KnowledgeGraph} and the
	 *         database.
	 */
	@Path("/removeRecommendationsForKnowledgeElement")
	@POST
	public Response removeRecommendationsForKnowledgeElement(@Context HttpServletRequest request, Long jiraIssueId) {
		if (request == null || jiraIssueId == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Resetting decision knowledge documented in the description and comments of a Jira issue failed due to a bad request."))
					.build();
		}
		Issue jiraIssue = ComponentAccessor.getIssueManager().getIssueObject(jiraIssueId);
		if (jiraIssue == null) {
			return Response.status(Status.NOT_FOUND)
					.entity(ImmutableMap.of("error", "Resetting all recommendations for this Jira issue failed "
							+ "because the Jira issue could not be found."))
					.build();
		}
		String projectKey = jiraIssue.getProjectObject().getKey();
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance(projectKey);
		ApplicationUser user = AuthenticationManager.getUser(request);
		List<KnowledgeElement> knowledgeElementsInJiraIssue = persistenceManager.getJiraIssueTextManager()
				.getElementsInJiraIssue(jiraIssueId);

		int numberOfRemovedElements = 0;
		for (KnowledgeElement element : knowledgeElementsInJiraIssue) {
			if (element.getStatus() == KnowledgeStatus.RECOMMENDED) {
				persistenceManager.deleteKnowledgeElement(element, user);
				numberOfRemovedElements++;
			}
		}
		return Response.ok(numberOfRemovedElements).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param filterSettings
	 *            including the selected decision problem and additional keywords
	 *            (optional) as the search term.
	 * @return {@link ElementRecommendation}s for the given decision problem and
	 *         keywords. Uses the activated {@link KnowledgeSource}s in the
	 *         {@link DecisionGuidanceConfiguration} to generate the
	 *         recommendations.
	 */
	@Path("/recommendations")
	@POST
	public Response getRecommendations(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (filterSettings == null || filterSettings.getSelectedElement() == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Invalid filter settings given. Decision guidance recommendation cannot be made.")).build();
		}
		String projectKey = filterSettings.getProjectKey();
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}

		KnowledgeElement selectedElement = filterSettings.getSelectedElement();
		List<Recommendation> recommendations = Recommender.getAllRecommendations(projectKey, selectedElement,
				filterSettings.getSearchTerm());
		if (ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey)
				.isRecommendationAddedToKnowledgeGraph()) {
			Recommender.addToKnowledgeGraph(selectedElement, AuthenticationManager.getUser(request), recommendations);
		}
		return Response.ok(recommendations).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param keyword
	 * @param knowledgeSourceName
	 * @param kResults
	 * @param issueId
	 * @param documentationLocation
	 * @return
	 */
	@Path("/recommendationEvaluation")
	@GET
	public Response getRecommendationEvaluation(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("keyword") String keyword,
			@QueryParam("knowledgeSource") String knowledgeSourceName, @QueryParam("kResults") int kResults,
			@QueryParam("issueId") int issueId, @QueryParam("documentationLocation") String documentationLocation) {
		Response checkIfDataIsValidResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (checkIfDataIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfDataIsValidResponse;
		}

		KnowledgePersistenceManager manager = KnowledgePersistenceManager.getInstance(projectKey);
		KnowledgeElement issue = manager.getKnowledgeElement(issueId, documentationLocation);

		if (issue == null) {
			return Response.status(Status.NOT_FOUND).entity(ImmutableMap.of("error", "The issue could not be found."))
					.build();
		}

		RecommendationEvaluation recommendationEvaluation = Evaluator.evaluate(issue, keyword, kResults,
				knowledgeSourceName);

		return Response.ok(recommendationEvaluation).build();
	}
}