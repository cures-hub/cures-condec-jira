package de.uhd.ifi.se.decision.management.jira.rest.releasenotes;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotes;
import de.uhd.ifi.se.decision.management.jira.rest.ReleaseNotesRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetReleaseNotesById extends TestSetUp {
	private HttpServletRequest request;
	private ReleaseNotesRest releaseNotesRest;
	private ReleaseNotes releaseNotes;

	@Before
	public void setUp() {
		releaseNotesRest = new ReleaseNotesRest();
		init();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
		releaseNotes = new ReleaseNotes();
		releaseNotes.setTitle("some title");
		releaseNotes.setContent("some short content");
		releaseNotes.setProjectKey("TEST");
	}

	@Test
	@NonTransactional
	public void testRequestValidReleaseNotesExisting() {
		long databaseId = Long
				.parseLong(releaseNotesRest.createReleaseNotes(request, releaseNotes).getEntity().toString());
		assertEquals(Status.OK.getStatusCode(), releaseNotesRest.getReleaseNotesById(request, databaseId).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestValidReleaseNotesNotExisting() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), releaseNotesRest.getReleaseNotesById(request, -1).getStatus());
	}

}
