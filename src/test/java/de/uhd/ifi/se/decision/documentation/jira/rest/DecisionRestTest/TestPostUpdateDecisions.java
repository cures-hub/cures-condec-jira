package de.uhd.ifi.se.decision.documentation.jira.rest.DecisionRestTest;

import com.google.common.collect.ImmutableMap;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestPostUpdateDecisions extends TestDecisionSetUp {
    @Test
    public void testActionTypeNullReqNullDecNull() {
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build()
                .getEntity(), decRest.updateDecisionKnowledgeElement(null,null).getEntity());
    }

    @Test
    public void testActionTypeNullReqNullDecFilled() {
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build()
                .getEntity(), decRest.updateDecisionKnowledgeElement(null, dec).getEntity());
    }

    @Test
    public void testActionTypeNullReqFilledDecNull() {
        req.setAttribute("WithFails", false);
        req.setAttribute("NoFails", true);
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build()
                .getEntity(), decRest.updateDecisionKnowledgeElement( req, null).getEntity());
    }

    @Test
    public void testActionTypecreateReqNullDecNull() {
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build()
                .getEntity(), decRest.updateDecisionKnowledgeElement(null, null).getEntity());
    }

    @Test
    public void testActionTypecreateReqNullDecFilled() {
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build()
                .getEntity(), decRest.updateDecisionKnowledgeElement( null, dec).getEntity());
    }

    @Test
    public void testActionTypecreateReqFilledDecNull() {
        req.setAttribute("WithFails", false);
        req.setAttribute("NoFails", true);
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build()
                .getEntity(), decRest.updateDecisionKnowledgeElement( req, null).getEntity());
    }

    @Test
    public void testActionTypecreateReqFilledDecFilled() {
        req.setAttribute("WithFails", false);
        req.setAttribute("NoFails", true);
        assertEquals(Response.Status.OK.getStatusCode(), decRest.updateDecisionKnowledgeElement( req, dec).getStatus());
    }



    @Test
    public void testActionTypeEditReqNullDecNull() {
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build()
                .getEntity(), decRest.updateDecisionKnowledgeElement(null, null).getEntity());
    }

    @Test
    public void testActionTypeEditReqNullDecFilled() {
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build()
                .getEntity(), decRest.updateDecisionKnowledgeElement(null, dec).getEntity());
    }
}
