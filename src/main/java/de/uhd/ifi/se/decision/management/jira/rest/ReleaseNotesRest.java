package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.List;

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
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ReleaseNotesPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.MarkdownCreator;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotes;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCategory;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesConfiguration;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCreator;

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
	@Path("/delete/{id}")
	@DELETE
	public Response deleteReleaseNotes(@Context HttpServletRequest request, @PathParam("id") long id) {
		ApplicationUser user = AuthenticationManager.getUser(request);
		boolean isDeleted = ReleaseNotesPersistenceManager.deleteReleaseNotes(id, user);
		if (!isDeleted) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Release notes could not be deleted.")).build();
		}
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param releaseNoteConfiguration
	 *            {@link ReleaseNotesConfiguration} that is used to propose Jira
	 *            issues to be included in the release notes.
	 * @return {@link ReleaseNotes} with suggested Jira issues for each
	 *         {@link ReleaseNotesCategory}. The content field is not filled yet.
	 */
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
		ReleaseNotes proposedReleaseNotes = releaseNotesCreator.proposeReleaseNotes();

		if (proposedReleaseNotes == null) {
			return Response.status(Status.BAD_REQUEST).entity(
					ImmutableMap.of("error", "No Jira issues with the mapped types are resolved in this date range!"))
					.build();
		}
		return Response.ok(proposedReleaseNotes).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param releaseNotes
	 *            {@link ReleaseNotes} with selected Jira issues for each
	 *            {@link ReleaseNotesCategory}.
	 * @return markdown string for the content field of the {@link ReleaseNotes}.
	 */
	@Path("/create-content")
	@POST
	public Response createReleaseNotesContent(@Context HttpServletRequest request, ReleaseNotes releaseNotes) {
		MarkdownCreator markdownCreator = new MarkdownCreator(releaseNotes);
		String markDownString = markdownCreator.getMarkdownString();
		return Response.ok(ImmutableMap.of("markdown", markDownString)).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param releaseNotesConfiguration
	 *            {@link ReleaseNotesConfiguration} to be saved as the default when
	 *            creating new {@link ReleaseNotes}.
	 * @return ok if saving was successful.
	 */
	@Path("/save-configuration")
	@POST
	public Response saveReleaseNotesConfiguration(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, ReleaseNotesConfiguration releaseNotesConfiguration) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		ConfigPersistenceManager.saveReleaseNotesConfiguration(projectKey, releaseNotesConfiguration);
		return Response.ok().build();
	}

	/**
	 * @param projectKey
	 *            of a Jira project.
	 * @return {@link ReleaseNotesConfiguration} to be used as the default when
	 *         creating new {@link ReleaseNotes}.
	 */
	@Path("/configuration")
	@GET
	public Response getReleaseNotesConfiguration(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		ReleaseNotesConfiguration releaseNotesConfiguration = ConfigPersistenceManager
				.getReleaseNotesConfiguration(projectKey);
		return Response.ok(releaseNotesConfiguration).build();
	}
}