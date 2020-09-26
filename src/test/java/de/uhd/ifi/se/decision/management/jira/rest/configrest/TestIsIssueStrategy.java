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

public class TestIsIssueStrategy extends TestSetUp {
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
	public void testIsIssueStrategyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isIssueStrategy(null).getStatus());
	}

	@Test
	public void testIsIssueStrategyProjectKeyEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isIssueStrategy("").getStatus());
	}

	@Test
	public void testIsIssueStrategyProjectKeyFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isIssueStrategy("InvalidKey").getStatus());
	}

	@Test
	public void testIsIssueStrategyProjectKeyOK() {
		assertEquals(Status.OK.getStatusCode(), configRest.isIssueStrategy("TEST").getStatus());
	}
}
