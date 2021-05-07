package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyCheckLogHelper;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.CompletenessHandler;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.LinkSuggestionConfiguration;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.contextinformation.ContextInformation;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.duplicatedetection.BasicDuplicateTextDetector;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.duplicatedetection.DuplicateDetectionManager;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.DuplicateSuggestion;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.LinkSuggestion;
import de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions.SuggestionType;

/**
 * REST resource for consistency functionality.
 */

@Path("/consistency")
public class ConsistencyRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsistencyRest.class);

	// --------------------
	// Related issue detection
	// --------------------

	// TODO Change to POST method and post FilterSettings object
	@Path("/getRelatedKnowledgeElements")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getRelatedKnowledgeElements(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("elementId") Long elementId,
			@QueryParam("elementLocation") String elementLocation) {
		Optional<KnowledgeElement> knowledgeElement = isKnowledgeElementValid(projectKey, elementId, elementLocation);
		if (knowledgeElement.isPresent()) {
			ContextInformation ci = new ContextInformation(knowledgeElement.get());
			Collection<LinkSuggestion> linkSuggestions = ci.getLinkSuggestions();
			Map<String, Object> result = new HashMap<>();

			result.put("relatedIssues", linkSuggestions);

			return Response.ok(result).build();
		}
		return Response.status(400).entity(ImmutableMap.of("error", "No such element exists!")).build();
	}

	@Path("/discardLinkSuggestion")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response discardLinkSuggestion(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("originElementId") Long originId,
			@QueryParam("originElementLocation") String originLocation, @QueryParam("targetElementId") Long targetId,
			@QueryParam("targetElementLocation") String targetLocation) {
		return this.discardSuggestion(projectKey, originId, originLocation, targetId, targetLocation,
				SuggestionType.LINK);

	}

	// --------------------
	// Duplicate issue detection
	// --------------------

	@Path("/getDuplicateKnowledgeElement")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDuplicateKnowledgeElements(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("elementId") Long elementId,
			@QueryParam("location") String elementLocation) {
		Optional<KnowledgeElement> knowledgeElement;
		Response response;
		try {
			knowledgeElement = isKnowledgeElementValid(projectKey, elementId, elementLocation);

			if (knowledgeElement.isPresent()) {
				HashMap<String, Object> result = new HashMap<>();
				LinkSuggestionConfiguration linkSuggestionConfiguration = ConfigPersistenceManager
						.getLinkSuggestionConfiguration(projectKey);
				DuplicateDetectionManager manager = new DuplicateDetectionManager(knowledgeElement.get(),
						new BasicDuplicateTextDetector(linkSuggestionConfiguration.getMinTextLength()),
						linkSuggestionConfiguration.getMinTextLength());

				// get KnowledgeElements of project
				KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);

				// detect duplicates
				List<DuplicateSuggestion> foundDuplicateSuggestions = manager
						.findAllDuplicates(persistenceManager.getKnowledgeElements());

				result.put("duplicates", foundDuplicateSuggestions);
				response = Response.ok(result).build();
			} else {
				response = Response.status(400).entity(ImmutableMap.of("error", "No such element exists!")).build();
			}
		} catch (Exception e) {
			// LOGGER.error(e.getMessage());
			response = Response.status(500).entity(e).build();
		}
		return response;
	}

	@Path("/discardDuplicate")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response discardDetectedDuplicate(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("originElementId") Long originIssueId,
			@QueryParam("originElementLocation") String originLocation,
			@QueryParam("targetElementId") Long targetIssueId,
			@QueryParam("targetElementLocation") String targetLocation) {
		return this.discardSuggestion(projectKey, originIssueId, originLocation, targetIssueId, targetLocation,
				SuggestionType.DUPLICATE);

	}

	// --------------------
	// Consistency checks
	// --------------------

	@Path("/doesElementNeedApproval")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response doesElementNeedApproval(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("elementId") Long elementId,
			@QueryParam("elementLocation") String documentationLocation) {
		Optional<KnowledgeElement> knowledgeElement;
		Response response;
		try {
			knowledgeElement = isKnowledgeElementValid(projectKey, elementId, documentationLocation);

			if (knowledgeElement.isPresent()) {
				boolean doesIssueNeedApproval = ConsistencyCheckLogHelper
						.doesKnowledgeElementNeedApproval(knowledgeElement.get());
				response = Response.ok().entity(ImmutableMap.of("needsApproval", doesIssueNeedApproval)).build();
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

	@Path("/approveCheck")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
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

	private Response discardSuggestion(String projectKey, Long originIssueId, String originLocation, Long targetIssueId,
			String targetLocation, SuggestionType type) {
		Response response;
		// check if issue keys exist
		try {

			KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);
			KnowledgeElement origin = persistenceManager.getKnowledgeElement(originIssueId, originLocation);
			KnowledgeElement target = persistenceManager.getKnowledgeElement(targetIssueId, targetLocation);
			if (origin == null || target == null) {
				response = Response.status(400).entity(ImmutableMap.of("error", "No such element exists!")).build();
			} else {
				try {
					long databaseId;

					databaseId = ConsistencyPersistenceHelper.addDiscardedSuggestions(origin, target, type);

					response = Response.status(200).build();
					if (databaseId == -1) {
						response = Response.status(500).build();
					}

				} catch (Exception e) {
					// LOGGER.error(e.getMessage());
					response = Response.status(500).entity(ImmutableMap.of("error", e.toString())).build();
				}
			}

		} catch (Exception e) {
			// LOGGER.error(e.getMessage());
			response = Response.status(400).entity(ImmutableMap.of("error", "No such element exists!")).build();
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
	// Completeness checks
	// --------------------
	@Path("/doesElementNeedCompletenessApproval")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response doesElementNeedCompletenessApproval(@Context HttpServletRequest request,
			FilterSettings filterSettings) {
		if (filterSettings == null || filterSettings.getProjectKey() == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Completeness check could not be performed due to a bad request."))
					.build();
		}
		KnowledgeElement knowledgeElement = filterSettings.getSelectedElement();
		if (knowledgeElement == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error",
							"Completeness check could not be performed because the element could not be found."))
					.build();
		}
		boolean doesIssueNeedApproval = CompletenessHandler.hasIncompleteKnowledgeLinked(knowledgeElement);
		return Response.ok().entity(ImmutableMap.of("needsCompletenessApproval", doesIssueNeedApproval)).build();
	}
}
