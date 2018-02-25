package ut.de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.ActiveObjectStrategy;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.IssueStrategy;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.StrategyProvider;
import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ut.TestSetUp;
import ut.de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import ut.de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;

import static org.junit.Assert.*;

/**
 * @author Tim Kuchenbuch
 */
@RunWith(ActiveObjectsJUnitRunner.class)
public class TestStrategyProvider extends TestSetUp {
    protected EntityManager entityManager;
    protected ActiveObjectStrategy aoStrategy;
    private StrategyProvider provider;

    @Before
    public void setUp(){
        ActiveObjects ao = new TestActiveObjects(entityManager);
        new ComponentGetter().init(ao,new MockTransactionTemplate(), new MockDefaultUserManager());
        initialisation();
        provider= new StrategyProvider();
    }

    @Test
    public void testProjectKeyNull(){
        assertNull(provider.getStrategy(null));
    }

    //Because of Mock no assertion
    @Test
    public void testProjectKeyNotExisting(){
        provider.getStrategy("NotTest");
    }

    @Test
    public void testProjectKeyExisting(){
        assertEquals(IssueStrategy.class,provider.getStrategy("Test").getClass());
    }
}