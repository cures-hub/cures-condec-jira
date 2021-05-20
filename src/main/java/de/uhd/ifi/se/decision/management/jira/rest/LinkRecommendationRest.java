package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyCheckLogHelper;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.DuplicateRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendationConfiguration;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation.ContextInformation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.duplicatedetection.DuplicateDetectionManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.duplicatedetection.DuplicateTextDetector;

/**
 * REST resource for link recommendation and duplicate recognition (including
 * its configuration).
 */
@Path("/linkrecommendation")
public class LinkRecommendationRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(LinkRecommendationRest.class);

	// TODO Add duplicates here
	@Path("/getRelatedKnowledgeElements")
	@GET
	public Response getRelatedKnowledgeElements(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("elementId") Long elementId,
			@QueryParam("elementLocation") String elementLocation) {
		Optional<KnowledgeElement> knowledgeElement = isKnowledgeElementValid(projectKey, elementId, elementLocation);
		if (knowledgeElement.isPresent()) {
			ContextInformation ci = new ContextInformation(knowledgeElement.get());
			Collection<Recommendation> linkSuggestions = ci.getLinkRecommendations();
			return Response.ok(linkSuggestions).build();
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "No such element exists!")).build();
	}

	// --------------------
	// Duplicate issue detection
	// --------------------

	@Path("/getDuplicateKnowledgeElement")
	@GET
	public Response getDuplicateKnowledgeElements(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("elementId") Long elementId,
			@QueryParam("location") String elementLocation) {
		Optional<KnowledgeElement> knowledgeElement;

		knowledgeElement = isKnowledgeElementValid(projectKey, elementId, elementLocation);

		if (knowledgeElement.isPresent()) {
			LinkRecommendationConfiguration linkSuggestionConfiguration = ConfigPersistenceManager
					.getLinkRecommendationConfiguration(projectKey);
			DuplicateDetectionManager manager = new DuplicateDetectionManager(knowledgeElement.get(),
					new DuplicateTextDetector(linkSuggestionConfiguration.getMinTextLength()));

			KnowledgeGraph graph = KnowledgeGraph.getInstance(projectKey);
			List<KnowledgeElement> unlinkedElements = graph.getUnlinkedElements(knowledgeElement.get());

			// detect duplicates
			List<DuplicateRecommendation> foundDuplicateSuggestions = manager.findAllDuplicates(unlinkedElements);

			return Response.ok(foundDuplicateSuggestions).build();
		} else {
			return Response.status(400).entity(ImmutableMap.of("error", "No such element exists!")).build();
		}
	}

	@Path("/discardRecommendation")
	@POST
	public Response discardRecommendation(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, LinkRecommendation recommendation) {
		if (recommendation == null || recommendation.getBothElements().contains(null)) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The recommendation to discard is not valid.")).build();
		}

		long databaseId = ConsistencyPersistenceHelper.addDiscardedSuggestions(recommendation.getSource(),
				recommendation.getTarget(), recommendation.getRecommendationType());

		if (databaseId == -1) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The recommendation could not be discarded.")).build();
		}

		return Response.status(Status.OK).build();
	}

	// --------------------
	// Link suggestion prompts
	// --------------------

	@Path("/doesElementNeedApproval")
	@GET
	public Response doesElementNeedApproval(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("elementId") Long elementId,
			@QueryParam("elementLocation") String documentationLocation) {
		Optional<KnowledgeElement> knowledgeElement = isKnowledgeElementValid(projectKey, elementId,
				documentationLocation);

		if (knowledgeElement.isPresent()) {
			boolean doesIssueNeedApproval = ConsistencyCheckLogHelper
					.doesKnowledgeElementNeedApproval(knowledgeElement.get());
			return Response.ok().entity(doesIssueNeedApproval).build();
		}
		return Response.status(400).entity(ImmutableMap.of("error", "No issue with the given key exists!")).build();
	}

	@Path("/approveCheck")
	@POST
	public Response approveCheck(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("elementId") Long elementId, @QueryParam("elementLocation") String documentationLocation,
			@QueryParam("user") String user) {

		Optional<KnowledgeElement> knowledgeElement;
		ApplicationUser doesUserExist;
		Response response;
		try {
			knowledgeElement = isKnowledgeElementValid(projectKey, elementId, documentationLocation);
			doesUserExist = ComponentAccessor.getUserManager().getUserByName(user);
			if (knowledgeElement.isPresent() && doesUserExist != null) {

				ConsistencyCheckLogHelper.approveCheck(knowledgeElement.get(), user);
				response = Response.ok().build();
			} else {
				response = Response.status(400).entity(ImmutableMap.of("error", "No issue with the given key exists!"))
						.build();
			}
		} catch (Exception e) {
			// LOGGER.error(e.getMessage());
			response = Response.status(500).entity(e).build();
		}
		return response;
	}

	private Optional<KnowledgeElement> isKnowledgeElementValid(String projectKey, Long elementId,
			String elementLocation) {
		KnowledgeElement knowledgeElement = null;
		try {
			// we do not want to create a new project here!
			if (ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey) != null) {
				KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);
				knowledgeElement = persistenceManager.getKnowledgeElement(elementId, elementLocation);
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		return Optional.ofNullable(knowledgeElement);
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
		ConfigPersistenceManager.saveLinkSuggestionConfiguration(projectKey, linkSuggestionConfiguration);
		return Response.ok().build();
	}

	@Path("/setMinimumDuplicateLength")
	@POST
	public Response setMinimumDuplicateLength(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("fragmentLength") int fragmentLength) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		if (fragmentLength < 3) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The minimum length for the duplicates is invalid.")).build();
		}
		LinkRecommendationConfiguration linkSuggestionConfiguration = ConfigPersistenceManager
				.getLinkRecommendationConfiguration(projectKey);
		linkSuggestionConfiguration.setMinTextLength(fragmentLength);
		ConfigPersistenceManager.saveLinkSuggestionConfiguration(projectKey, linkSuggestionConfiguration);
		return Response.ok().build();
	}
}
