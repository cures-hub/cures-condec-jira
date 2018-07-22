package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestUpdateDecisionKnowledgeElement extends TestKnowledgeRestSetUp {
    private final static String UPDATE_ERROR = "Update of decision knowledge element failed.";
    @Test
    public void testActionTypeNullReqNullDecNull() {
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
                .getEntity(), knowledgeRest.updateDecisionKnowledgeElement(null,null).getEntity());
    }

    @Test
    public void testActionTypeNullReqNullDecFilled() {
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
                .getEntity(), knowledgeRest.updateDecisionKnowledgeElement(null, decisionKnowledgeElement).getEntity());
    }

    @Test
    public void testActionTypeNullReqFilledDecNull() {
        request.setAttribute("WithFails", false);
        request.setAttribute("NoFails", true);
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
                .getEntity(), knowledgeRest.updateDecisionKnowledgeElement( request, null).getEntity());
    }

    @Test
    public void testActionTypecreateReqNullDecNull() {
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
                .getEntity(), knowledgeRest.updateDecisionKnowledgeElement(null, null).getEntity());
    }

    @Test
    public void testActionTypecreateReqNullDecFilled() {
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
                .getEntity(), knowledgeRest.updateDecisionKnowledgeElement( null, decisionKnowledgeElement).getEntity());
    }

    @Test
    public void testActionTypecreateReqFilledDecNull() {
        request.setAttribute("WithFails", false);
        request.setAttribute("NoFails", true);
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
                .getEntity(), knowledgeRest.updateDecisionKnowledgeElement( request, null).getEntity());
    }

    @Test
    public void testActionTypecreateReqFilledDecFilled() {
        request.setAttribute("WithFails", false);
        request.setAttribute("NoFails", true);
        assertEquals(Response.Status.OK.getStatusCode(), knowledgeRest.updateDecisionKnowledgeElement( request, decisionKnowledgeElement).getStatus());
    }



    @Test
    public void testActionTypeEditReqNullDecNull() {
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
                .getEntity(), knowledgeRest.updateDecisionKnowledgeElement(null, null).getEntity());
    }

    @Test
    public void testActionTypeEditReqNullDecFilled() {
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
                .getEntity(), knowledgeRest.updateDecisionKnowledgeElement(null, decisionKnowledgeElement).getEntity());
    }
}
