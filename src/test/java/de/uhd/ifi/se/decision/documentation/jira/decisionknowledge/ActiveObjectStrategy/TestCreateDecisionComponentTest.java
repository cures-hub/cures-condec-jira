package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.ActiveObjectStrategy;


import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Kuchenbuch
 */
@RunWith(ActiveObjectsJUnitRunner.class)
public class TestCreateDecisionComponentTest extends ActiveObjectStrategyTestSetUp {

    @Test
    public void testRepresNullUserNull(){
        assertNull(aoStrategy.insertDecisionKnowledgeElement(null,null));
    }

    @Test
    public void testRepresFilledUserNull(){
        DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
        assertNull(aoStrategy.insertDecisionKnowledgeElement(dec,null));
    }

    //TODO Fixing the Test Problems (Closed Connection)
    @Ignore
    public void testRepresFilledUserNoFails(){
        DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
        dec.setProjectKey("TEST");
        dec.setType("Solution");
        ApplicationUser user = new MockApplicationUser("NoFails");
        assertNotNull(aoStrategy.insertDecisionKnowledgeElement(dec,user));
    }

    //TODO Fixing the Test Problems (Closed Connection)
    @Ignore
    public void testRepresFilledUserWithFails(){
        DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
        dec.setProjectKey("TEST");
        dec.setType("Solution");
        ApplicationUser user = new MockApplicationUser("WithFails");
        assertNull(aoStrategy.insertDecisionKnowledgeElement(dec, user));
    }
}
