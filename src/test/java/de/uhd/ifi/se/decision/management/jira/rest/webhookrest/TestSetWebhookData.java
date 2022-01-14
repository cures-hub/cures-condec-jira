package de.uhd.ifi.se.decision.management.jira.rest.webhookrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.WebhookRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConfiguration;

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
	public void testRequestNullProjectKeyNullUrlNullSecretNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(null, null, null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullUrlNullSecretValid() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(null, null, null, "42").getStatus());
	}

	@Test
	public void testRequestNullProjectKeyValidUrlNullSecretNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(null, "TEST", null, null).getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyNullUrlNullSecretNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(request, null, null, null).getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyValidUrlNullSecretNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(request, "TEST", null, null).getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyValidUrlNullSecretValid() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(request, "TEST", null, "42").getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyValidUrlValidSecretNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setWebhookData(request, "TEST", "http://", null).getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyValidUrlValidSecretValid() {
		assertEquals(Response.Status.OK.getStatusCode(),
				webhookRest.setWebhookData(request, "TEST", "http://", "42").getStatus());
		ConfigPersistenceManager.saveWebhookConfiguration("TEST", new WebhookConfiguration());
	}
}
