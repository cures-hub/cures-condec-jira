package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestPostSquashedCommits extends TestConfigSuper {

	@Test
	public void testOk() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
		configRest.setPostSquashedCommits(request, "TEST", "true"));
	}

	@Test
	public void testInvalidKey() {
		assertNotEquals(Response.status(Response.Status.OK).build().getStatus(),
			configRest.setPostSquashedCommits(request, "TUTU", "true"));
	}

	@Test
	public void testEmptyKey() {
		assertNotEquals(Response.status(Response.Status.OK).build().getStatus(),
			configRest.setPostSquashedCommits(request, "", "true"));
	}

	@Test
	public void testInvalidBooleanValue() {
		assertNotEquals(Response.status(Response.Status.OK).build().getStatus(),
			configRest.setPostSquashedCommits(request, "TEST", "ok"));
	}
}
