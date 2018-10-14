package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.extraction.model.GenericLink;
import de.uhd.ifi.se.decision.management.jira.extraction.model.TestComment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.CommentImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestClearSentenceDatabase extends TestConfigSuper{
	
    @Test
    public void testSetActivatedRequestNullProjectKeyNull() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.clearSentenceDatabase(null, null).getEntity());
    }

    @Test
    public void testSetActivatedRequestNullProjectKeyTrue() {
        assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
                confRest.clearSentenceDatabase(null, "TEST").getEntity());
    }

    @Test
    public void testSetActivatedRequestExistsProjectKeyExistsIsActivatedFalse() {
        assertEquals(Response.ok().build().getClass(), confRest.setActivated(request, "TEST", "false").getClass());
    }

    @Test
    public void testSetActivatedUserUnauthorized() {
        request.setAttribute("WithFails", true);
        request.setAttribute("NoFails", false);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
                confRest.setActivated(request, "NotTEST", "false").getStatus());
    }

    @Test
    public void testSetActivatedUserNull() {
        request.setAttribute("WithFails", false);
        request.setAttribute("NoFails", false);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
                confRest.setActivated(request, "NotTEST", "false").getStatus());
    }
}
