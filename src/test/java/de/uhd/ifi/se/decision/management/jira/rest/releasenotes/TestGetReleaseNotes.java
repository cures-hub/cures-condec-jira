package de.uhd.ifi.se.decision.management.jira.rest.releasenotes;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ReleaseNotesRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetReleaseNotes extends TestSetUp {
	private HttpServletRequest request;
	private ReleaseNotesRest releaseNotesRest;

	@Before
	public void setUp() {
		releaseNotesRest = new ReleaseNotesRest();
		init();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testRequestValidProjectKeyValidSearchTermEmpty() {
		assertEquals(Response.Status.OK.getStatusCode(),
				releaseNotesRest.getReleaseNotes(request, "TEST", "").getStatus());
	}
}
