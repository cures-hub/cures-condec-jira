package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetKnowledgeExtractedFromGit extends TestSetUp {

	protected HttpServletRequest request;
	protected ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestNullProjectKeyNullIsExtractedFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(null, null, false).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyValidIsExtractedFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(null, "TEST", false).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyNullIsExtractedFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(request, null, false).getStatus());
	}

	@Test
	public void testRequestUserNullProjectKeyValidIsExtractedFalse() {
		request.setAttribute("user", null);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(request, "TEST", false).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidIsExtractedFalse() {
		assertEquals(Response.Status.OK.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(request, "TEST", false).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidIsExtractedTrue() {
		List<GitRepositoryConfiguration> gitRepositoryConfigurations = new ArrayList<>();
		GitRepositoryConfiguration gitRepositoryConfiguration = new GitRepositoryConfiguration(TestSetUpGit.GIT_URI,
		"master", "", "", "");
		gitRepositoryConfigurations.add(gitRepositoryConfiguration);
		assertEquals(Status.OK.getStatusCode(),
				configRest.setGitRepositoryConfigurations(request, "TEST", gitRepositoryConfigurations).getStatus());
		assertEquals(Response.Status.OK.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(request, "TEST", true).getStatus());
	}
	
	@Test
	public void testRequestValidProjectKeyExistsGitUriProvidedButBad() {
		GitRepositoryConfiguration badGitRepositoryConfiguration = new GitRepositoryConfiguration("/this/path/does/not/exist",
				"master", "", "", "");
		List<GitRepositoryConfiguration> badGitRepositoryConfigurations = new ArrayList<>();
		badGitRepositoryConfigurations.add(badGitRepositoryConfiguration);
		assertEquals(Status.OK.getStatusCode(),
				configRest.setGitRepositoryConfigurations(request, "TEST", badGitRepositoryConfigurations).getStatus());
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(request, "TEST", true).getStatus());
				List<GitRepositoryConfiguration> gitRepositoryConfigurations = new ArrayList<>();
				GitRepositoryConfiguration gitRepositoryConfiguration = new GitRepositoryConfiguration(TestSetUpGit.GIT_URI,
				"master", "", "", "");
				gitRepositoryConfigurations.add(gitRepositoryConfiguration);
				assertEquals(Status.OK.getStatusCode(),
						configRest.setGitRepositoryConfigurations(request, "TEST", gitRepositoryConfigurations).getStatus());
	}		
}
