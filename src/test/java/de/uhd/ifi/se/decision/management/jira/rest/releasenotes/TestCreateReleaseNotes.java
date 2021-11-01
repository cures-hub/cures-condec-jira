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

public class TestCreateReleaseNotes extends TestSetUp {
	protected HttpServletRequest request;
	private ReleaseNotesRest releaseNotesRest;
	private ReleaseNotes releaseNotes;

	@Before
	public void setUp() {
		init();
		releaseNotesRest = new ReleaseNotesRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
		releaseNotes = new ReleaseNotes();
		releaseNotes.setTitle("some title");
		releaseNotes.setContent("some short content");
		releaseNotes.setProjectKey("TEST");
	}

	@Test
	@NonTransactional
	public void testRequestValidReleaseNotesValid() {
		assertEquals(Status.OK.getStatusCode(), releaseNotesRest.createReleaseNotes(request, releaseNotes).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestUserNullReleaseNotesValid() {
		request.setAttribute("user", null);
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				releaseNotesRest.createReleaseNotes(request, releaseNotes).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestValidReleaseNotesNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				releaseNotesRest.createReleaseNotes(request, null).getStatus());
	}
}