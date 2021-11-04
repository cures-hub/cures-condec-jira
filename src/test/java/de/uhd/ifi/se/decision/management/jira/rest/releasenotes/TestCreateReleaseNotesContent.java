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

public class TestCreateReleaseNotesContent extends TestSetUp {
	protected HttpServletRequest request;
	private ReleaseNotesRest releaseNotesRest;

	@Before
	public void setUp() {
		init();
		releaseNotesRest = new ReleaseNotesRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testPostProposedKeys() {
		assertEquals(Response.Status.OK.getStatusCode(),
				releaseNotesRest.createReleaseNotesContent(request, new ReleaseNotes()).getStatus());
	}

}
