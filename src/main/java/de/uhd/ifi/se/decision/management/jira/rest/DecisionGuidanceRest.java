package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.BaseRecommender;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.EvaluationRecommender;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.factory.RecommenderFactory;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.RecommendationEvaluation;

/**
 * REST resource for configuration and usage of decision guidance
 */
@Path("/decisionguidance")
public class DecisionGuidanceRest {

	@Path("/setMaxNumberRecommendations")
	@POST
	public Response setMaxNumberRecommendations(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("maxNumberRecommendations") int maxNumberRecommendations) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		if (maxNumberRecommendations < 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The maximum number of results cannot be smaller 0.")).build();
		}

		ConfigPersistenceManager.setMaxNumberRecommendations(projectKey, maxNumberRecommendations);
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

		ConfigPersistenceManager.setSimilarityThreshold(projectKey, threshold);
		return Response.ok(Status.ACCEPTED).build();
	}

	@Path("/setIrrelevantWords")
	@POST
	public Response setIrrelevantWords(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("words") String words) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		if (words.isBlank()) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "The words should not be blank"))
					.build();
		}

		ConfigPersistenceManager.setIrrelevantWords(projectKey, words);
		return Response.ok(Status.ACCEPTED).build();
	}

	@Path("/setRDFKnowledgeSource")
	@POST
	public Response setRDFKnowledgeSource(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, String rdfSourceJSON) {

		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}

		// TODO Remove JSON conversion and directly pass RDFSource object instead of
		// String (see setDefinitionOfDone method)
		Gson gson = new Gson();
		// TODO Please avoid acronyms like "RDF" and rather write complete names
		RDFSource rdfSource = gson.fromJson(rdfSourceJSON, RDFSource.class);

		if (rdfSource == null || rdfSource.getName().isBlank()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The name of the knowledge source must not be empty")).build();
		}

		try {
			int timeout = Integer.parseInt(rdfSource.getTimeout());
			if (timeout <= 0) {
				return Response.status(Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", "The timeout must be greater zero!")).build();
			}

		} catch (NumberFormatException e) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The timeout must be an Integer")).build();
		}

		for (RDFSource rdfSourceCheck : ConfigPersistenceManager.getRDFKnowledgeSources(projectKey)) {
			if (rdfSourceCheck.getName().equals(rdfSource.getName()))
				return Response.status(Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", "The name of the knowledge already exists.")).build();
		}

		ConfigPersistenceManager.setRDFKnowledgeSource(projectKey, rdfSource);
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

		ConfigPersistenceManager.deleteKnowledgeSource(projectKey, knowledgeSourceName);
		return Response.ok().build();
	}

	@Path("/updateKnowledgeSource")
	@POST
	public Response updateKnowledgeSource(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("knowledgeSourceName") String knowledgeSourceName,
			String rdfSourceJSON) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}

		Gson gson = new Gson();
		RDFSource rdfSource = gson.fromJson(rdfSourceJSON, RDFSource.class);
		if (rdfSource.getName().isBlank()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The knowledge source must not be empty.")).build();
		}

		ConfigPersistenceManager.updateKnowledgeSource(projectKey, knowledgeSourceName, rdfSource);
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

		ConfigPersistenceManager.setRDFKnowledgeSourceActivation(projectKey, knowledgeSourceName, isActivated);
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
		ConfigPersistenceManager.setProjectSource(projectKey, projectSourceKey, isActivated);
		return Response.ok().build();
	}

	@Path("/setAddRecommendationDirectly")
	@POST
	public Response setAddRecommendationDirectly(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("addRecommendationDirectly") String addRecommendationDirectly) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}

		ConfigPersistenceManager.setAddRecommendationDirectly(projectKey, Boolean.valueOf(addRecommendationDirectly));
		return Response.ok().build();
	}

	@Path("/setRecommendationInput")
	@POST
	public Response setRecommendationInput(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("recommendationInput") String recommendationInput,
			@QueryParam("isActivated") boolean isActivated) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}

		ConfigPersistenceManager.setRecommendationInput(projectKey, recommendationInput, isActivated);
		return Response.ok().build();
	}

	@Path("/resetRecommendationsForKnowledgeElement")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response resetRecommendationsForKnowledgeElement(@Context HttpServletRequest request, Long jiraIssueId) {
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

		for (KnowledgeElement element : knowledgeElementsInJiraIssue) {
			if (element.getStatus() == KnowledgeStatus.RECOMMENDED) {
				persistenceManager.deleteKnowledgeElement(element, user);
			}
		}
		return Response.status(Status.OK).build();
	}

	@Path("/getRecommendation")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getRecommendation(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("keyword") String keyword, @QueryParam("issueID") int jiraIssueId,
			@QueryParam("documentationLocation") String documentationLocation) {
		Response checkIfDataIsValidResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (checkIfDataIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfDataIsValidResponse;
		}

		if (keyword == null || keyword.isBlank()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The keywords should not be empty.")).build();
		}

		List<KnowledgeSource> allKnowledgeSources = ConfigPersistenceManager
				.getAllActivatedKnowledgeSources(projectKey);

		KnowledgePersistenceManager manager = KnowledgePersistenceManager.getOrCreate(projectKey);

		KnowledgeElement knowledgeElement = manager.getKnowledgeElement(jiraIssueId, documentationLocation);

		List<BaseRecommender> recommenders = new ArrayList<>();

		for (Map.Entry<String, Boolean> entry : ConfigPersistenceManager.getRecommendationInputAsMap(projectKey)
				.entrySet()) {
			if (entry.getValue()) {
				BaseRecommender recommender = RecommenderFactory
						.getRecommender(RecommenderType.valueOf(entry.getKey()));
				recommender.addKnowledgeSource(allKnowledgeSources);
				recommenders.add(recommender);
			}
		}

		List<Recommendation> recommendations = new ArrayList<>();
		for (BaseRecommender recommender : recommenders) {
			for (KnowledgeSource knowledgeSource : allKnowledgeSources) {
				if (knowledgeElement == null) {
					return Response.status(Status.BAD_REQUEST)
							.entity(ImmutableMap.of("error", "The Knowledgeelement could not be found.")).build();
				} else if (RecommenderType.KEYWORD.equals(recommender.getRecommenderType())) // TODO implement a more
					// advanced logic that is
					// extensible
					recommender.setInput(keyword);
				else {
					recommender.setInput(knowledgeElement);
				}

				try {
					recommendations.addAll(recommender.getRecommendations(knowledgeSource));
				} catch (Exception e) {
				}

			}

			if (checkIfKnowledgeSourceNotConfigured(recommender)) {
				return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
						"There is no knowledge source configured! <a href='/jira/plugins/servlet/condec/settings?projectKey="
								+ projectKey + "&category=decisionGuidance'>Configure</a>"))
						.build();
			}

			if (ConfigPersistenceManager.getAddRecommendationDirectly(projectKey))
				recommender.addToKnowledgeGraph(knowledgeElement, AuthenticationManager.getUser(request), projectKey);
		}

		return Response.ok(recommendations.stream().distinct().collect(Collectors.toList())).build();
	}

	@Path("/getRecommendationEvaluation")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getRecommendationEvaluation(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("keyword") String keyword,
			@QueryParam("issueID") int issueID, @QueryParam("knowledgeSource") String knowledgeSourceName,
			@QueryParam("kResults") int kResults, @QueryParam("documentationLocation") String documentationLocation) {
		Response checkIfDataIsValidResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (checkIfDataIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfDataIsValidResponse;
		}

		List<KnowledgeSource> allKnowledgeSources = ConfigPersistenceManager.getAllKnowledgeSources(projectKey);
		KnowledgePersistenceManager manager = KnowledgePersistenceManager.getOrCreate(projectKey);
		KnowledgeElement issue = manager.getKnowledgeElement(issueID, documentationLocation);

		if (issue == null) {
			return Response.status(Status.NOT_FOUND).entity(ImmutableMap.of("error", "The issue could not be found."))
					.build();
		}

		EvaluationRecommender recommender = new EvaluationRecommender(issue, keyword, kResults);
		RecommendationEvaluation recommendationEvaluation = recommender.evaluate(issue)
				.withKnowledgeSource(allKnowledgeSources, knowledgeSourceName).execute();

		return Response.ok(recommendationEvaluation).build();
	}

	private boolean checkIfKnowledgeSourceNotConfigured(BaseRecommender<?> recommender) {
		for (KnowledgeSource knowledgeSource : recommender.getKnowledgeSources()) {
			if (knowledgeSource.isActivated())
				return false;
		}
		return true;
	}
}