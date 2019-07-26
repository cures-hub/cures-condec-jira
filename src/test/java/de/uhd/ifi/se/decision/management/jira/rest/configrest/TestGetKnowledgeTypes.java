package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;

public class TestGetKnowledgeTypes extends TestConfigSuper {
	@Test
	public void testProjectKeyNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getStatus(),
				configRest.getKnowledgeTypes(null).getStatus());
	}

	@Test
	public void testProjectKeyEmpty() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getStatus(),
				configRest.getKnowledgeTypes("").getStatus());
	}

	@Test
	public void testProjectKeyInvalid() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				configRest.getKnowledgeTypes("InvalidKey").getStatus());
	}

	@Test
	public void testProjectKeyValid() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				configRest.getKnowledgeTypes("TEST").getStatus());
	}
}
