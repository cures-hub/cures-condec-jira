package de.uhd.ifi.se.decision.management.jira.rest.decisionsrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestDeleteDecision extends TestDecisionSetUp {

    private final static String DELETION_ERROR = "Deletion of decision knowledge element failed.";

    @Test
    public void testActionTypeDeleteReqFilledDecFilled() {
        request.setAttribute("WithFails", false);
        request.setAttribute("NoFails", true);
        assertEquals(Response.Status.OK.getStatusCode(),decisionsRest.deleteDecisionKnowledgeElement( request, decisionKnowledgeElement).getStatus());
    }

    @Test
    public void testActionTypeDeleteErrorReqFilledDecFilled() {
        request.setAttribute("WithFails", true);
        request.setAttribute("NoFails", false);
        assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", DELETION_ERROR)).build().getEntity(),decisionsRest.deleteDecisionKnowledgeElement(request, decisionKnowledgeElement).getEntity());
    }

    @Test
    public void testRequestNullDecNull(){
        assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", DELETION_ERROR)).build().getEntity(),decisionsRest.deleteDecisionKnowledgeElement(null, null).getEntity());
    }
}
