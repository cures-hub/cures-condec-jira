package ut.db.strategy.impl.AoStrategy;

import com.atlassian.DecisionDocumentation.db.strategy.impl.AoStrategy;
import org.junit.Before;
import ut.testsetup.TestSetUp;

public class TestAoStrategySetUp extends TestSetUp{
    protected AoStrategy aoStrategy;

    @Before
    public void setUp(){
        initialisation();
        aoStrategy = new AoStrategy();
    }
}
