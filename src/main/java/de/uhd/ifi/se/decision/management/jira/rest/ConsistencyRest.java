package de.uhd.ifi.se.decision.management.jira.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.management.jira.consistency.ContextInformation;
import de.uhd.ifi.se.decision.management.jira.consistency.DuplicateDetectionManager;
import de.uhd.ifi.se.decision.management.jira.consistency.LinkSuggestion;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.BasicDuplicateTextDetector;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.DuplicateFragment;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyCheckLogHelper;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;

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
	public Response getDuplicatesForIssue(@Context HttpServletRequest request, @QueryParam("issueKey") String issueKey) {
		boolean areIssueKeysValid;
		Response response;
		try {
			areIssueKeysValid = ComponentAccessor.getIssueManager().isExistingIssueKey(issueKey);

			if (areIssueKeysValid) {
				Issue baseIssue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(issueKey);
				HashMap<String, Object> result = new HashMap<>();
				DuplicateDetectionManager manager = new DuplicateDetectionManager(baseIssue, new BasicDuplicateTextDetector(ConfigPersistenceManager.getMinDuplicateLength(baseIssue.getProjectObject().getKey())));

				// get Issues of project
				Collection<Long> issueKeysToCheck = ComponentAccessor.getIssueManager().getIssueIdsForProject(baseIssue.getProjectId());
				Collection<Issue> issuesToCheck = new ArrayList<>();
				for (Long issueId : issueKeysToCheck) {
					issuesToCheck.add(ComponentAccessor.getIssueManager().getIssueObject(issueId));
				}
				// detect duplicates
				List<DuplicateFragment> foundDuplicateFragments = manager.findAllDuplicates(issuesToCheck);

				// convert to Json
				List<Map<String, Object>> jsonifiedIssues = new ArrayList<>();
				for (DuplicateFragment duplicateFragment : foundDuplicateFragments) {
					jsonifiedIssues.add(this.duplicateToJsonMap(duplicateFragment));
				}
				result.put("duplicates", jsonifiedIssues);
				response = Response.ok(result).build();
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


	@Path("/discardDuplicate")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response discardDetectedDuplicate(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey, @QueryParam("originIssueKey") String originIssueKey, @QueryParam("targetIssueKey") String targetIssueKey) {
		Response response;
		//check if issue keys exist
		boolean areIssueKeysValid;
		try {
			areIssueKeysValid = ComponentAccessor.getIssueManager().isExistingIssueKey(originIssueKey) && ComponentAccessor.getIssueManager().isExistingIssueKey(originIssueKey);
			if (areIssueKeysValid) {
				long databaseId = ConsistencyPersistenceHelper.addDiscardedDuplicate(originIssueKey, targetIssueKey, projectKey);
				response = Response.status(200).build();
				if (databaseId == -1) {
					response = Response.status(500).build();
				}
			} else {
				response = Response.status(400).entity(
					ImmutableMap.of("error", "No issues with the given key exists!")).build();
			}
		} catch (Exception e) {
			//e.printStackTrace();
			response = Response.status(500).entity(
				ImmutableMap.of("error", e.toString())).build();
		}

		return response;
	}

	public Map<String, Object> duplicateToJsonMap(DuplicateFragment duplicateFragment) {
		Map<String, Object> jsonMap = new HashMap<>();
		if(duplicateFragment != null){
			jsonMap.put("id", duplicateFragment.getI2().getId());
			jsonMap.put("key", duplicateFragment.getI2().getKey());
			jsonMap.put("summary", duplicateFragment.getI2().getSummary());
			jsonMap.put("preprocessedSummary", duplicateFragment.getPreprocessedSummary());

			jsonMap.put("description", duplicateFragment.getI2().getDescription());
			jsonMap.put("startDuplicate", duplicateFragment.getStartDuplicate());
			jsonMap.put("length", duplicateFragment.getLength());

		}


		return jsonMap;
	}


	@Path("/doesIssueNeedApproval")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response doesIssueNeedApproval(@Context HttpServletRequest request, @QueryParam("issueKey") String issueKey) {
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
	public Response approveCheck(@Context HttpServletRequest request, @QueryParam("issueKey") String issueKey, @QueryParam("user") String user) {
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


}
