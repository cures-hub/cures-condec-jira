package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectstrategy;


import de.uhd.ifi.se.decision.management.jira.model.*;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(ActiveObjectStrategyTestSetUp.AoSentenceTestDatabaseUpdater.class)
public class TestDeleteDecisionKnowledgeElement extends ActiveObjectStrategyTestSetUp{

    private DecisionKnowledgeElement dec;
    private DecisionKnowledgeElement linkedDec;

    @Before
    public void setUp(){
        intizialisation();
        dec = new DecisionKnowledgeElementImpl();
        dec.setProject("TEST");
        dec.setType(KnowledgeType.SOLUTION);

        linkedDec = new DecisionKnowledgeElementImpl();
        linkedDec.setProject("TEST");
        linkedDec.setType(KnowledgeType.DECISION);

    }

    @Test
    @NonTransactional
    public void testElementNullUserNull(){
        assertFalse(aoStrategy.deleteDecisionKnowledgeElement(null,null));
    }

    @Test
    @NonTransactional
    public void testElementNullUserFilled(){
        assertFalse(aoStrategy.deleteDecisionKnowledgeElement(null, user));
    }

    @Test
    @NonTransactional
    public void testElementFilledUserNull(){
        assertFalse(aoStrategy.deleteDecisionKnowledgeElement(dec, null));
    }

    @Test
    @NonTransactional
    public void testElementFilledUserFilledNoElementInTable(){
        assertFalse(aoStrategy.deleteDecisionKnowledgeElement(dec,user));
    }

    @Test
    @NonTransactional
    public void testElementFilledUserFilledElementInTable(){
        DecisionKnowledgeElement returnDecision= aoStrategy.insertDecisionKnowledgeElement(dec,user);
        assertTrue(aoStrategy.deleteDecisionKnowledgeElement(returnDecision,user));
    }

    @Test
    @NonTransactional
    public void testElementFilledUserFilledElementLinked(){
        DecisionKnowledgeElement returnDecision= aoStrategy.insertDecisionKnowledgeElement(dec,user);
        DecisionKnowledgeElement returnLinkDec = aoStrategy.insertDecisionKnowledgeElement(linkedDec, user);
        Link link = new LinkImpl(returnLinkDec,returnDecision);
        aoStrategy.insertLink(link, user);
        assertTrue(aoStrategy.deleteDecisionKnowledgeElement(returnDecision,user));
    }

}
