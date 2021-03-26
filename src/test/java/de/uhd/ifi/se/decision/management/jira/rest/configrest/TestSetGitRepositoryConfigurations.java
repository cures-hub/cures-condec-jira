package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettings;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetGitRepositoryConfigurations extends TestSetUp {

	protected HttpServletRequest request;
	protected ConfigRest configRest;
	private List<GitRepositoryConfiguration> gitRepositoryConfigurations;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
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
				configRest.setGitRepositoryConfigurations(null, null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullGitRepositoryConfigurationsProvided() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setGitRepositoryConfigurations(null, null, gitRepositoryConfigurations).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidGitRepositoryConfigurationsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setGitRepositoryConfigurations(request, "TEST", null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidGitRepositoryConfigurationsInvalid() {
		GitRepositoryConfiguration gitRepositoryConfiguration = new GitRepositoryConfiguration(null, null, "", "", "");
		List<GitRepositoryConfiguration> gitRepositoryConfigurations = new ArrayList<>();
		gitRepositoryConfigurations.add(gitRepositoryConfiguration);
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setGitRepositoryConfigurations(request, "TEST", gitRepositoryConfigurations).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsGitUriProvided() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.setGitRepositoryConfigurations(request, "TEST", gitRepositoryConfigurations).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsGitUriProvidedButBad() {
		GitRepositoryConfiguration badGitRepositoryConfiguration = new GitRepositoryConfiguration("/this/path/does/not/exist",
				"master", "", "", "");
		List<GitRepositoryConfiguration> badGitRepositoryConfigurations = new ArrayList<>();
		badGitRepositoryConfigurations.add(badGitRepositoryConfiguration);
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				configRest.setGitRepositoryConfigurations(request, "TEST", gitRepositoryConfigurations).getStatus());
	}

	@AfterClass
	public static void tearDown() {
		// reset plugin settings to default settings
		MockPluginSettingsFactory.pluginSettings = new MockPluginSettings();
	}

}
