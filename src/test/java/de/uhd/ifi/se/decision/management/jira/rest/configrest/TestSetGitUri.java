package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestSetGitUri extends TestConfigSuper {
	private static final String INVALID_GIT_URI = "Git URI could not be set because it is null.";

	@Test
	public void testRequestNullProjectKeyNullGitUriNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setGitUri(null, null, null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyNullGitUriProvided() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setGitUri(null, null, "http://").getEntity());
	}

	@Test
	public void testRequestNullProjectKeyExistsGitUriNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setGitUri(null, "TEST", null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyExistsGitUriProvided() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setGitUri(null, "TEST", "http://").getEntity());
	}

	@Test
	public void testRequestValidProjectKeyNullGitUriNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.setGitUri(request, null, null).getEntity());
	}

	@Test
	public void testRequestValidProjectKeyNullGitUriProvided() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.setGitUri(request, null, "http://").getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestValidProjectKeyExistsGitUriNull() {
		assertEquals(getBadRequestResponse(INVALID_GIT_URI).getEntity(),
				configRest.setGitUri(request, "TEST", null).getEntity());
	}

	@Test
	public void testRequestValidProjectKeyExistsGitUriProvided() {
		assertEquals(Response.ok().build().getClass(), configRest.setGitUri(request, "TEST", "http://").getClass());
	}
}
