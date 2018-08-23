package de.uhd.ifi.se.decision.management.jira.webhook;

import com.atlassian.activeobjects.test.TestActiveObjects;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestWebBodyProvider extends TestSetUp{
    private EntityManager entityManager;
    private Treant treant;

    @Before
    public void setUp() {
        TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
                new MockDefaultUserManager());
        initialization();
    }

    @Test
    public void testOutput(){
        WebBodyProvider provider = new WebBodyProvider("TEST", "TEST-14");
        System.out.println(provider.getJsonString());
    }
}
