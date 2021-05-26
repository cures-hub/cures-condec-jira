package de.uhd.ifi.se.decision.management.jira.rest.nudgingrest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import de.uhd.ifi.se.decision.management.jira.rest.NudgingRest;
import de.uhd.ifi.se.decision.management.jira.rest.configrest.TestConfigSuper;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class TestActivatePromptEventForNonValidatedElementsChecking extends TestConfigSuper {
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
		assertEquals(Response.Status.OK.getStatusCode(),
			nudgingRest.activatePromptEventForNonValidatedElementsChecking(request, "TEST", "done", true).getStatus());
	}

	@Test
	public void testRequestNullProjectValidEventTypeValidIsActivatedTrue() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			nudgingRest.activatePromptEventForNonValidatedElementsChecking(null, "TEST", "done", true).getStatus());
	}

	@Test
	public void testRequestValidProjectNullEventTypeValidIsActivatedTrue() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			nudgingRest.activatePromptEventForNonValidatedElementsChecking(request, null, "done", true).getStatus());
	}
}
