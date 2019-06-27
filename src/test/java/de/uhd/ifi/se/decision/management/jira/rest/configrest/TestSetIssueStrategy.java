package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestSetIssueStrategy extends TestConfigSuper {

	@Test
	public void testRequestNullProjectKeyNullIsIssueStrategyNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setIssueStrategy(null, null, null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyNullIsIssueStrategyTrue() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setIssueStrategy(null, null, "true").getEntity());
	}

	@Test
	public void testRequestValidProjectKeyNullIsIssueStrategyNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.setIssueStrategy(request, null, null).getEntity());
	}

	@Test
	public void testRequestValidProjectKeyExistsIsIssueStrategyNull() {
		assertEquals(getBadRequestResponse(INVALID_STRATEGY).getEntity(),
				configRest.setIssueStrategy(request, "TEST", null).getEntity());
	}

	@Test
	public void testRequestValidProjectKeyExistsIsIssueStrategyTrue() {
		assertEquals(Response.ok().build().getClass(), configRest.setIssueStrategy(request, "TEST", "true").getClass());
	}

	@Test
	public void testRequestValidProjectKeyExistsIsIssueStrategyFalse() {
		assertEquals(Response.ok().build().getClass(),
				configRest.setIssueStrategy(request, "TEST", "false").getClass());
	}
}
