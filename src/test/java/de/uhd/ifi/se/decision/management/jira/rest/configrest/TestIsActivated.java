package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;

public class TestIsActivated extends TestConfigSuper {

	@Test
	public void testIsActivatedNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(), configRest.isActivated(null).getEntity());
	}

	@Test
	public void testIsActivatedProjectKeyEmpty() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(), configRest.isActivated("").getEntity());
	}

	@Test
	public void testIsActivatedProjectKeyFalse() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				configRest.isActivated("InvalidKey").getStatus());
	}

	@Test
	public void testIsActivatedProjectKeyOK() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				configRest.isActivated("TEST").getStatus());
	}
}
