package de.uhd.ifi.se.decision.management.jira.rest.webhookrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.WebhookRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetWebhookEnabled extends TestSetUp {

	private HttpServletRequest request;
	private WebhookRest webhookRest;

	@Before
	public void setUp() {
		init();
		webhookRest = new WebhookRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestFilledProjectKeyValidActivationFalse() {
		assertEquals(Response.Status.OK.getStatusCode(),
				webhookRest.setWebhookEnabled(request, "TEST", false).getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyValidActivationTrue() {
		assertEquals(Response.Status.OK.getStatusCode(),
				webhookRest.setWebhookEnabled(request, "TEST", true).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullActivationFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookEnabled(null, null, false).getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyNullActivationFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookEnabled(request, null, false).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyValidActivationFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookEnabled(null, "TEST", false).getStatus());
	}
}
