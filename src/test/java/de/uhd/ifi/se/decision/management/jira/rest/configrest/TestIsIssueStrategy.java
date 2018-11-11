package de.uhd.ifi.se.decision.management.jira.rest.configrest;


import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestIsIssueStrategy extends TestConfigSuper {

    @Test
    public void testIsIssueStrategyNull(){
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                configRest.isIssueStrategy(null).getEntity());
    }

    @Test
    public void testIsIssueStrategyProjectKeyEmpty(){
        assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
                configRest.isIssueStrategy("").getEntity());
    }

    @Test
    public void testIsIssueStrategyProjectKeyFalse(){
        assertEquals(Response.status(Response.Status.OK).build().getStatus(),
                configRest.isIssueStrategy("InvalidKey").getStatus());
    }

    @Test
    public void testIsIssueStrategyProjectKeyOK(){
        assertEquals(Response.status(Response.Status.OK).build().getStatus(),configRest.isIssueStrategy("TEST").getStatus());
    }
}
