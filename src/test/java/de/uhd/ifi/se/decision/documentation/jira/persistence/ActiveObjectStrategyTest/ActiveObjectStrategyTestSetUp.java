package de.uhd.ifi.se.decision.documentation.jira.persistence.ActiveObjectStrategyTest;

import org.junit.Before;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.documentation.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.documentation.jira.persistence.ActiveObjectStrategy;
import net.java.ao.EntityManager;

public class ActiveObjectStrategyTestSetUp extends TestSetUp {

	protected EntityManager entityManager;
	protected ActiveObjectStrategy aoStrategy;

	@Before
	public void setUp() {
		ActiveObjects ao = new TestActiveObjects(entityManager);
		TestComponentGetter.init(ao, new MockTransactionTemplate(), new MockDefaultUserManager());
		initialization();
		aoStrategy = new ActiveObjectStrategy();
	}
}
