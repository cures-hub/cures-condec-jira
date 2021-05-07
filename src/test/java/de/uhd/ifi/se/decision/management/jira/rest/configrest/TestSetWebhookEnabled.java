package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;

public class TestSetWebhookEnabled extends TestConfigSuper {

	@Test
	public void testReqNullProNullActNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			configRest.setWebhookEnabled(null, null, null).getStatus());
	}

	@Test
	public void testReqFilledProNullActNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			configRest.setWebhookEnabled(request, null, null).getStatus());
	}

	@Test
	public void testReqNullProFilledActNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			configRest.setWebhookEnabled(null, "TEST", null).getStatus());
	}

	@Test
	public void testReqNullProNullActFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			configRest.setWebhookEnabled(null, null, "Test").getStatus());
	}
}
