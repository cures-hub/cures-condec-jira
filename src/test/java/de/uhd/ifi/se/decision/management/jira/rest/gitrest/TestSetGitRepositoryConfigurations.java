package de.uhd.ifi.se.decision.management.jira.rest.gitrest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.rest.GitRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetGitRepositoryConfigurations extends TestSetUpGit {

	protected HttpServletRequest request;
	protected GitRest gitRest;
	private List<GitRepositoryConfiguration> gitRepositoryConfigurations;

	@Before
	public void setUp() {
		init();
		gitRest = new GitRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
		GitRepositoryConfiguration gitRepositoryConfiguration = new GitRepositoryConfiguration(TestSetUpGit.GIT_URI,
				"master", "", "", "");
		gitRepositoryConfigurations = new ArrayList<>();
		gitRepositoryConfigurations.add(gitRepositoryConfiguration);
	}

	@Test
	public void testRequestNullProjectKeyNullGitRepositoryConfigurationsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.setGitRepositoryConfigurations(null, null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullGitRepositoryConfigurationsProvided() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.setGitRepositoryConfigurations(null, null, gitRepositoryConfigurations).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidGitRepositoryConfigurationsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.setGitRepositoryConfigurations(request, "TEST", null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidGitRepositoryConfigurationsInvalid() {
		GitRepositoryConfiguration gitRepositoryConfiguration = new GitRepositoryConfiguration(null, null, "", "", "");
		List<GitRepositoryConfiguration> gitRepositoryConfigurations = new ArrayList<>();
		gitRepositoryConfigurations.add(gitRepositoryConfiguration);
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.setGitRepositoryConfigurations(request, "TEST", gitRepositoryConfigurations).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsGitUriProvided() {
		assertEquals(Status.OK.getStatusCode(),
				gitRest.setGitRepositoryConfigurations(request, "TEST", gitRepositoryConfigurations).getStatus());
	}

}
