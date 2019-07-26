package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;

import net.java.ao.test.jdbc.NonTransactional;

public class TestSaveArffFile extends TestConfigSuper {

	@Test
	public void testRequestNullProjectKeyNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.saveArffFile(null, null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyExists() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.saveArffFile(null, "TEST").getEntity());
	}

	@Test
	public void testRequestValidProjectKeyNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.saveArffFile(request, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestValidProjectKeyExists() {
		assertEquals(Response.ok().build().getClass(), configRest.saveArffFile(request, "TEST").getClass());
	}
}
