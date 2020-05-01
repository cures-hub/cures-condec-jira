package de.uhd.ifi.se.decision.management.jira.rest.impl;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import de.uhd.ifi.se.decision.management.jira.consistency.ContextInformation;
import de.uhd.ifi.se.decision.management.jira.consistency.LinkSuggestion;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceManager;
import org.ofbiz.core.entity.GenericEntityException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * REST resource for plug-in configuration
 */

@Path("/consistency")
public class ConsistencyRestImpl {
	private IssueManager issueManager = ComponentAccessor.getIssueManager();

	@Path("/getRelatedIssues")
	@GET
	public Response getRelatedIssues(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
									 @QueryParam("issueKey") String issueKey) {
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
		} catch (GenericEntityException e) {
			return Response.status(500).build();
		}
	}


	private Collection<Issue> getAllIssuesForProject(Long projectId) throws GenericEntityException {
		Collection<Issue> issuesOfProject = new ArrayList<>();
		Collection<Long> issueIds = issueManager.getIssueIdsForProject(projectId);

		for (Long issueId : issueIds) {
			issuesOfProject.add(issueManager.getIssueObject(issueId));
		}
		return issuesOfProject;
	}

	private Collection<Issue> getAllIssuesForProject(String projectKey) throws GenericEntityException {
		return getAllIssuesForProject(ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey).getId());
	}

	@Path("/discardLinkSuggestion")
	@GET
	public Response discardLinkSuggestion(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
										  @QueryParam("originIssueKey") String originIssueKey, @QueryParam("targetIssueKey") String targetIssueKey) {
		long databaseId = ConsistencyPersistenceManager.addDiscardedSuggestions(originIssueKey, targetIssueKey, projectKey);
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
