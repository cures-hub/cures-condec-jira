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

public class TestSetWebhookData extends TestSetUp {

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
	public void testReqNullProNullAdrNullSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(null, null, null, null).getStatus());
	}

	@Test
	public void testReqNullProNullAdrNullSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(null, null, null, "TEST").getStatus());
	}

	@Test
	public void testReqNullProNullAdrFilledSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(null, null, "TEST", null).getStatus());
	}

	@Test
	public void testReqNullProNullAdrFilledSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(null, null, "TEST", "TEST").getStatus());
	}

	@Test
	public void testReqNullProFilledAdrNullSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(null, "TEST", null, null).getStatus());
	}

	@Test
	public void testReqNullProFilledAdrNullSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(null, "TEST", null, "TEST").getStatus());
	}

	@Test
	public void testReqNullProFilledAdrFilledSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(null, "TEST", "TEST", null).getStatus());
	}

	@Test
	public void testReqNullProFilledAdrFilledSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(null, "TEST", "TEST", "TEST").getStatus());
	}

	@Test
	public void testReqFilledProNullAdrNullSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(request, null, null, null).getStatus());
	}

	@Test
	public void testReqFilledProNullAdrNullSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(request, null, null, "TEST").getStatus());
	}

	@Test
	public void testReqFilledProNullAdrFilledSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(request, null, "TEST", null).getStatus());
	}

	@Test
	public void testReqFilledProNullAdrFilledSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(request, null, "TEST", "TEST").getStatus());
	}

	@Test
	public void testReqFilledProFilledAdrNullSecNull() {
		((MockHttpServletRequest) request).setParameter("projectKey", "TEST");
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(request, "TEST", null, null).getStatus());
	}

	@Test
	public void testReqFilledProFilledAdrNullSecFilled() {
		((MockHttpServletRequest) request).setParameter("projectKey", "TEST");
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(request, "TEST", null, "TEST").getStatus());
	}

	@Test
	public void testReqFilledProFilledAdrFilledSecNull() {
		((MockHttpServletRequest) request).setParameter("projectKey", "TEST");
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(request, "TEST", "TEST", null).getStatus());
	}

	@Test
	public void testReqFilledProFilledAdrFilledSecFilled() {
		((MockHttpServletRequest) request).setParameter("projectKey", "TEST");
		assertEquals(Response.Status.OK.getStatusCode(),
				webhookRest.setWebhookData(request, "TEST", "TEST", "TEST").getStatus());
	}
}
