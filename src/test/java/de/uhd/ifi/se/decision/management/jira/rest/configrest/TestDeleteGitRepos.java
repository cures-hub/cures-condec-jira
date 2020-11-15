package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestDeleteGitRepos extends TestSetUpGit {
	protected HttpServletRequest request;
	protected ConfigRest configRest;

	@Override
	@Before
	public void setUp() {
		configRest = new ConfigRest();
		init();
		super.setUp();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestNullProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.deleteGitRepos(null, null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValid() {
		assertEquals(Status.OK.getStatusCode(), configRest.deleteGitRepos(request, "TEST").getStatus());
	}

}
