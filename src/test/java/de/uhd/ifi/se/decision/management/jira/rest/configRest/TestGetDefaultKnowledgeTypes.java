package de.uhd.ifi.se.decision.management.jira.rest.configRest;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;


@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGetDefaultKnowledgeTypes extends TestConfigSuper {

    @Test
    public void testProjectKeyNull(){
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getStatus(),confRest.getDefaultKnowledgeTypes(null).getStatus());
    }

    @Test
    public void testProjectKeyEmpty(){
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getStatus(),confRest.getDefaultKnowledgeTypes("").getStatus());
    }

    @Test
    public void testProjectKeyInvalid(){
        assertEquals(Response.status(Response.Status.OK).build().getStatus(),confRest.getDefaultKnowledgeTypes("InvalidKey").getStatus());
    }

    @Test
    public void testProjectKeyValid(){
        assertEquals(Response.status(Response.Status.OK).build().getStatus(),confRest.getDefaultKnowledgeTypes("TEST").getStatus());
    }
}
