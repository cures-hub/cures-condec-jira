package de.uhd.ifi.se.decision.management.jira.rest.nudgingrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.rest.NudgingRest;
import de.uhd.ifi.se.decision.management.jira.rest.configrest.TestConfigSuper;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestActivatePromptEventForDefinitionOfDoneChecking extends TestConfigSuper {
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
	public void testRequestValidProjectValidEventTypeValidIsActivatedTrue() {
		assertEquals(Status.OK.getStatusCode(),
				nudgingRest.activatePromptEventForDefinitionOfDoneChecking(request, "TEST", "done", true).getStatus());
	}

	@Test
	public void testRequestNullProjectValidEventTypeValidIsActivatedTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				nudgingRest.activatePromptEventForDefinitionOfDoneChecking(null, "TEST", "done", true).getStatus());
	}

	@Test
	public void testRequestValidProjectNullEventTypeValidIsActivatedTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				nudgingRest.activatePromptEventForDefinitionOfDoneChecking(request, null, "done", true).getStatus());
	}
}
