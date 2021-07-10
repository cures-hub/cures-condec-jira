package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.DecisionGuidanceConfiguration;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.Recommender;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.Evaluator;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.evaluation.RecommendationEvaluation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;

/**
 * REST resource for configuration and usage of decision guidance
 */
@Path("/decisionguidance")
public class DecisionGuidanceRest {

	@Path("/setMaxNumberOfRecommendations")
	@POST
	public Response setMaxNumberOfRecommendations(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("maxNumberOfRecommendations") int maxNumberOfRecommendations) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		if (maxNumberOfRecommendations < 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The maximum number of results cannot be smaller 0.")).build();
		}

		DecisionGuidanceConfiguration decisionGuidanceConfiguration = ConfigPersistenceManager
				.getDecisionGuidanceConfiguration(projectKey);
		decisionGuidanceConfiguration.setMaxNumberOfRecommendations(maxNumberOfRecommendations);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);
		return Response.ok().build();
	}

	@Path("/setSimilarityThreshold")
	@POST
	public Response setSimilarityThreshold(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("threshold") double threshold) {
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
		return Response.ok(Status.ACCEPTED).build();
	}

	@Path("/createRDFKnowledgeSource")
	@POST
	public Response createRDFKnowledgeSource(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, RDFSource rdfSource) {
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

	@Path("/deleteKnowledgeSource")
	@POST
	public Response deleteKnowledgeSource(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("knowledgeSourceName") String knowledgeSourceName) {
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
		decisionGuidanceConfiguration.deleteRDFKnowledgeSource(knowledgeSourceName);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);
		return Response.ok().build();
	}

	@Path("/updateKnowledgeSource")
	@POST
	public Response updateKnowledgeSource(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("knowledgeSourceName") String knowledgeSourceName,
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

	@Path("/setKnowledgeSourceActivated")
	@POST
	public Response setKnowledgeSourceActivated(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("knowledgeSourceName") String knowledgeSourceName,
			@QueryParam("isActivated") boolean isActivated) {
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

	@Path("/setProjectSource")
	@POST
	public Response setProjectSource(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("projectSourceKey") String projectSourceKey, @QueryParam("isActivated") boolean isActivated) {
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

	@Path("/setAddRecommendationDirectly")
	@POST
	public Response setAddRecommendationDirectly(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("addRecommendationDirectly") boolean addRecommendationDirectly) {
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
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);
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
		return Response.status(Status.OK).entity(numberOfRemovedElements).build();
	}

	/**
	 * Get all recommendations for a Jira issue
	 *
	 * @param request
	 * @param projectKey
	 * @param issueKey
	 * @return Map<KnoweledgeElement, List < Recommendation>> A map of
	 *         knowledgeElements (currently only Issues), each with a list of
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

		Map<Long, List<Recommendation>> results = new HashMap<>();

		if (selectedElement.getType().getSuperType() != KnowledgeType.PROBLEM) {
			// the selected element is not a decision problem, but e.g. a requirement
			// we need to get all decision problems related to the selected element
			filterSettings.setCreateTransitiveLinks(true);
			filterSettings.setOnlyDecisionKnowledgeShown(true);
			filterSettings.setKnowledgeTypes(Set.of("Issue", "Problem", "Goal"));
			Set<KnowledgeElement> filteredGraph = new FilteringManager(filterSettings)
					.getElementsMatchingFilterSettings();
			filteredGraph.remove(selectedElement);
			for (KnowledgeElement element : filteredGraph) {
				List<Recommendation> recommendations = Recommender.getAllRecommendations(projectKey, element,
						element.getSummary());
				results.put(element.getId(), recommendations);
			}
		} else {
			List<Recommendation> recommendations = Recommender.getAllRecommendations(projectKey, selectedElement,
					selectedElement.getSummary());
			results.put(selectedElement.getId(), recommendations);
			if (ConfigPersistenceManager.getDecisionGuidanceConfiguration(projectKey)
					.isRecommendationAddedToKnowledgeGraph()) {
				Recommender.addToKnowledgeGraph(selectedElement, AuthenticationManager.getUser(request),
						recommendations);
			}
		}
		return Response.ok(results).build();
	}

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

		KnowledgePersistenceManager manager = KnowledgePersistenceManager.getOrCreate(projectKey);
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