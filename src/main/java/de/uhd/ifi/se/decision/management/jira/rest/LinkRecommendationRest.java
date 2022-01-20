package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.recommendation.DiscardedRecommendationPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendationConfiguration;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.ContextInformation;

/**
 * REST resource for link recommendation and duplicate recognition (including
 * its configuration).
 */
@Path("/link-recommendation")
public class LinkRecommendationRest {

	@Path("/recommendations")
	@POST
	public Response getLinkRecommendations(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (filterSettings == null || filterSettings.getSelectedElement() == null) {
			return Response.status(Status.BAD_REQUEST).entity(
					ImmutableMap.of("error", "Invalid filter settings given. Link recommendations cannot be made."))
					.build();
		}
		ContextInformation contextInformation = new ContextInformation(filterSettings.getSelectedElement());
		Collection<Recommendation> linkRecommendations = contextInformation.getLinkRecommendations();
		return Response.ok(linkRecommendations).build();
	}

	@Path("/discardRecommendation")
	@POST
	public Response discardRecommendation(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, LinkRecommendation recommendation) {
		if (recommendation == null || recommendation.getBothElements().contains(null)) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The recommendation to discard is not valid.")).build();
		}
		recommendation.setProject(projectKey);

		long databaseId = DiscardedRecommendationPersistenceManager.saveDiscardedRecommendation(recommendation);

		if (databaseId == -1) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The recommendation could not be discarded.")).build();
		}

		return Response.status(Status.OK).build();
	}

	@Path("/undoDiscardRecommendation")
	@POST
	public Response undoDiscardRecommendation(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, LinkRecommendation recommendation) {
		if (recommendation == null || recommendation.getBothElements().contains(null)) {
			return Response.status(Status.BAD_REQUEST).entity(
					ImmutableMap.of("error", "The recommendation for that discarding should be undone is not valid."))
					.build();
		}
		recommendation.setProject(projectKey);
		DiscardedRecommendationPersistenceManager.removeDiscardedRecommendation(recommendation);
		return Response.status(Status.OK).build();
	}

	// --------------------
	// Configuration
	// --------------------

	@Path("/setMinimumLinkSuggestionProbability")
	@POST
	public Response setMinimumRecommendationScore(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("minLinkSuggestionProbability") double minLinkSuggestionProbability) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		if (1. < minLinkSuggestionProbability || minLinkSuggestionProbability < 0.) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The minimum of the score value is invalid.")).build();
		}

		LinkRecommendationConfiguration linkSuggestionConfiguration = ConfigPersistenceManager
				.getLinkRecommendationConfiguration(projectKey);
		linkSuggestionConfiguration.setMinProbability(minLinkSuggestionProbability);
		ConfigPersistenceManager.saveLinkRecommendationConfiguration(projectKey, linkSuggestionConfiguration);
		return Response.ok().build();
	}
}
