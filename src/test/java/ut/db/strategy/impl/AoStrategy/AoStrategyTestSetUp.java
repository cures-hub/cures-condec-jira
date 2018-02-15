package ut.db.strategy.impl.AoStrategy;

import com.atlassian.DecisionDocumentation.db.strategy.impl.AoStrategy;
import com.atlassian.DecisionDocumentation.util.ComponentGetter;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import net.java.ao.EntityManager;
import org.junit.Before;
import ut.mocks.MockDefaultUserManager;
import ut.mocks.MockTransactionTemplate;
import ut.testsetup.TestSetUp;

/**
 * @author Tim Kuchenbuch
 */
public class AoStrategyTestSetUp extends TestSetUp{

    protected EntityManager entityManager;
    protected AoStrategy aoStrategy;

    @Before
    public void setUp() {
        ActiveObjects ao = new TestActiveObjects(entityManager);
        new ComponentGetter().init(ao, new MockTransactionTemplate(), new MockDefaultUserManager());
        initialisation();
        aoStrategy = new AoStrategy();
    }
}
