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

import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.recommendation.DiscardedRecommendationPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.DecisionGuidanceConfiguration;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.Recommender;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.Evaluator;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.RecommendationEvaluation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;
import org.apache.jena.base.Sys;

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
	 *            minimum textual similarity necessary to create a recommendation.
	 *            Recommendations need to be more textual similar to the original
	 *            element(s) than this threshold.
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
	 * @param filterSettings
	 *            including the selected decision problem and additional keywords
	 *            (optional) as the search term.
	 * @return {@link ElementRecommendation}s for the given decision problem and
	 *         keywords. Uses the settings in the
	 *         {@link DecisionGuidanceConfiguration} including the activated
	 *         {@link KnowledgeSource}s to generate the recommendations.
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

		KnowledgeElement selectedElementFromDatabase = filterSettings.getSelectedElementFromDatabase();
		System.out.print("filterSettings.getSelectedElementFromDatabase(): ");
		System.out.println(filterSettings.getSelectedElementFromDatabase());
		System.out.print("filterSettings.getSelectedElementFromDatabase().getProject(): ");
		System.out.println(filterSettings.getSelectedElementFromDatabase().getProject());
		List<Recommendation> recommendations = Recommender.getAllRecommendations(projectKey,
				selectedElementFromDatabase, filterSettings.getSearchTerm());
		if (ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey)
				.isRecommendationAddedToKnowledgeGraph()) {
			Recommender.addToKnowledgeGraph(selectedElementFromDatabase, AuthenticationManager.getUser(request),
					recommendations);
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
	 *            additional keywords used to query the knowledge source.
	 * @param knowledgeSourceName
	 *            name of the {@link KnowledgeSource} that is evaluated. It must
	 *            exist in the {@link DecisionGuidanceConfiguration}.
	 * @param topKResults
	 *            number of {@link ElementRecommendation}s with the highest
	 *            {@link RecommendationScore} that should be included in the
	 *            evaluation. All other recommendations are ignored.
	 * @param decisionProblemId
	 *            id of a decision problem with existing solution options
	 *            (alternatives, decision, solution, claims) used as the ground
	 *            truth/gold standard for the evaluation.
	 * @param documentationLocation
	 *            of the decision problem (e.g. Jira issue text).
	 * @return {@link RecommendationEvaluation} that contains the evaluation metrics
	 *         for one {@link KnowledgeSource} for a given decision problem and
	 *         keywords.
	 */
	@Path("/evaluation/{projectKey}")
	@GET
	public Response getRecommendationEvaluation(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, @QueryParam("keyword") String keyword,
			@QueryParam("knowledgeSource") String knowledgeSourceName, @QueryParam("kResults") int topKResults,
			@QueryParam("issueId") int decisionProblemId,
			@QueryParam("documentationLocation") String documentationLocation) {
		Response checkIfDataIsValidResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (checkIfDataIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfDataIsValidResponse;
		}

		KnowledgePersistenceManager manager = KnowledgePersistenceManager.getInstance(projectKey);
		KnowledgeElement issue = manager.getKnowledgeElement(decisionProblemId, documentationLocation);

		if (issue == null) {
			return Response.status(Status.NOT_FOUND).entity(ImmutableMap.of("error", "The issue could not be found."))
					.build();
		}

		RecommendationEvaluation recommendationEvaluation = Evaluator.evaluate(issue, keyword, topKResults,
				knowledgeSourceName);

		return Response.ok(recommendationEvaluation).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param recommendation
	 *            {@link ElementRecommendation} to be discarded.
	 * @return ok if {@link ElementRecommendation} was successfully discarded.
	 */
	@Path("/discard")
	@POST
	public Response discardRecommendation(@Context HttpServletRequest request, ElementRecommendation recommendation, String projectKey) {
		System.out.print("discardRecommendation got following projectKey argument: '");
		System.out.print(projectKey);
		System.out.println("'");
		System.out.println(
				"Running public Response discardRecommendation(@Context HttpServletRequest request, ElementRecommendation recommendation) {");
		if (recommendation == null) {
			System.out.println("Recommendation is null :/");
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The recommendation to discard is not valid.")).build();
		}
		System.out.println(recommendation.getSummary());
		System.out.println("in DecisionGuidanceRest.java:discardRecommendation");
		System.out.print("recommendation.getTarget().getProject(): ");
		System.out.println(recommendation.getTarget().getProject());
		System.out.println(
				"Calling DiscardedRecommendationPersistenceManager.saveDiscardedElementRecommendation(recommendation);");
		DiscardedRecommendationPersistenceManager.saveDiscardedElementRecommendation(recommendation, projectKey);
		System.out.println("Done. Calling Response.ok().build();");
		return Response.ok().build();
	}


	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param recommendation
	 *            previously discarded {@link ElementRecommendation} to be restored .
	 * @return ok if {@link ElementRecommendation} was successfully un-discarded.
	 */
	@Path("/undo-discard")
	@POST
	public Response undiscardRecommendation(@Context HttpServletRequest request, ElementRecommendation recommendation, String projectKey) {
		System.out.println(
				"Running public Response undiscardRecommendation(@Context HttpServletRequest request, ElementRecommendation recommendation) {");
		if (recommendation == null) {
			System.out.println("Recommendation is null :/");
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The recommendation to undiscard is not valid.")).build();
		}
		System.out.println(recommendation.getSummary());
		System.out.println(
				"Calling DiscardedRecommendationPersistenceManager.saveDiscardedElementRecommendation(recommendation);");
		DiscardedRecommendationPersistenceManager.removeDiscardedElementRecommendation(recommendation, projectKey);
		System.out.println("Done. Calling Response.ok().build();");
		return Response.ok().build();
	}

}