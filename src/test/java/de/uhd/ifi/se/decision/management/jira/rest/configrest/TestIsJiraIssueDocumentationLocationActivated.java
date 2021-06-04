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

public class TestIsJiraIssueDocumentationLocationActivated extends TestSetUp {
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
	public void testProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.isJiraIssueDocumentationLocationActivated(null).getStatus());
	}

	@Test
	public void testProjectKeyEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.isJiraIssueDocumentationLocationActivated("").getStatus());
	}

	@Test
	public void testProjectKeyInvalid() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.isJiraIssueDocumentationLocationActivated("InvalidKey").getStatus());
	}

	@Test
	public void testProjectKeyValid() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.isJiraIssueDocumentationLocationActivated("TEST").getStatus());
	}
}
