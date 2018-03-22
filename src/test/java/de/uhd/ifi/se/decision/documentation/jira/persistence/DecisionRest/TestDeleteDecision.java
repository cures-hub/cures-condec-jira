package de.uhd.ifi.se.decision.documentation.jira.persistence.DecisionRest;

import com.google.common.collect.ImmutableMap;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestDeleteDecision extends TestDecisionSetUp {

    @Test
    public void testActionTypeDeleteReqFilledDecFilled() {
        req.setAttribute("WithFails", false);
        req.setAttribute("NoFails", true);
        assertEquals(Response.Status.OK.getStatusCode(),decRest.deleteDecision("delete", req, dec).getStatus());
    }

    @Test
    public void testActionTypeDeleteErrorReqFilledDecFilled() {
        req.setAttribute("WithFails", true);
        req.setAttribute("NoFails", false);
        assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Deletion of decision knowledge element failed.")).build().getEntity(),decRest.deleteDecision("delete", req, dec).getEntity());
    }

    @Test
    public void testActionTypeOtherReqFilledDecFilled() {
        req.setAttribute("WithFails", false);
        req.setAttribute("NoFails", true);
        assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Unknown actionType.")).build().getEntity(),decRest.deleteDecision("test", req, dec).getEntity());
    }
}
