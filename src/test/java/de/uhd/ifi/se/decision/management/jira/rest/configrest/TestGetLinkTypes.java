package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class TestGetLinkTypes extends TestConfigSuper {
	@Test
	public void testProjectKeyNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getStatus(),
				configRest.getLinkTypes(null).getStatus());
	}

	@Test
	public void testProjectKeyEmpty() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getStatus(),
				configRest.getLinkTypes("").getStatus());
	}

	@Test
	public void testProjectKeyInvalid() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				configRest.getLinkTypes("InvalidKey").getStatus());
	}

	@Test
	public void testProjectKeyValid() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				configRest.getLinkTypes("TEST").getStatus());
	}
}
