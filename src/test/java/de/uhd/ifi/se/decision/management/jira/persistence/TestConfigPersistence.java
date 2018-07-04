package de.uhd.ifi.se.decision.management.jira.persistence;

import com.atlassian.activeobjects.test.TestActiveObjects;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestConfigPersistence extends TestSetUp{
    private EntityManager entityManager;
    private ConfigPersistence configPersistence;

    @Before
    public void setUp(){
        initialization();
        TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
                new MockDefaultUserManager());
        configPersistence = new ConfigPersistence();
    }

    //IsIssueStrategy
    //Because the TransactionCallbacks are hardcoded on true in the Test the Tests are only fore the Right values
    @Ignore
    public void testIsIssueStrategyInvalid(){
        assertFalse(configPersistence.isIssueStrategy("InvalidKey"));
    }

    @Test
    public void testIsIssueStrategyOk(){
        assertTrue(configPersistence.isIssueStrategy("TEST"));
    }

    //SetIssueStrategy
    @Test
    public void testSetIssueStrategyNullFalse(){
        configPersistence.setIssueStrategy(null,false);
    }

    @Test
    public void testSetIssueStrategyNullTrue(){
        configPersistence.setIssueStrategy(null,true);
    }

    @Test
    public void testSetIssueStrategyValid(){
        configPersistence.setIssueStrategy("TEST", true);
    }

    //IsActivated
    @Ignore
    public void testIsActivatedInvalid(){
        assertFalse(configPersistence.isActivated("InvalidKey"));
    }

    @Test
    public void testIsActivatedOk(){
        assertTrue(configPersistence.isActivated("TEST"));
    }

    //SetActivated
    @Test
    public void testSetActivatedNullFalse(){
        configPersistence.setActivated(null,false);
    }

    @Test
    public void testSetActivateNullTrue(){
        configPersistence.setActivated(null, true);
    }

    @Test
    public void testSetActivatedValid(){
        configPersistence.setActivated("TEST", true);
    }
}
