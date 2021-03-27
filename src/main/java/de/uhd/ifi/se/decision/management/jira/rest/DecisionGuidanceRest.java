package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

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
		if (response.getStatus() != 200) {
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
		if (response.getStatus() != 200) {
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
		if (response.getStatus() != 200) {
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
		if (response.getStatus() != 200) {
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

		for (RDFSource rdfSourceCheck : ConfigPersistenceManager.getRDFKnowledgeSource(projectKey)) {
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
		if (response.getStatus() != 200) {
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
		if (response.getStatus() != 200) {
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
		if (response.getStatus() != 200) {
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
		if (response.getStatus() != 200) {
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
		if (response.getStatus() != 200) {
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
		if (response.getStatus() != 200) {
			return response;
		}

		ConfigPersistenceManager.setRecommendationInput(projectKey, recommendationInput, isActivated);
		return Response.ok().build();
	}
}