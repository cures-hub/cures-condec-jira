package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectstrategy;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
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
public class TestUpdateDecisionKnowledgeElement extends ActiveObjectStrategyTestSetUp {

    private DecisionKnowledgeElement element;

    @Before
    public void setUp(){
        intizialisation();
        element = new DecisionKnowledgeElementImpl();
        element.setId(13);
        element.setKey("TEST-13");
        element.setType(KnowledgeType.SOLUTION);
        element.setProject("TEST");
        element.setDescription("Old");
        aoStrategy.insertDecisionKnowledgeElement(element,user);
        element.setDescription("New");
    }

    @Test (expected = NullPointerException.class)
    @NonTransactional
    public void testElementNullUserNull(){
        aoStrategy.updateDecisionKnowledgeElement(null, null);
    }

    @Test (expected = NullPointerException.class)
    @NonTransactional
    public void testElementNullUserFilled(){
        aoStrategy.updateDecisionKnowledgeElement(null, user);
    }

    @Test
    @NonTransactional
    public void testElementFilledUserNull(){
        assertTrue(aoStrategy.updateDecisionKnowledgeElement(element, null));
    }

    @Test
    @NonTransactional
    public void testElementFilledUserFilledNotInTable(){
        DecisionKnowledgeElement notInTableElement = new DecisionKnowledgeElementImpl();
        notInTableElement.setProject("TESTNOT");
        notInTableElement.setType(KnowledgeType.SOLUTION);
        notInTableElement.setKey("TESTNOT-12");
        notInTableElement.setId(12);
        assertFalse(aoStrategy.updateDecisionKnowledgeElement(notInTableElement, user));
    }

    @Test
    @NonTransactional
    public void testElementFilledUserFilledInTable(){
        assertTrue(aoStrategy.updateDecisionKnowledgeElement(element, user));
    }
}
