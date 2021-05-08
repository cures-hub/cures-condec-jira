package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;

public class TestSetWebhookType extends TestConfigSuper {

	@Test
	public void testReqNullProNullTypNullActFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			configRest.setWebhookType(null, null, null, false).getStatus());
	}

	@Test
	public void testReqFilledProNullTypNullActFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			configRest.setWebhookType(request, null, null, false).getStatus());
	}

	@Test
	public void testReqNullProFilledTypNullActFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			configRest.setWebhookType(null, "TEST", null, false).getStatus());
	}

	@Test
	public void testReqNullProNullTypFilledActFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			configRest.setWebhookType(null, null, "TEST", false).getStatus());
	}

	@Test
	public void testReqNullProNullTypNullActTrue() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			configRest.setWebhookType(null, null, null, true).getStatus());
	}
}
