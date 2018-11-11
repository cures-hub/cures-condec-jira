package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGetDefaultKnowledgeTypes extends TestConfigSuper {

	@Test
	public void testProjectKeyNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getStatus(),
				configRest.getDefaultKnowledgeTypes(null).getStatus());
	}

	@Test
	public void testProjectKeyEmpty() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getStatus(),
				configRest.getDefaultKnowledgeTypes("").getStatus());
	}

	@Test
	public void testProjectKeyInvalid() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				configRest.getDefaultKnowledgeTypes("InvalidKey").getStatus());
	}

	@Test
	public void testProjectKeyValid() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				configRest.getDefaultKnowledgeTypes("TEST").getStatus());
	}
}
