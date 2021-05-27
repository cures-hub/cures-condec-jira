package de.uhd.ifi.se.decision.management.jira.rest.gitrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.GitRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestPostFeatureBranchCommits extends TestSetUp {

	private HttpServletRequest request;
	private GitRest gitRest;

	@Before
	public void setUp() {
		init();
		gitRest = new GitRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testOk() {
		assertEquals(Status.OK.getStatusCode(),
				gitRest.setPostFeatureBranchCommits(request, "TEST", true).getStatus());
	}

	@Test
	public void testInvalidKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.setPostFeatureBranchCommits(request, null, true).getStatus());
	}

	@Test
	public void testEmptyKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.setPostFeatureBranchCommits(request, "", true).getStatus());
	}
}
