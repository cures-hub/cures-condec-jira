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

public class TestSetWebhookType extends TestSetUp {

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
	public void testReqNullProNullTypNullActFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookType(null, null, null, false).getStatus());
	}

	@Test
	public void testReqFilledProNullTypNullActFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookType(request, null, null, false).getStatus());
	}

	@Test
	public void testReqNullProFilledTypNullActFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookType(null, "TEST", null, false).getStatus());
	}

	@Test
	public void testReqNullProNullTypFilledActFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookType(null, null, "TEST", false).getStatus());
	}

	@Test
	public void testReqNullProNullTypNullActTrue() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookType(null, null, null, true).getStatus());
	}
}
