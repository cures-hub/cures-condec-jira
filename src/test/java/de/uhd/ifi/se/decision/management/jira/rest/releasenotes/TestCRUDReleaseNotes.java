package de.uhd.ifi.se.decision.management.jira.rest.releasenotes;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotes;
import de.uhd.ifi.se.decision.management.jira.rest.ReleaseNotesRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestCRUDReleaseNotes extends TestSetUp {
	protected HttpServletRequest request;
	private ReleaseNotesRest releaseNoteRest;
	private String projectKey;
	private String releaseNoteContent;
	private ReleaseNotes releaseNote;
	private HashMap<String, String> postObject;

	@Before
	public void setUp() {
		releaseNoteRest = new ReleaseNotesRest();
		init();
		request = new MockHttpServletRequest();
		projectKey = "TEST";

		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		releaseNoteContent = "some short content";
		releaseNote = new ReleaseNotes();
		postObject = new HashMap<String, String>();
		postObject.put("title", "some title");
		postObject.put("content", releaseNoteContent);
	}

	@Test
	public void testCreateReleaseNotes() {
		assertEquals(Response.Status.OK.getStatusCode(),
				releaseNoteRest.createReleaseNote(request, projectKey, postObject).getStatus());
	}

	@Test
	public void testUpdateReleaseNotes() {
		assertEquals(Response.Status.OK.getStatusCode(),
				releaseNoteRest.updateReleaseNote(request, projectKey, releaseNote).getStatus());
	}

	@Test
	public void testGetAllReleaseNotes() {
		assertEquals(Response.Status.OK.getStatusCode(),
				releaseNoteRest.getAllReleaseNotes(request, projectKey, "").getStatus());
	}

	@Test
	public void testGetReleaseNote() {
		assertEquals(Response.Status.OK.getStatusCode(),
				releaseNoteRest.getReleaseNote(request, projectKey, releaseNote.getId()).getStatus());
	}

	@Test
	public void testDeleteReleaseNotes() {
		assertEquals(Response.Status.OK.getStatusCode(),
				releaseNoteRest.deleteReleaseNote(request, projectKey, releaseNote.getId()).getStatus());
	}
}
