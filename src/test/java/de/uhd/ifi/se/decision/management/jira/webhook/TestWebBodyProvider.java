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
import static org.junit.Assert.assertNull;

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
        assertNull(provider.getJsonString());
    }

    @Test
    public void testConstructorNullFilled(){
        WebBodyProvider provider = new WebBodyProvider(null,"TEST-14");
        assertNull(provider.getJsonString());
    }

    @Test
    public void testConstructorFilledNull(){
        WebBodyProvider provider = new WebBodyProvider("TEST",null);
        String expection = "{\"commit\":{\"hash\":\"true\"},\"ConDeTree\":{\"nodeStructure\":{\"children\":[],\"text\":{\"name\":\"Assessment\",\"title\":\"Test\",\"desc\":\"TEST-14\"}},\"chart\":{\"container\":\"#treant-container\",\"node\":{\"collapsable\":\"true\"},\"connectors\":{\"type\":\"straight\"},\"rootOrientation\":\"NORTH\",\"siblingSeparation\":30,\"levelSeparation\":30,\"subTreeSeparation\":30}}}\n";
        assertEquals(expection,provider.getJsonString());
    }

    @Test
    public void testFilledFilled(){
        WebBodyProvider provider = new WebBodyProvider("TEST", "TEST-14");
        System.out.println(provider.getJsonString());
    }
}
