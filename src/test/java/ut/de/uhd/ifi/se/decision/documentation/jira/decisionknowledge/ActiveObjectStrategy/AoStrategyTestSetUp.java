package ut.de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.ActiveObjectStrategy;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.ActiveObjectStrategy;
import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;
import net.java.ao.EntityManager;
import ut.TestSetUp;
import ut.de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import ut.de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;

import org.junit.Before;

/**
 * @author Tim Kuchenbuch
 */
public class AoStrategyTestSetUp extends TestSetUp{

    protected EntityManager entityManager;
    protected ActiveObjectStrategy aoStrategy;

    @Before
    public void setUp() {
        ActiveObjects ao = new TestActiveObjects(entityManager);
        new ComponentGetter().init(ao, new MockTransactionTemplate(), new MockDefaultUserManager());
        initialisation();
        aoStrategy = new ActiveObjectStrategy();
    }
}
