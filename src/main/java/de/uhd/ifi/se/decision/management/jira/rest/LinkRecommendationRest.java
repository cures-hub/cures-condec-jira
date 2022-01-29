package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.user.ApplicationUser;
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

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param filterSettings
	 *            including the selected knowledge element for that link
	 *            recommendations should be made.
	 * @return {@link LinkRecommendation}s for the selected knowledge element.
	 */
	@Path("/recommendations")
	@POST
	public Response getLinkRecommendations(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (filterSettings == null || filterSettings.getSelectedElement() == null) {
			return Response.status(Status.BAD_REQUEST).entity(
					ImmutableMap.of("error", "Invalid filter settings given. Link recommendations cannot be made."))
					.build();
		}
		ContextInformation contextInformation = new ContextInformation(filterSettings.getSelectedElementFromDatabase());
		Collection<Recommendation> linkRecommendations = contextInformation.getLinkRecommendations();
		return Response.ok(linkRecommendations).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param recommendation
	 *            {@link LinkRecommendation} to be discarded.
	 * @return ok if {@link LinkRecommendation} was successfully discarded.
	 */
	@Path("/discard")
	@POST
	public Response discardRecommendation(@Context HttpServletRequest request, LinkRecommendation recommendation) {
		if (recommendation == null || recommendation.getBothElements().contains(null)) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The recommendation to discard is not valid.")).build();
		}
		DiscardedRecommendationPersistenceManager.saveDiscardedRecommendation(recommendation);
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param recommendation
	 *            discarded {@link LinkRecommendation} that should not be discarded
	 *            anymore.
	 * @return ok if discarding the {@link LinkRecommendation} was successfully
	 *         undone.
	 */
	@Path("/undo-discard")
	@POST
	public Response undoDiscardRecommendation(@Context HttpServletRequest request, LinkRecommendation recommendation) {
		if (recommendation == null || recommendation.getBothElements().contains(null)) {
			return Response.status(Status.BAD_REQUEST).entity(
					ImmutableMap.of("error", "The recommendation for that discarding should be undone is not valid."))
					.build();
		}
		DiscardedRecommendationPersistenceManager.removeDiscardedRecommendation(recommendation);
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param threshold
	 *            minimum similarity necessary to create a recommendation.
	 *            Recommendations need to be more similar to the original element(s)
	 *            than this threshold.
	 * @return ok if the threshold was successfully saved.
	 */
	@Path("/configuration/{projectKey}/threshold")
	@POST
	public Response setMinimumRecommendationScore(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, double threshold) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		if (1. < threshold || threshold < 0.) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The minimum of the score value is invalid.")).build();
		}

		LinkRecommendationConfiguration linkRecommendationConfiguration = ConfigPersistenceManager
				.getLinkRecommendationConfiguration(projectKey);
		linkRecommendationConfiguration.setMinProbability(threshold);
		ConfigPersistenceManager.saveLinkRecommendationConfiguration(projectKey, linkRecommendationConfiguration);
		return Response.ok().build();
	}

	@Path("/configuration/{projectKey}")
	@GET
	public Response getLinkRecommendationConfiguration(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		LinkRecommendationConfiguration linkRecommendationConfiguration = ConfigPersistenceManager
				.getLinkRecommendationConfiguration(projectKey);
		return Response.ok(linkRecommendationConfiguration).build();
	}
}
