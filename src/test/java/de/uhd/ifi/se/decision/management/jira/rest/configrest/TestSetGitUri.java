package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;

import net.java.ao.test.jdbc.NonTransactional;

public class TestSetGitUri extends TestConfigSuper {
    private static final String INVALID_GIT_URI = "Git URI could not be set because it is null.";

    @Test
    public void testRequestNullProjectKeyNullGitUriNull() {
	assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
		configRest.setGitUris(null, null, null, null).getEntity());
    }

    @Test
    public void testRequestNullProjectKeyNullGitUriProvided() {
	assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
		configRest.setGitUris(null, null, "http://", "default").getEntity());
    }

    @Test
    public void testRequestNullProjectKeyExistsGitUriNull() {
	assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
		configRest.setGitUris(null, "TEST", null, "default").getEntity());
    }

    @Test
    public void testRequestNullProjectKeyExistsGitUriProvided() {
	assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
		configRest.setGitUris(null, "TEST", "http://", "default").getEntity());
    }

    @Test
    public void testRequestValidProjectKeyNullGitUriNull() {
	assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
		configRest.setGitUris(request, null, null, "default").getEntity());
    }

    @Test
    public void testRequestValidProjectKeyNullGitUriProvided() {
	assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
		configRest.setGitUris(request, null, "http://", "default").getEntity());
    }

    @Test
    @NonTransactional
    public void testRequestValidProjectKeyExistsGitUriNull() {
	assertEquals(getBadRequestResponse(INVALID_GIT_URI).getEntity(),
		configRest.setGitUris(request, "TEST", null, "default").getEntity());
    }

    @Test
    public void testRequestValidProjectKeyExistsGitUriProvided() {
	assertEquals(Response.ok().build().getClass(),
		configRest.setGitUris(request, "TEST", "http://", "default").getClass());
    }
}
