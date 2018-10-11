package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectstrategy;

import de.uhd.ifi.se.decision.management.jira.model.*;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(ActiveObjectStrategyTestSetUp.AoSentenceTestDatabaseUpdater.class)
public class TestGetElementsLinkedWith extends ActiveObjectStrategyTestSetUp{

    private Link link;

    @Before
    public void setUp(){
        intizialisation();

        DecisionKnowledgeElement dec = new DecisionKnowledgeElementImpl();
        dec.setProject("TEST");
        dec.setId(13);
        dec.setType(KnowledgeType.ASSESSMENT);

        DecisionKnowledgeElement linkedDec = new DecisionKnowledgeElementImpl();
        linkedDec.setProject("TEST");
        linkedDec.setId(14);
        linkedDec.setType(KnowledgeType.DECISION);

        DecisionKnowledgeElement returnDecision= aoStrategy.insertDecisionKnowledgeElement(dec,user);
        DecisionKnowledgeElement returnLinkDec = aoStrategy.insertDecisionKnowledgeElement(linkedDec, user);
        link = new LinkImpl(returnLinkDec,returnDecision);
        aoStrategy.insertLink(link, user);
    }

    @Test (expected = NullPointerException.class)
    @NonTransactional
    public void testElementNullInward(){
        aoStrategy.getElementsLinkedWithInwardLinks(null);
    }

    @Test
    @NonTransactional
    public void testElementNotInTableInward(){
        assertEquals(0, aoStrategy.getElementsLinkedWithInwardLinks(link.getSourceElement()).size(), 0.0);
    }

    @Test
    @NonTransactional
    public void testElementInTableInward(){
        assertEquals(1, aoStrategy.getElementsLinkedWithInwardLinks(link.getDestinationElement()).size(), 0.0);
    }


    @Test (expected = NullPointerException.class)
    @NonTransactional
    public void testElementNullOutward(){
        aoStrategy.getElementsLinkedWithOutwardLinks(null);
    }

    @Test
    @NonTransactional
    public void testElementNotInTableOutward(){
        assertEquals(0, aoStrategy.getElementsLinkedWithOutwardLinks(link.getDestinationElement()).size(),0.0);
    }

    @Test
    @NonTransactional
    public void testElementInTableOutward(){
        aoStrategy.insertLink(link, user);
        assertEquals(1, aoStrategy.getElementsLinkedWithOutwardLinks(link.getSourceElement()).size(), 0.0);
    }
}
