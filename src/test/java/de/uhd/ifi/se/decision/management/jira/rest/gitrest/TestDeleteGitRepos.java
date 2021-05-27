package de.uhd.ifi.se.decision.management.jira.rest.gitrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.rest.GitRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

@Ignore
public class TestDeleteGitRepos extends TestSetUpGit {
	protected HttpServletRequest request;
	protected GitRest gitRest;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		gitRest = new GitRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testRequestNullProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), gitRest.deleteGitRepos(null, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestValidProjectKeyValid() {
		assertEquals(Status.OK.getStatusCode(), gitRest.deleteGitRepos(request, "TEST").getStatus());
	}

}
