package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNote;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteConfiguration;

/**
 * REST resource for release notes
 */
public interface ReleaseNotesRest {

	Response getProposedIssues(HttpServletRequest request, String projectKey,
			ReleaseNoteConfiguration releaseNoteConfiguration);

	Response postProposedKeys(HttpServletRequest request, String projectKey,
			HashMap<String, HashMap<String, List<String>>> postObject);

	Response createReleaseNote(HttpServletRequest request, String projectKey, HashMap<String, String> postObject);

	Response updateReleaseNote(HttpServletRequest request, String projectKey, ReleaseNote releaseNote);

	Response getReleaseNote(HttpServletRequest request, String projectKey, long id);

	Response getAllReleaseNotes(HttpServletRequest request, String projectKey, String query);

	Response deleteReleaseNote(HttpServletRequest request, String projectKey, long id);
}