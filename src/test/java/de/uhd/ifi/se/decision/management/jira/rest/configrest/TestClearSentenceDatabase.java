package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;

public class TestClearSentenceDatabase extends TestConfigSuper {

	@Test
	public void testSetActivatedRequestNullProjectKeyNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.clearSentenceDatabase(null, null).getEntity());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyTrue() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.clearSentenceDatabase(null, "TEST").getEntity());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyExistsIsActivatedFalse() {
		assertEquals(Response.ok().build().getClass(), configRest.setActivated(request, "TEST", "false").getClass());
	}

	@Test
	public void testSetActivatedUserUnauthorized() {
		request.setAttribute("WithFails", true);
		request.setAttribute("NoFails", false);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				configRest.setActivated(request, "NotTEST", "false").getStatus());
	}

	@Test
	public void testSetActivatedUserNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", false);
		request.setAttribute("SysAdmin", false);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				configRest.setActivated(request, "NotTEST", "false").getStatus());
	}
}
