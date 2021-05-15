package de.uhd.ifi.se.decision.management.jira.rest.definitionofdonecheckingrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.DefinitionOfDoneCheckingRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetCoverageOfJiraIssue extends TestSetUp {

	protected HttpServletRequest request;
	protected DefinitionOfDoneCheckingRest dodCheckingRest;

	@Before
	public void setUp() {
		init();
		dodCheckingRest = new DefinitionOfDoneCheckingRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestValidProjectKeyValidIssueKeyValid() {
		assertEquals(Status.OK.getStatusCode(),
			dodCheckingRest.getCoverageOfJiraIssue(request, "TEST", JiraIssues.getTestJiraIssues().get(0).getKey()).getStatus());
	}

	@Test
	public void testRequestInvalidProjectKeyValidIssueKeyValid() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			dodCheckingRest.getCoverageOfJiraIssue(null, "TEST", JiraIssues.getTestJiraIssues().get(0).getKey()).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyInvalidIssueKeyValid() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			dodCheckingRest.getCoverageOfJiraIssue(request, null, JiraIssues.getTestJiraIssues().get(0).getKey()).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidIssueKeyInvalid() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			dodCheckingRest.getCoverageOfJiraIssue(request, "TEST", null));
	}
}
