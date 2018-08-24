package de.uhd.ifi.se.decision.management.jira.webhook;

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

import static org.junit.Assert.assertTrue;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestWebConnector extends TestSetUp{
    private EntityManager entityManager;

    @Before
    public void setUp() {
        TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
                new MockDefaultUserManager());
        initialization();
    }

    @Ignore
    public void testConnectionSend(){
        WebConnector connector = new WebConnector("https://cuu-staging.ase.in.tum.de/api/v1/projects/ConDecDev/integrations/conDec","03f90207-73bc-44d9-9848-d3f1f8c8254e");
        assertTrue(connector.sendWebHookTreant("CONDEC", "CONDEC-1234"));
    }

    @Test
    public void
}
