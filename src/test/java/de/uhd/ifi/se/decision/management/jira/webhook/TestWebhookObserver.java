package de.uhd.ifi.se.decision.management.jira.webhook;


import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestWebhookObserver extends TestSetUp {
    private EntityManager entityManager;
    private WebhookObserver observer;
    private DecisionKnowledgeElement element;
    @Before
    public void setUp() {
        TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
                new MockDefaultUserManager());
        initialization();

        element = new DecisionKnowledgeElementImpl();
        element.setProject("TEST");
        element.setType("TASK");
        element.setId(1);
        element.setDescription("Test description");
        element.setKey("TEST-1");
        element.setSummary("Test summary");

        observer = new WebhookObserver("TEST");
    }

    @Test
    public void testSendElementChangesNullFalse(){
        assertFalse(observer.sendElementChanges(null,false));
    }

    @Test
    public void testSendElementChangesNullTrue(){
        assertFalse(observer.sendElementChanges(null,true));
    }

    @Test
    public void testSendElementChangesFilledFalse(){
        assertTrue(observer.sendElementChanges(element,false));
    }

    @Test
    public void testSendElementChangesFilledTrue(){
        assertTrue(observer.sendElementChanges(element,true));
    }

    @Test
    public void testSendElementChangesNull(){
        assertFalse(observer.sendElementChanges(null));
    }

    @Test
    public void testSendElementChangesFilled(){
        assertTrue(observer.sendElementChanges(element));
    }
}
