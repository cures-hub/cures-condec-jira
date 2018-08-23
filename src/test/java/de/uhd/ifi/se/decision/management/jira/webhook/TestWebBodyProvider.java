package de.uhd.ifi.se.decision.management.jira.webhook;

import com.atlassian.activeobjects.test.TestActiveObjects;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestWebBodyProvider extends TestSetUp{
    private EntityManager entityManager;

    @Before
    public void setUp() {
        TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
                new MockDefaultUserManager());
        initialization();
    }

    @Test
    public void testConstructorNullNull(){
        WebBodyProvider provider = new WebBodyProvider(null,null);
        assertEquals(0,provider.getPostMethod().getParameters().length,0.0);
    }

    @Test
    public void testConstructorNullFilled(){
        WebBodyProvider provider = new WebBodyProvider(null,"TEST-14");
        assertEquals(0,provider.getPostMethod().getParameters().length,0.0);
    }

    @Test
    public void testConstructorFilledNull(){
        WebBodyProvider provider = new WebBodyProvider("TEST",null);
        assertEquals(0,provider.getPostMethod().getParameters().length, 0.0);
    }

    @Test
    public void testFilledFilled(){
        WebBodyProvider provider = new WebBodyProvider("TEST", "TEST-14");
        assertEquals(2,provider.getPostMethod().getParameters().length, 0.0);
    }
}
