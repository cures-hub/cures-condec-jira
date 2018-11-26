package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectpersistencemanager;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(ActiveObjectStrategyTestSetUp.AoSentenceTestDatabaseUpdater.class)
public class TestGetDecisionKnowledgeElementId extends ActiveObjectStrategyTestSetUp {

    private DecisionKnowledgeElement element;

    @Before
    public void setUp(){
        initialisation();
        DecisionKnowledgeElement insertElement = new DecisionKnowledgeElementImpl();
        insertElement.setId(13);
        insertElement.setProject("TEST");
        insertElement.setType(KnowledgeType.DECISION);
        element = aoStrategy.insertDecisionKnowledgeElement(insertElement, user);
    }

    @Test (expected = NullPointerException.class)
    @NonTransactional
    public void testIdNull(){
        aoStrategy.getDecisionKnowledgeElement(null);
    }

    @Test
    @NonTransactional
    public void testIdNotInTable(){
        assertNull(aoStrategy.getDecisionKnowledgeElement(123132));
    }

    @Test
    @NonTransactional
    public void testIdInTable(){
        assertNotNull(aoStrategy.getDecisionKnowledgeElement(element.getId()));
    }
}
