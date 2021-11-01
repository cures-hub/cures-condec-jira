package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryHandler;
import de.uhd.ifi.se.decision.management.jira.persistence.ReleaseNotesPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.MarkdownCreator;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotes;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesConfiguration;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCreator;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesIssueProposal;

/**
 * REST resource for release notes
 */
@Path("/release-note")
public class ReleaseNotesRest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseNotesRest.class);

	@Path("/getProposedIssues")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getProposedIssues(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			ReleaseNotesConfiguration releaseNoteConfiguration) {

		ApplicationUser user = AuthenticationManager.getUser(request);
		String query = "?jql=project=" + projectKey + " && resolved >= " + releaseNoteConfiguration.getStartDate()
				+ " && resolved <= " + releaseNoteConfiguration.getEndDate();
		JiraQueryHandler queryHandler = new JiraQueryHandler(user, projectKey, query);
		List<Issue> jiraIssuesMatchingQuery = queryHandler.getJiraIssuesFromQuery();
		if (jiraIssuesMatchingQuery.size() == 0) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "No resolved issues were found in this date range!")).build();
		}
		ReleaseNotesCreator releaseNotesCreator = new ReleaseNotesCreator(jiraIssuesMatchingQuery,
				releaseNoteConfiguration, user);
		Map<String, ArrayList<ReleaseNotesIssueProposal>> mappedProposals = releaseNotesCreator.getMappedProposals();

		if (mappedProposals == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(
					ImmutableMap.of("error", "No issues with the mapped types are resolved in this date range!"))
					.build();
		}
		Map<String, Object> result = new HashMap<>();
		result.put("proposals", mappedProposals);
		result.put("additionalConfiguration", releaseNoteConfiguration.getAdditionalConfiguration());
		result.put("title", releaseNoteConfiguration.getTitle());
		result.put("startDate", releaseNoteConfiguration.getStartDate());
		result.put("endDate", releaseNoteConfiguration.getEndDate());
		return Response.ok(result).build();
	}

	@Path("/postProposedKeys")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response postProposedKeys(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			Map<String, Map<String, List<String>>> postObject) {
		ApplicationUser user = AuthenticationManager.getUser(request);
		Map<String, List<String>> keysForContent = postObject.get("selectedKeys");
		String title = postObject.get("title").get("id").get(0);
		List<String> additionalConfiguration = postObject.get("additionalConfiguration").get("id");
		MarkdownCreator markdownCreator = new MarkdownCreator(user, projectKey, keysForContent, title,
				additionalConfiguration);

		// generate text string
		String markDownString = markdownCreator.getMarkdownString();
		// return text string
		return Response.ok(Map.of("markdown", markDownString)).build();
	}

	@Path("/createReleaseNote")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createReleaseNote(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			Map<String, String> postObject) {
		ApplicationUser user = AuthenticationManager.getUser(request);
		String title = postObject.get("title");
		String startDate = postObject.get("startDate");
		String endDate = postObject.get("endDate");
		String releaseNoteContent = postObject.get("content");

		ReleaseNotes releaseNote = new ReleaseNotes(title, releaseNoteContent, projectKey, startDate, endDate);
		long id = ReleaseNotesPersistenceManager.createReleaseNotes(releaseNote, user);

		LOGGER.info("Release notes were created for project: " + projectKey);
		return Response.ok(id).build();
	}

	@Path("/updateReleaseNote")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateReleaseNote(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			ReleaseNotes releaseNote) {
		ApplicationUser user = AuthenticationManager.getUser(request);

		boolean updated = ReleaseNotesPersistenceManager.updateReleaseNotes(releaseNote, user);

		return Response.ok(updated).build();
	}

	@Path("/getReleaseNote")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getReleaseNote(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("id") long id) {
		ReleaseNotes releaseNote = ReleaseNotesPersistenceManager.getReleaseNotes(id);
		return Response.ok(releaseNote).build();
	}

	@Path("/getAllReleaseNotes")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getAllReleaseNotes(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("query") String query) {
		List<ReleaseNotes> releaseNotes = ReleaseNotesPersistenceManager.getAllReleaseNotes(projectKey, query);

		LOGGER.info("Release notes were viewed for project: " + projectKey);
		return Response.ok(releaseNotes).build();
	}

	@Path("/deleteReleaseNote")
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteReleaseNote(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("id") long id) {
		ApplicationUser user = AuthenticationManager.getUser(request);
		boolean deleted = ReleaseNotesPersistenceManager.deleteReleaseNotes(id, user);
		return Response.ok(deleted).build();
	}

}
