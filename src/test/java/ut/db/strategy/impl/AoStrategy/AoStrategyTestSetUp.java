package ut.db.strategy.impl.AoStrategy;

import com.atlassian.DecisionDocumentation.db.strategy.impl.AoStrategy;
import com.atlassian.DecisionDocumentation.util.ComponentGetter;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.runner.RunWith;
import ut.mocks.MockDefaultUserManager;
import ut.mocks.MockTransactionTemplate;
import ut.testsetup.TestSetUp;


public class AoStrategyTestSetUp extends TestSetUp{

    protected EntityManager entityManager;
    protected AoStrategy aoStrategy;

    @Before
    public void setUp() {
        initialisation();
        ActiveObjects ao = new TestActiveObjects(entityManager);
        new ComponentGetter().init(ao, new MockTransactionTemplate(), new MockDefaultUserManager());
        aoStrategy = new AoStrategy();
    }
}
