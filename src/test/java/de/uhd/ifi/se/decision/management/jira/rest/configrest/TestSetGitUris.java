package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettings;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetGitUris extends TestSetUp {

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
	public void testRequestNullProjectKeyNullGitUriNullDefaultBranchesNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setGitUris(null, null, null, null, null, null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullGitUriProvided() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setGitUris(null, null, TestSetUpGit.GIT_URIS.get(0), "master", "", "", "").getStatus());
	}

	@Test
	public void testRequestNullProjectKeyExistsGitUriNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setGitUris(null, "TEST", null, "default", "", "", "").getStatus());
	}

	@Test
	public void testRequestNullProjectKeyExistsGitUriProvided() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setGitUris(null, "TEST", TestSetUpGit.GIT_URIS.get(0), "master", "", "", "").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyNullGitUriNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setGitUris(request, null, null, "default", "", "", "").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyNullGitUriProvided() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setGitUris(request, null, TestSetUpGit.GIT_URIS.get(0), "master", "", "", "").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsGitUriNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setGitUris(request, "TEST", null, "master", "", "", "").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsGitUriProvided() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.setGitUris(request, "TEST", TestSetUpGit.GIT_URIS.get(0), "master", "", "", "").getStatus());
	}

	@AfterClass
	public static void tearDown() {
		// reset plugin settings to default settings
		MockPluginSettingsFactory.pluginSettings = new MockPluginSettings();
	}

}
