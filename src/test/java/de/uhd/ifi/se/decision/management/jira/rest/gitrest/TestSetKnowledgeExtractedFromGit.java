package de.uhd.ifi.se.decision.management.jira.rest.gitrest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.extraction.config.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.rest.GitRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetKnowledgeExtractedFromGit extends TestSetUpGit {

	protected HttpServletRequest request;
	protected GitRest gitRest;

	@Before
	public void setUp() {
		init();
		gitRest = new GitRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestNullProjectKeyNullIsExtractedFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				gitRest.setKnowledgeExtractedFromGit(null, null, false).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyValidIsExtractedFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				gitRest.setKnowledgeExtractedFromGit(null, "TEST", false).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyNullIsExtractedFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				gitRest.setKnowledgeExtractedFromGit(request, null, false).getStatus());
	}

	@Test
	public void testRequestUserNullProjectKeyValidIsExtractedFalse() {
		request.setAttribute("user", null);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				gitRest.setKnowledgeExtractedFromGit(request, "TEST", false).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidIsExtractedFalse() {
		assertEquals(Response.Status.OK.getStatusCode(),
				gitRest.setKnowledgeExtractedFromGit(request, "TEST", false).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidIsExtractedTrue() {
		List<GitRepositoryConfiguration> gitRepositoryConfigurations = new ArrayList<>();
		GitRepositoryConfiguration gitRepositoryConfiguration = new GitRepositoryConfiguration(TestSetUpGit.GIT_URI,
				"master", "", "", "");
		gitRepositoryConfigurations.add(gitRepositoryConfiguration);
		assertEquals(Status.OK.getStatusCode(),
				gitRest.setGitRepositoryConfigurations(request, "TEST", gitRepositoryConfigurations).getStatus());
		assertEquals(Response.Status.OK.getStatusCode(),
				gitRest.setKnowledgeExtractedFromGit(request, "TEST", true).getStatus());
	}
}
