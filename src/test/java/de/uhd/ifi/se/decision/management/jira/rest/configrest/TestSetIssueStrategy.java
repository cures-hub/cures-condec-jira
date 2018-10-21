package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestSetIssueStrategy extends TestConfigSuper{
    @Test
    public void testdoPutrequestNullKeyNullIsIssueStategydNull() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setIssueStrategy(null, null, null).getEntity());
    }

    @Test
    public void testdoPutrequestNullKeyNullIsIssueStategyTrue() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setIssueStrategy(null, null, "true").getEntity());
    }

    @Test
    public void testdoPutrequestNullKeyNullIsIssueStategyFalse() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setIssueStrategy(null, null, "false").getEntity());
    }

    @Test
    public void testdoPutrequestNullKeyExistsIsIssueStategyNull() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setIssueStrategy(null, "TEST", null).getEntity());
    }

    @Test
    public void testdoPutrequestNullKeyExistsIsIssueStategyTrue() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setIssueStrategy(null, "TEST", "true").getEntity());
    }

    @Test
    public void testdoPutrequestNullKeyExistsIsIssueStategyFalse() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setIssueStrategy(null, "TEST", "false").getEntity());
    }

    @Test
    public void testdoPutrequestNullKeyDontExistIsIssueStategyNull() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setIssueStrategy(null, "NotTEST", null).getEntity());
    }

    @Test
    public void testdoPutrequestNullKeyDontExistIsIssueStategyTrue() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setIssueStrategy(null, "NotTEST", "true").getEntity());
    }

    @Test
    public void testdoPutrequestNullKeyDontExistIsIssueStategyFalse() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.setIssueStrategy(null, "NotTEST", "false").getEntity());
    }

    @Test
    public void testdoPutrequestExKeyNullIsIssueStategyNull() {
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                confRest.setIssueStrategy(request, null, null).getEntity());
    }

    @Test
    public void testdoPutrequestExKeyNullIsIssueStategyTrue() {
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                confRest.setIssueStrategy(request, null, "true").getEntity());
    }

    @Test
    public void testdoPutrequestExKeyNullIsIssueStategyFalse() {
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                confRest.setIssueStrategy(request, null, "false").getEntity());
    }

    @Test
    public void testdoPutrequestExKeyExistsIsIssueStategyNull() {
        assertEquals(getBadRequestResponse(INVALID_STRATEGY).getEntity(),
                confRest.setIssueStrategy(request, "TEST", null).getEntity());
    }

    @Test
    public void testdoPutrequestExKeyExistsIsIssueStategyTrue() {
        assertEquals(Response.ok().build().getClass(), confRest.setIssueStrategy(request, "TEST", "true").getClass());
    }

    @Test
    public void testdoPutrequestExKeyExistsIsIssueStategyFalse() {
        assertEquals(Response.ok().build().getClass(), confRest.setIssueStrategy(request, "TEST", "false").getClass());
    }

    @Test
    public void testdoPutUserUnauthorized() {
        request.setAttribute("WithFails", true);
        request.setAttribute("NoFails", false);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
                confRest.setIssueStrategy(request, "NotTEST", "false").getStatus());
    }

    @Test
    public void testdoPutUserNull() {
        request.setAttribute("WithFails", false);
        request.setAttribute("NoFails", false);
        request.setAttribute("SysAdmin", false);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
                confRest.setIssueStrategy(request, "NotTEST", "false").getStatus());
    }
}
