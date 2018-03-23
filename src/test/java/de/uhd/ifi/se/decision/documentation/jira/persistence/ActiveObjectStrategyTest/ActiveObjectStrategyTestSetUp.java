package de.uhd.ifi.se.decision.documentation.jira.persistence.ActiveObjectStrategyTest;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.documentation.jira.persistence.ActiveObjectStrategy;
import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;
import net.java.ao.EntityManager;

import org.junit.Before;

/**
 * @author Tim Kuchenbuch
 */
public class ActiveObjectStrategyTestSetUp extends TestSetUp{

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
