package de.uhd.ifi.se.decision.documentation.jira.rest.DecisionRestTest;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.documentation.jira.rest.DecisionsRest;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGetDecisionKnowledgeElement extends TestDecisionSetUp {
    private EntityManager entityManager;
    private DecisionsRest decRest;

    @Before
    public void setUp() {
        decRest = new DecisionsRest();
        initialization();
        new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());
    }

    @Test
    public void testIssueIdZeroProjectKeyNull() {
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error",
                        "Decision knowledge element could not be received due to a bad request (element id or project key was missing)."))
                .build().getEntity(), decRest.getDecisionKnowledgeElement(0, null).getEntity());
    }

    @Test
    public void testIssueIdFilledProjectKeyNull() {
        assertEquals(Response.status(Response.Status.BAD_REQUEST)
                .entity(ImmutableMap.of("error",
                        "Decision knowledge element could not be received due to a bad request (element id or project key was missing)."))
                .build().getEntity(), decRest.getDecisionKnowledgeElement(7, null).getEntity());
    }

    @Test
    public void testIssueIdFilledProjectKeyDontExist() {
        assertEquals(200,decRest.getDecisionKnowledgeElement(7,"NotTEST").getStatus());
    }


    @Test
    public void testIssueIdFilledProjectKeyExist() {
        assertEquals(Response.Status.OK.getStatusCode(),decRest.getDecisionKnowledgeElement(7,
                "TEST").getStatus());
    }
}
