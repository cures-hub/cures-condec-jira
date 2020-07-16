package de.uhd.ifi.se.decision.management.jira.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.management.jira.consistency.contextinformation.ContextInformation;
import de.uhd.ifi.se.decision.management.jira.consistency.duplicatedetection.BasicDuplicateTextDetector;
import de.uhd.ifi.se.decision.management.jira.consistency.duplicatedetection.DuplicateDetectionManager;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.DuplicateSuggestion;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.LinkSuggestion;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.SuggestionType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyCheckLogHelper;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST resource for consistency functionality.
 */

@Path("/consistency")
public class ConsistencyRest {

	//--------------------
	// Related issue detection
	//--------------------

	@Path("/getRelatedIssues")
	@GET
	public Response getRelatedIssues(@Context HttpServletRequest request, @QueryParam("issueKey") String issueKey) {
		try {
			ContextInformation ci = new ContextInformation(issueKey);
			Collection<LinkSuggestion> linkSuggestions = ci.getLinkSuggestions();
			HashMap<String, Object> result = new HashMap<>();

			List<Map<String, Object>> jsonifiedIssues = new ArrayList<>();
			for (LinkSuggestion linkSuggestion : linkSuggestions) {
				jsonifiedIssues.add(this.suggestionToJsonMap(linkSuggestion));
			}
			result.put("relatedIssues", jsonifiedIssues);

			return Response.ok(result).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
	}

	@Path("/discardLinkSuggestion")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response discardLinkSuggestion(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, @QueryParam("originElementId") Long originIssueId, @QueryParam("originLocation") String originLocation,
										  @QueryParam("targetElementId") Long targetIssueId, @QueryParam("targetLocation") String targetLocation) {
		return this.discardSuggestion(projectKey, originIssueId, originLocation, targetIssueId, targetLocation, SuggestionType.LINK);

	}


	private Map<String, Object> suggestionToJsonMap(LinkSuggestion linkSuggestion) {
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("key", linkSuggestion.getTargetElement().getKey());
		jsonMap.put("summary", linkSuggestion.getTargetElement().getSummary());
		jsonMap.put("id", linkSuggestion.getTargetElement().getId());
		jsonMap.put("score", linkSuggestion.getTotalScore());
		jsonMap.put("results", linkSuggestion.getScore());

		return jsonMap;
	}


	//--------------------
	// Duplicate issue detection
	//--------------------

	@Path("/getDuplicatesForIssue")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getDuplicatesForIssue(@Context HttpServletRequest request, @QueryParam("issueKey") String
		issueKey) {
		boolean areIssueKeysValid;
		Response response;
		try {
			areIssueKeysValid = ComponentAccessor.getIssueManager().isExistingIssueKey(issueKey);

			if (areIssueKeysValid) {
				Issue baseIssue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(issueKey);
				HashMap<String, Object> result = new HashMap<>();
				DuplicateDetectionManager manager = new DuplicateDetectionManager(baseIssue, new BasicDuplicateTextDetector(ConfigPersistenceManager.getMinDuplicateLength(baseIssue.getProjectObject().getKey())));

				// get KnowledgeElements of project
				KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(baseIssue.getProjectObject().getKey());

				// detect duplicates
				List<DuplicateSuggestion> foundDuplicateSuggestions = manager.findAllDuplicates(persistenceManager.getKnowledgeElements());

				// convert to Json
				List<Map<String, Object>> jsonifiedIssues = new ArrayList<>();
				for (DuplicateSuggestion duplicateSuggestion : foundDuplicateSuggestions) {
					jsonifiedIssues.add(this.duplicateToJsonMap(duplicateSuggestion));
				}
				result.put("duplicates", jsonifiedIssues);
				response = Response.ok(result).build();
			} else {
				response = Response.status(400).entity(
					ImmutableMap.of("error", "No such element exists!")).build();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			response = Response.status(500).entity(e).build();
		}
		return response;
	}


	@Path("/discardDuplicate")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response discardDetectedDuplicate(@Context HttpServletRequest
												 request, @QueryParam("projectKey") String projectKey, @QueryParam("originElementId") Long
												 originIssueId, @QueryParam("originLocation") String originLocation, @QueryParam("targetElementId") Long targetIssueId,
											 @QueryParam("targetLocation") String targetLocation) {
		return this.discardSuggestion(projectKey, originIssueId, originLocation, targetIssueId, targetLocation, SuggestionType.DUPLICATE);

	}

	public Map<String, Object> duplicateToJsonMap(DuplicateSuggestion duplicateSuggestion) {
		Map<String, Object> jsonMap = new HashMap<>();
		if (duplicateSuggestion != null) {
			jsonMap.put("baseElement", duplicateSuggestion.getBaseElement());
			jsonMap.put("suggestion", duplicateSuggestion.getSuggestion());
			jsonMap.put("preprocessedSummary", duplicateSuggestion.getPreprocessedSummary());
			jsonMap.put("startDuplicate", duplicateSuggestion.getStartDuplicate());
			jsonMap.put("length", duplicateSuggestion.getLength());

		}

		return jsonMap;
	}


	@Path("/doesIssueNeedApproval")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response doesIssueNeedApproval(@Context HttpServletRequest request, @QueryParam("issueKey") String
		issueKey) {
		boolean isIssueKeyValid;
		Response response;
		try {
			isIssueKeyValid = ComponentAccessor.getIssueManager().isExistingIssueKey(issueKey);

			if (isIssueKeyValid) {
				Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(issueKey);

				boolean doesIssueNeedApproval = ConsistencyCheckLogHelper.doesIssueNeedApproval(issue);
				response = Response.ok().entity(ImmutableMap.of("needsApproval", doesIssueNeedApproval)).build();
			} else {
				response = Response.status(400).entity(
					ImmutableMap.of("error", "No issue with the given key exists!")).build();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			response = Response.status(500).entity(e).build();
		}
		return response;
	}

	@Path("/approveCheck")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response approveCheck(@Context HttpServletRequest request, @QueryParam("issueKey") String
		issueKey, @QueryParam("user") String user) {
		boolean isIssueKeyValid;
		ApplicationUser doesUserExist;
		Response response;
		try {
			isIssueKeyValid = ComponentAccessor.getIssueManager().isExistingIssueKey(issueKey);
			doesUserExist = ComponentAccessor.getUserManager().getUserByName(user);
			if (isIssueKeyValid && doesUserExist != null) {
				Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(issueKey);

				ConsistencyCheckLogHelper.approveCheck(issue, user);
				response = Response.ok().build();
			} else {
				response = Response.status(400).entity(
					ImmutableMap.of("error", "No issue with the given key exists!")).build();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			response = Response.status(500).entity(e).build();
		}
		return response;
	}

	private Response discardSuggestion(String projectKey, Long originIssueId, String originLocation, Long targetIssueId, String targetLocation, SuggestionType type) {
		Response response;
		//check if issue keys exist
		try {

			KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);
			KnowledgeElement origin = persistenceManager.getKnowledgeElement(originIssueId, originLocation);
			KnowledgeElement target = persistenceManager.getKnowledgeElement(targetIssueId, targetLocation);
			if (origin == null || target == null) {
				response = Response.status(400).entity(
					ImmutableMap.of("error", "No such element exists!")).build();
			} else {
				try {
					long databaseId;

					databaseId = ConsistencyPersistenceHelper.addDiscardedSuggestions(origin, target, type);


					response = Response.status(200).build();
					if (databaseId == -1) {
						response = Response.status(500).build();
					}

				} catch (Exception e) {
					//e.printStackTrace();
					response = Response.status(500).entity(
						ImmutableMap.of("error", e.toString())).build();
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			response = Response.status(400).entity(
				ImmutableMap.of("error", "No such element exists!")).build();
		}


		return response;
	}


}
