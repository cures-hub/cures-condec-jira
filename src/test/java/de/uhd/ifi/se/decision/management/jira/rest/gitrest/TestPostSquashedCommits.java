package de.uhd.ifi.se.decision.management.jira.rest.gitrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettings;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;
import de.uhd.ifi.se.decision.management.jira.rest.GitRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestPostSquashedCommits extends TestSetUp {

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
	@NonTransactional
	public void testOk() {
		assertEquals(Status.OK.getStatusCode(), gitRest.setPostDefaultBranchCommits(request, "TEST", true).getStatus());
	}

	@Test
	@NonTransactional
	public void testInvalidKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.setPostDefaultBranchCommits(request, null, true).getStatus());
	}

	@Test
	@NonTransactional
	public void testEmptyKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				gitRest.setPostDefaultBranchCommits(request, "", true).getStatus());
	}

	@After
	public void tidyUp() {
		MockPluginSettingsFactory.pluginSettings = new MockPluginSettings();
	}
}
