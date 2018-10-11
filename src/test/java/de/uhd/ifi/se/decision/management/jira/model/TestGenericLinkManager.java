package de.uhd.ifi.se.decision.management.jira.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;


@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUp.AoSentenceTestDatabaseUpdater.class)
public class TestGenericLinkManager extends TestSetUp {

	private EntityManager entityManager;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
	}
	
	@Test
	@NonTransactional
	public void testSimple() {
		
	}
	
	
	
	
	
	
	
	
	

}
