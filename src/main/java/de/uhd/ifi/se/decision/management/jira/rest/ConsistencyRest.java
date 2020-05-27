package de.uhd.ifi.se.decision.management.jira.rest;

import de.uhd.ifi.se.decision.management.jira.consistency.ContextInformation;
import de.uhd.ifi.se.decision.management.jira.consistency.LinkSuggestion;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * REST resource for plug-in configuration
 */

@Path("/consistency")
public class ConsistencyRest {

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
	public Response discardLinkSuggestion(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
										  @QueryParam("originIssueKey") String originIssueKey, @QueryParam("targetIssueKey") String targetIssueKey) {
		long databaseId = ConsistencyPersistenceHelper.addDiscardedSuggestions(originIssueKey, targetIssueKey, projectKey);
		Response response = Response.status(200).build();
		if (databaseId == -1) {
			response = Response.status(500).build();
		}
		return response;
	}

	private Map<String, Object> suggestionToJsonMap(LinkSuggestion linkSuggestion) {
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("key", linkSuggestion.getTargetIssue().getKey());
		jsonMap.put("summary", linkSuggestion.getTargetIssue().getSummary());
		jsonMap.put("id", linkSuggestion.getTargetIssue().getId());
		jsonMap.put("score", linkSuggestion.getScore());

		return jsonMap;
	}

}
