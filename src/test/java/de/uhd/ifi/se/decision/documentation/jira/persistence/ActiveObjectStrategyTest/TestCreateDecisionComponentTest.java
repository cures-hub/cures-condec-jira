package de.uhd.ifi.se.decision.documentation.jira.persistence.ActiveObjectStrategyTest;


import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.documentation.jira.model.KnowledgeType;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestCreateDecisionComponentTest extends ActiveObjectStrategyTestSetUp {

    @Test
    public void testRepresNullUserNull(){
        assertNull(aoStrategy.insertDecisionKnowledgeElement(null,null));
    }

    @Test
    public void testRepresFilledUserNull(){
        DecisionKnowledgeElementImpl dec = new DecisionKnowledgeElementImpl();
        assertNull(aoStrategy.insertDecisionKnowledgeElement(dec,null));
    }

    //TODO Fixing the Test Problems (Closed Connection)
    @Ignore
    public void testRepresFilledUserNoFails(){
        DecisionKnowledgeElementImpl dec = new DecisionKnowledgeElementImpl();
        dec.setProjectKey("TEST");
        dec.setType(KnowledgeType.SOLUTION);
        ApplicationUser user = new MockApplicationUser("NoFails");
        assertNotNull(aoStrategy.insertDecisionKnowledgeElement(dec,user));
    }

    //TODO Fixing the Test Problems (Closed Connection)
    @Ignore
    public void testRepresFilledUserWithFails(){
        DecisionKnowledgeElementImpl dec = new DecisionKnowledgeElementImpl();
        dec.setProjectKey("TEST");
        dec.setType(KnowledgeType.SOLUTION);
        ApplicationUser user = new MockApplicationUser("WithFails");
        assertNull(aoStrategy.insertDecisionKnowledgeElement(dec, user));
    }
}
