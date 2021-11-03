package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesEntry;

/**
 * REST resource for the management of {@link ReleaseNotes} with explicit
 * decision knowledge.
 */
@Path("/releasenotes")
public class ReleaseNotesRest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseNotesRest.class);

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param searchTerm
	 *            for substring filtering so that only the release notes that match
	 *            the term are returned.
	 * @return all release notes with explicit decision knowledge for the Jira
	 *         project.
	 */
	@GET
	public Response getReleaseNotes(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("searchTerm") String searchTerm) {
		List<ReleaseNotes> releaseNotes = ReleaseNotesPersistenceManager.getReleaseNotesMatchingFilter(projectKey,
				searchTerm);
		LOGGER.info("Release notes were viewed for project: " + projectKey);
		return Response.ok(releaseNotes).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param id
	 *            internal database id of the release notes.
	 * @return release notes for the given id or bad request if non existing.
	 */
	@Path("/{id}")
	@GET
	public Response getReleaseNotesById(@Context HttpServletRequest request, @PathParam("id") long id) {
		ReleaseNotes releaseNotes = ReleaseNotesPersistenceManager.getReleaseNotesById(id);
		if (releaseNotes == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Release notes for given id could not be found.")).build();
		}
		return Response.ok(releaseNotes).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param releaseNotes
	 *            {@link ReleaseNotes} to be created without database id set.
	 * @return internal database id of inserted release notes, -1 if insertion
	 *         failed.
	 */
	@Path("/create")
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

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param releaseNotes
	 *            {@link ReleaseNotes} to be updated by title and/or content. The
	 *            database id must be set, i.e. {@link ReleaseNotes#getId()} must be
	 *            > 0. Only the title and/or textual content of the
	 *            {@link ReleaseNotes} can be updated.
	 * @return ok if the release notes were successfully updated.
	 */
	@Path("/update")
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

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param id
	 *            internal database id of the {@link ReleaseNotes}, i.e. the value
	 *            of {@link ReleaseNotes#getId()}.
	 * @return ok if the release notes were successfully deleted.
	 */
	@Path("/delete")
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

	@Path("/propose-elements")
	@POST
	public Response proposeElements(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			ReleaseNotesConfiguration releaseNoteConfiguration) {
		ApplicationUser user = AuthenticationManager.getUser(request);
		List<Issue> jiraIssuesMatchingQuery = JiraQueryHandler.getJiraIssuesResolvedDuringTimeRange(user, projectKey,
				releaseNoteConfiguration.getStartDate(), releaseNoteConfiguration.getEndDate());
		if (jiraIssuesMatchingQuery.isEmpty()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "No resolved Jira issues were found in this date range!")).build();
		}
		ReleaseNotesCreator releaseNotesCreator = new ReleaseNotesCreator(jiraIssuesMatchingQuery,
				releaseNoteConfiguration, user);
		Map<String, List<ReleaseNotesEntry>> mappedProposals = releaseNotesCreator.getMappedProposals();

		if (mappedProposals == null) {
			return Response.status(Status.BAD_REQUEST).entity(
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