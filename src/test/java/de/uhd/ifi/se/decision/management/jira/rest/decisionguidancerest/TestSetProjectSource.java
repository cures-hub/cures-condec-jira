package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetProjectSource extends TestSetUp {

	private DecisionGuidanceRest decisionGuidanceRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		decisionGuidanceRest = new DecisionGuidanceRest();
		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
	}

	@Test
	public void testSetProjectSourceValid() {
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.setProjectSource(request, "TEST", "TEST", true).getStatus());
	}

	@Test
	public void testSetProjectSourceInvalidProjectKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.setProjectSource(request, "", "TEST", false).getStatus());
	}

	@Test
	public void testSetProjectSourceInvalidProjectKeyOfKnowledgeSource() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.setProjectSource(request, "TEST", "", false).getStatus());
	}
}
