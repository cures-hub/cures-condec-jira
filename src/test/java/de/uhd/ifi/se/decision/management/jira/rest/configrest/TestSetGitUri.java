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
	private static final String NOT_VERIFIED_GIT_URI = "Git URI could not be verified.";

	@Test
	public void testRequestNullProjectKeyNullGitUriNull() {
		Response response = configRest.setGitUri(null, null, null);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode()
				, response.getStatus());
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				response.getEntity());
	}

	@Test
	public void testRequestNullProjectKeyNullGitUriProvided() {
		Response response = configRest.setGitUri(null, null, "http://");
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode()
				, response.getStatus());
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity()
				, response.getEntity());
	}

	@Test
	public void testRequestNullProjectKeyExistsGitUriNull() {
		Response response = configRest.setGitUri(null, "TEST", null);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode()
				, response.getStatus());
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				response.getEntity());
	}

	@Test
	public void testRequestNullProjectKeyExistsGitUriProvided() {
		Response response = configRest.setGitUri(null, "TEST", "http://");
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode()
				, response.getStatus());
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity()
				, response.getEntity());
	}

	@Test
	public void testRequestValidProjectKeyNullGitUriNull() {
		Response response = configRest.setGitUri(request, null, null);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode()
				, response.getStatus());
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity()
				, response.getEntity());
	}

	@Test
	public void testRequestValidProjectKeyNullGitUriProvided() {
		Response response = configRest.setGitUri(request, null, "http://");
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode()
				, response.getStatus());
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				response.getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestValidProjectKeyExistsGitUriNull() {
		Response response = configRest.setGitUri(request, "TEST", null);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode()
				, response.getStatus());
		assertEquals(getBadRequestResponse(INVALID_GIT_URI).getEntity(),
				response.getEntity());
	}

	@Test
	public void testRequestValidProjectKeyExistsGitUriProvidedInvalid() {
		Response response = configRest.setGitUri(request, "TEST", "http://");
		assertEquals(Response.Status.SERVICE_UNAVAILABLE.getStatusCode()
				, response.getStatus());
		assertEquals(getServiceUnavailableRequestResponse(NOT_VERIFIED_GIT_URI).getEntity(),
				response.getEntity());
	}

	@Test
	public void testRequestValidProjectKeyExistsGitUriProvidedValid() {
		Response response = configRest.setGitUri(request
				, "TEST"
				, "https://github.com/rtyley/small-test-repo.git");
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
}
