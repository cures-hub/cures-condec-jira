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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestWebConnector extends TestSetUp{
    private EntityManager entityManager;
    private WebConnector connectorHook;

    @Before
    public void setUp() {
        TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
                new MockDefaultUserManager());
        initialization();
        connectorHook = new WebConnector("https://cuu-staging.ase.in.tum.de/api/v1/projects/ConDecDev/integrations/conDec","03f90207-73bc-44d9-9848-d3f1f8c8254e");
    }

    //Can be used to try if the connection is working like intended
    @Ignore
    public void testConnectionSend(){
        WebConnector connector = new WebConnector("https://cuu-staging.ase.in.tum.de/api/v1/projects/ConDecDev/integrations/conDec","03f90207-73bc-44d9-9848-d3f1f8c8254e");
        assertTrue(connector.sendWebHookTreant("CONDEC", "CONDEC-1234"));
    }

    @Test
    public void testConstNullNull(){
        WebConnector connector = new WebConnector(null, null);
        assertEquals("", connector.getUrl());
        assertEquals("", connector.getSecret());
    }

    @Test
    public void  testConstNullFilled(){
        WebConnector connector = new WebConnector(null, "test");
        assertEquals("", connector.getUrl());
        assertEquals("test", connector.getSecret());
    }

    @Test
    public void testConstFilledNull(){
        WebConnector connector = new WebConnector("test", null);
        assertEquals("test", connector.getUrl());
        assertEquals("", connector.getSecret());
    }

    @Test
    public void testConstFilledFilled(){
        WebConnector connector = new WebConnector("test", "test");
        assertEquals("test", connector.getUrl());
        assertEquals("test", connector.getSecret());
    }

    @Test
    public void testConstNull(){
        WebConnector connector = new WebConnector(null);
        assertEquals("", connector.getUrl());
        assertEquals("", connector.getSecret());
    }

    @Test
    public void testConstFilledWrong(){
        WebConnector connector = new WebConnector("NoTest");
        assertEquals("true", connector.getUrl());
        assertEquals("true", connector.getSecret());
    }

    @Test
    public void testConstFilled(){
        WebConnector connector = new WebConnector("TEST");
        assertEquals("true", connector.getUrl());
        assertEquals("true", connector.getSecret());
    }

    @Test
    public void testsendWebhookNullNull(){
        assertFalse(connectorHook.sendWebHookTreant(null, null));
    }

    @Test
    public void testsendWebhookNullEmpty(){
        assertFalse(connectorHook.sendWebHookTreant(null, ""));
    }

    @Test
    public void testsendWebhookEmptyNull(){
        assertFalse(connectorHook.sendWebHookTreant("", null));
    }

    @Test
    public void testsendWebhookEmptyEmpty(){
        assertFalse(connectorHook.sendWebHookTreant("", ""));
    }

    @Test
    public void testsendWebhookNullFilled(){
        assertFalse(connectorHook.sendWebHookTreant(null, "TEST-12"));
    }

    @Test
    public void testsendWebhookEmptyFilled(){
        assertFalse(connectorHook.sendWebHookTreant("", "TEST-12"));
    }

    @Test
    public void testsendWebhookFilledNull(){
        assertFalse(connectorHook.sendWebHookTreant("TEST", null));
    }

    @Test
    public void testsendWebhookFilledEmpty(){
        assertFalse(connectorHook.sendWebHookTreant("TEST", ""));
    }
}
