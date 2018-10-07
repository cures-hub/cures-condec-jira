package de.uhd.ifi.se.decision.management.jira.rest.config;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestSetActivated extends TestConfigSuper{
    @Test
    public void testSetActivatedRequestNullProjectKeyNullIsActivatedNull() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setActivated(null, null, null).getEntity());
    }

    @Test
    public void testSetActivatedRequestNullProjectKeyNullIsActivatedTrue() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setActivated(null, null, "true").getEntity());
    }

    @Test
    public void testSetActivatedRequestNullProjectKeyNullIsActivatedFalse() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setActivated(null, null, "false").getEntity());
    }

    @Test
    public void testSetActivatedRequestNullProjectKeyExistsIsActivatedNull() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setActivated(null, "TEST", null).getEntity());
    }

    @Test
    public void testSetActivatedRequestNullProjectKeyExistsIsActivatedTrue() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setActivated(null, "TEST", "true").getEntity());
    }

    @Test
    public void testSetActivatedRequestNullProjectKeyExistsIsActivatedFalse() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setActivated(null, "TEST", "false").getEntity());
    }

    @Test
    public void testSetActivatedRequestNullProjectKeyDoesNotExistIsActivatedNull() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setActivated(null, "NotTEST", null).getEntity());
    }

    @Test
    public void testSetActivatedRequestNullProjectKeyDoesNotExistIsActivatedTrue() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setActivated(null, "NotTEST", "true").getEntity());
    }

    @Test
    public void testSetActivatedRequestNullProjectKeyDoesNotExistIsActivatedFalse() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setActivated(null, "NotTEST", "false").getEntity());
    }

    @Test
    public void testSetActivatedRequestExistsProjectKeyNullIsActivatedNull() {
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                confRest.setActivated(request, null, null).getEntity());
    }

    @Test
    public void testSetActivatedRequestExistsProjectKeyNullIsActivatedTrue() {
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                confRest.setActivated(request, null, "true").getEntity());
    }

    @Test
    public void testSetActivatedRequestExistsProjectKeyNullIsActivatedFalse() {
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                confRest.setActivated(request, null, "false").getEntity());
    }

    @Test
    public void testSetActivatedRequestExistsProjectKeyExistsIsActivatedNull() {
        assertEquals(getBadRequestResponse(INVALID_ACTIVATION).getEntity(),
                confRest.setActivated(request, "TEST", null).getEntity());
    }

    @Test
    public void testSetActivatedRequestExistsProjectKeyExistsIsActivatedTrue() {
        assertEquals(Response.ok().build().getClass(), confRest.setActivated(request, "TEST", "true").getClass());
    }

    @Test
    public void testSetActivatedRequestExistsProjectKeyExistsIsActivatedFalse() {
        assertEquals(Response.ok().build().getClass(), confRest.setActivated(request, "TEST", "false").getClass());
    }

    @Test
    public void testSetActivatedUserUnauthorized() {
        request.setAttribute("WithFails", true);
        request.setAttribute("NoFails", false);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
                confRest.setActivated(request, "NotTEST", "false").getStatus());
    }

    @Test
    public void testSetActivatedUserNull() {
        request.setAttribute("WithFails", false);
        request.setAttribute("NoFails", false);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
                confRest.setActivated(request, "NotTEST", "false").getStatus());
    }
}
