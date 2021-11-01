package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
 * REST resource for the management of release notes with explicit decision
 * knowledge.
 */
@Path("/releasenotes")
public class ReleaseNotesRest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseNotesRest.class);

	@Path("/createReleaseNotes")
	@POST
	public Response createReleaseNotes(@Context HttpServletRequest request, ReleaseNotes releaseNotes) {
		ApplicationUser user = AuthenticationManager.getUser(request);
		long id = ReleaseNotesPersistenceManager.insertReleaseNotes(releaseNotes, user);
		if (id < 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Release notes could not be created.")).build();
		}
		LOGGER.info("Release notes were created for project: " + releaseNotes.getProjectKey());
		return Response.ok(id).build();
	}

	@Path("/updateReleaseNotes")
	@POST
	public Response updateReleaseNotes(@Context HttpServletRequest request, ReleaseNotes releaseNotes) {
		ApplicationUser user = AuthenticationManager.getUser(request);
		boolean isUpdated = ReleaseNotesPersistenceManager.updateReleaseNotes(releaseNotes, user);
		if (!isUpdated) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Release notes could not be updated.")).build();
		}
		return Response.ok().build();
	}

	@Path("/getReleaseNotesById")
	@GET
	public Response getReleaseNotesById(@Context HttpServletRequest request, @QueryParam("id") long id) {
		ReleaseNotes releaseNotes = ReleaseNotesPersistenceManager.getReleaseNotesById(id);
		if (releaseNotes == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Release notes for given id could not be found.")).build();
		}
		return Response.ok(releaseNotes).build();
	}

	@Path("/getReleaseNotes")
	@GET
	public Response getReleaseNotes(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("searchTerm") String searchTerm) {
		List<ReleaseNotes> releaseNotes = ReleaseNotesPersistenceManager.getReleaseNotesMatchingFilter(projectKey,
				searchTerm);
		LOGGER.info("Release notes were viewed for project: " + projectKey);
		return Response.ok(releaseNotes).build();
	}

	@Path("/deleteReleaseNotes")
	@DELETE
	public Response deleteReleaseNotes(@Context HttpServletRequest request, @QueryParam("id") long id) {
		ApplicationUser user = AuthenticationManager.getUser(request);
		boolean isDeleted = ReleaseNotesPersistenceManager.deleteReleaseNotes(id, user);
		if (!isDeleted) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Release notes could not be deleted.")).build();
		}
		return Response.ok().build();
	}

	@Path("/getProposedIssues")
	@POST
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
		Map<String, List<ReleaseNotesIssueProposal>> mappedProposals = releaseNotesCreator.getMappedProposals();

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
}