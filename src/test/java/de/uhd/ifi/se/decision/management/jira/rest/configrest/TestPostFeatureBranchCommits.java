package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestPostFeatureBranchCommits extends TestSetUp {

	private HttpServletRequest request;
	private ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testOk() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.setPostFeatureBranchCommits(request, "TEST", true).getStatus());
	}

	@Test
	public void testInvalidKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setPostFeatureBranchCommits(request, null, true).getStatus());
	}

	@Test
	public void testEmptyKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setPostFeatureBranchCommits(request, "", true).getStatus());
	}
}
