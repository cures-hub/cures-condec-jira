package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetWebhookEnabled extends TestSetUp {

	private HttpServletRequest request;
	private ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestNullProjectKeyNullActivationFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookEnabled(null, null, false).getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyNullActivationFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookEnabled(request, null, false).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyValidActivationFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookEnabled(null, "TEST", false).getStatus());
	}
}
