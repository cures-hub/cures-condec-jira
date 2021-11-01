package de.uhd.ifi.se.decision.management.jira.rest.releasenotes;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotes;
import de.uhd.ifi.se.decision.management.jira.rest.ReleaseNotesRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetReleaseNotes extends TestSetUp {
	protected HttpServletRequest request;
	private ReleaseNotesRest releaseNotesRest;
	private String projectKey;
	private ReleaseNotes releaseNotes;

	@Before
	public void setUp() {
		releaseNotesRest = new ReleaseNotesRest();
		init();
		request = new MockHttpServletRequest();
		projectKey = "TEST";
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
		releaseNotes = new ReleaseNotes();
		releaseNotes.setTitle("some title");
		releaseNotes.setContent("some short content");
	}

	@Test
	@NonTransactional
	public void testGetAllReleaseNotes() {
		assertEquals(Response.Status.OK.getStatusCode(),
				releaseNotesRest.getAllReleaseNotes(request, projectKey, "").getStatus());
	}
}
