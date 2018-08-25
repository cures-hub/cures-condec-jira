package de.uhd.ifi.se.decision.management.jira.rest.configRest;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestSetGitConnector extends TestConfigSuper{

    @Test
    public void testReqNullProNullAdrNull(){
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                confRest.setGitConnector(null,null,null).getStatus());
    }

    @Test
    public void testReqNullProNullAdrFilled(){
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                confRest.setGitConnector(null,null,"TEST").getStatus());
    }

    @Test
    public void testReqNullProFilledAdrNull(){
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                confRest.setGitConnector(null,"TEST",null).getStatus());
    }

    @Test
    public void testReqNullProFilledAdrFilled(){
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                confRest.setGitConnector(null,"TEST","TEST").getStatus());
    }

    @Test
    public void testReqFilledProNullAdrNull(){
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                confRest.setGitConnector(request,null,null).getStatus());
    }

    @Test
    public void testReqFilledProFilledAdrNull(){
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                confRest.setGitConnector(request,"TEST",null).getStatus());
    }

    @Test
    public void testReqFilledProNullAdrFilled(){
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
                confRest.setGitConnector(request,null,"TEST").getStatus());
    }

    @Test
    public void testReqFilledProFilledAdrFilled(){
        assertEquals(Response.Status.OK.getStatusCode(),
                confRest.setGitConnector(request,"TEST","TEST").getStatus());
    }
}
