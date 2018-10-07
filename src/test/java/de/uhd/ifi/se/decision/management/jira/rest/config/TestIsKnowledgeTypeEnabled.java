package de.uhd.ifi.se.decision.management.jira.rest.config;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestIsKnowledgeTypeEnabled extends TestConfigSuper {
    @Test
    public void testProjectKyNullKnowledgeTypeNull(){
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                confRest.isKnowledgeTypeEnabled(null,null).getEntity());
    }

    @Test
    public void testProjectKeyEmptyKnowledgeTypeNull(){
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                confRest.isKnowledgeTypeEnabled("",null).getEntity());
    }

    @Test
    public void testProjectKeyFalseKnowledgeTypeNull(){
        assertEquals(Response.status(Response.Status.OK).build().getStatus(),
                confRest.isKnowledgeTypeEnabled("InvalidKey",null).getStatus());
    }

    @Test
    public void testProjectKyNullKnowledgeTypeEmpty(){
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                confRest.isKnowledgeTypeEnabled(null,"").getEntity());
    }

    @Test
    public void testProjectKeyEmptyKnowledgeTypeEmpty(){
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                confRest.isKnowledgeTypeEnabled("","").getEntity());
    }

    @Test
    public void testProjectKeyFalseKnowledgeTypeEmpty(){
        assertEquals(Response.status(Response.Status.OK).build().getStatus(),
                confRest.isKnowledgeTypeEnabled("InvalidKey","").getStatus());
    }

    @Test
    public void testProjectKyNullKnowledgeTypeFilled(){
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                confRest.isKnowledgeTypeEnabled(null, KnowledgeType.SOLUTION.toString()).getEntity());
    }

    @Test
    public void testProjectKeyEmptyKnowledgeTypeFilled(){
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                confRest.isKnowledgeTypeEnabled("",KnowledgeType.SOLUTION.toString()).getEntity());
    }

    @Test
    public void testProjectKeyFalseKnowledgeTypeFilled(){
        assertEquals(Response.status(Response.Status.OK).build().getStatus(),
                confRest.isKnowledgeTypeEnabled("InvalidKey",KnowledgeType.SOLUTION.toString()).getStatus());
    }

    @Test
    public void testIsIssueStrategyProjectKeyOK(){
        assertEquals(Response.status(Response.Status.OK).build().getStatus(),confRest.isKnowledgeTypeEnabled("TEST",KnowledgeType.SOLUTION.toString()).getStatus());
    }
}
