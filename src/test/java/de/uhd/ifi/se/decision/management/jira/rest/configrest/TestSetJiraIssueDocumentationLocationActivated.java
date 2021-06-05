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

public class TestSetJiraIssueDocumentationLocationActivated extends TestSetUp {
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
	public void testRequestNullProjectKeyNullIsActivatedFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setJiraIssueDocumentationLocationActivated(null, null, false).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullIsActivatedTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setJiraIssueDocumentationLocationActivated(null, null, true).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyNullIsActivatedFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setJiraIssueDocumentationLocationActivated(request, null, false).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsIsActivatedTrue() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.setJiraIssueDocumentationLocationActivated(request, "TEST", true).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsIsActivatedFalse() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.setJiraIssueDocumentationLocationActivated(request, "TEST", false).getStatus());
	}
}
