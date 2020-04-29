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

public class TestSetIssueStrategy extends TestSetUp {
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
	public void testRequestNullProjectKeyNullIsIssueStrategyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setIssueStrategy(null, null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullIsIssueStrategyTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setIssueStrategy(null, null, "true").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyNullIsIssueStrategyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setIssueStrategy(request, null, null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsIsIssueStrategyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setIssueStrategy(request, "TEST", null).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsIsIssueStrategyTrue() {
		assertEquals(Status.OK.getStatusCode(), configRest.setIssueStrategy(request, "TEST", "true").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyExistsIsIssueStrategyFalse() {
		assertEquals(Status.OK.getStatusCode(), configRest.setIssueStrategy(request, "TEST", "false").getStatus());
	}
}
