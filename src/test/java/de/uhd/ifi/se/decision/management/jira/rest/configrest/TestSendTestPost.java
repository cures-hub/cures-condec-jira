package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;

public class TestSendTestPost extends TestConfigSuper {

	@Test
	public void testReqNullProNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			configRest.sendTestPost(null, null).getStatus());
	}

	@Test
	public void testReqFilledProNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			configRest.sendTestPost(request, null).getStatus());
	}

	@Test
	public void testReqNullProFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
			configRest.sendTestPost(null, "TEST").getStatus());
	}
}
