package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.Collection;
import java.util.List;

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
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.ContextInformationProvider;

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
		ContextInformation contextInformation = new ContextInformation(filterSettings.getSelectedElementFromDatabase(),
				filterSettings.getLinkRecommendationConfig());
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

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param maxRecommendations
	 *            maximum amount of recommendations that should be received.
	 * 
	 * @return ok if maxRecommendations was successfully saved.
	 */
	@Path("/configuration/{projectKey}/recommendationmaximum")
	@POST
	public Response setMaximumRecommendations(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, int maxRecommendations) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		if (maxRecommendations < -1) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The maximum has to be a number -1 or above.")).build();
		}

		LinkRecommendationConfiguration linkRecommendationConfiguration = ConfigPersistenceManager
				.getLinkRecommendationConfiguration(projectKey);
		linkRecommendationConfiguration.setMaxRecommendations(maxRecommendations);
		ConfigPersistenceManager.saveLinkRecommendationConfiguration(projectKey, linkRecommendationConfiguration);
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param rules
	 *            {@link ContextInformationProvider}s representing the rules for
	 *            link recommendation.
	 * @return ok if the rule configuration were successfully saved.
	 */
	@Path("/configuration/{projectKey}/rules")
	@POST
	public Response setLinkRecommendationRules(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, List<ContextInformationProvider> rules) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		LinkRecommendationConfiguration linkRecommendationConfiguration = ConfigPersistenceManager
				.getLinkRecommendationConfiguration(projectKey);
		/**
		 * @issue How can we make sure that a new number of link recommendation rules in
		 *        the backend is available to the users?
		 * @decision We store all link recommendation rules if the number stored in the
		 *           settings is different to them to fix inconsistency between stored
		 *           config and new code!
		 */
		List<ContextInformationProvider> allRules = LinkRecommendationConfiguration.getAllContextInformationProviders();
		if (rules.size() != allRules.size()) {
			linkRecommendationConfiguration.setContextInformationProviders(allRules);
		} else {
			linkRecommendationConfiguration.setContextInformationProviders(rules);
		}
		ConfigPersistenceManager.saveLinkRecommendationConfiguration(projectKey, linkRecommendationConfiguration);
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @return saved {@link LinkRecommendationConfiguration} object for the project.
	 */
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
