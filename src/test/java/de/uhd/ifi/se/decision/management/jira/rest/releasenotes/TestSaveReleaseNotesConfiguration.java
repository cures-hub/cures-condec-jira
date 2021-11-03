package de.uhd.ifi.se.decision.management.jira.rest.releasenotes;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesConfiguration;
import de.uhd.ifi.se.decision.management.jira.rest.ReleaseNotesRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSaveReleaseNotesConfiguration extends TestSetUp {

	private HttpServletRequest request;
	private ReleaseNotesRest releaseNotesRest;

	@Before
	public void setUp() {
		init();
		releaseNotesRest = new ReleaseNotesRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestValidProjectKeyNullReleaseNotesConfigNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				releaseNotesRest.saveReleaseNotesConfiguration(request, null, null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidReleaseNotesConfigValid() {
		assertEquals(Status.OK.getStatusCode(), releaseNotesRest
				.saveReleaseNotesConfiguration(request, "TEST", new ReleaseNotesConfiguration()).getStatus());
	}
}