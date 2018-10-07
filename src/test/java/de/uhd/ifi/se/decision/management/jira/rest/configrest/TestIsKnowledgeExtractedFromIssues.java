package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestIsKnowledgeExtractedFromIssues extends TestConfigSuper {
    @Test
    public void testProjectKeyNull(){
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                confRest.isKnowledgeExtractedFromIssues(null).getEntity());
    }

    @Test
    public  void testProjectKeyEmpty(){
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                confRest.isKnowledgeExtractedFromIssues("").getEntity());
    }

    @Test
    public void testProjectKeyInvalid(){
        assertEquals(Response.status(Response.Status.OK).build().getStatus(),
                confRest.isKnowledgeExtractedFromIssues("InvalidKey").getStatus());
    }

    @Test
    public void testProjectKeyValid(){
        assertEquals(Response.status(Response.Status.OK).build().getStatus(),
                confRest.isKnowledgeExtractedFromIssues("TEST").getStatus());
    }
}
