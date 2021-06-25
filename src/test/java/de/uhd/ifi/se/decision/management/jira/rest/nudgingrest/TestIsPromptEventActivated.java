package de.uhd.ifi.se.decision.management.jira.rest.nudgingrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.NudgingRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestIsPromptEventActivated extends TestSetUp {
	protected HttpServletRequest request;
	protected NudgingRest nudgingRest;

	@Before
	public void setUp() {
		init();
		nudgingRest = new NudgingRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestValidFeatureValidJiraIssueValidActionIdValid() {
		assertEquals(Status.OK.getStatusCode(),
				nudgingRest.isPromptEventActivated(request, "DOD_CHECKING", 1, 1).getStatus());
	}
}
