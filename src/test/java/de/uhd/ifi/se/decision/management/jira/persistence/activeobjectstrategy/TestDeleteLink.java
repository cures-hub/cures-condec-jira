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
public class TestDeleteLink extends ActiveObjectStrategyTestSetUp {

    private Link link;

    @Before
    public void setUp(){
        intizialisation();
        DecisionKnowledgeElement dec = new DecisionKnowledgeElementImpl();
        dec.setProject("TEST");
        dec.setType(KnowledgeType.SOLUTION);

        DecisionKnowledgeElement linkedDec = new DecisionKnowledgeElementImpl();
        linkedDec.setProject("TEST");
        linkedDec.setType(KnowledgeType.DECISION);

        DecisionKnowledgeElement returnDecision= aoStrategy.insertDecisionKnowledgeElement(dec,user);
        DecisionKnowledgeElement returnLinkDec = aoStrategy.insertDecisionKnowledgeElement(linkedDec, user);
        link = new LinkImpl(returnLinkDec,returnDecision);
        aoStrategy.insertLink(link, user);
    }

    @Test(expected = NullPointerException.class)
    @NonTransactional
    public void testLinkNullUserNull(){
        aoStrategy.deleteLink(null, null);
    }

    @Test (expected = NullPointerException.class)
    @NonTransactional
    public void testLinkNullUserFilled(){
        aoStrategy.deleteLink(null, user);
    }

    @Test
    @NonTransactional
    public void testLinkFilledUserNull(){
        assertTrue(aoStrategy.deleteLink(link, null));
    }

    @Test
    @NonTransactional
    public void testLinkFilledUserFilledLinkNotInTable(){
        Link emptyLink = new LinkImpl();
        assertFalse(aoStrategy.deleteLink(emptyLink, user));
    }

    @Test
    @NonTransactional
    public void testLinkFilledUserFilledLinkInTable(){
        assertTrue(aoStrategy.deleteLink(link,user));
    }
}
