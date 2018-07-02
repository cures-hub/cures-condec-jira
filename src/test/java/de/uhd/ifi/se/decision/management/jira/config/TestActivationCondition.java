package de.uhd.ifi.se.decision.management.jira.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestActivationCondition {
	private EntityManager entityManager;
	private ActivationCondition activationCondition;

	@Before
	public void setUp() {
		Map<String, String> context = new ConcurrentHashMap<>();
		activationCondition = new ActivationCondition();
		activationCondition.init(context);
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
	}

	@Test
	public void testShouldDisplayNull() {
		assertFalse(activationCondition.shouldDisplay(null));
	}

	@Test
	public void testShouldDisplayEmpty() {
		Map<String, Object> context = new ConcurrentHashMap<>();
		assertFalse(activationCondition.shouldDisplay(context));
	}

	@Test
	public void testShouldDisplayFilled() {
		Map<String, Object> context = new ConcurrentHashMap<String, Object>();
		context.put("projectKey", "TEST");
		assertTrue(activationCondition.shouldDisplay(context));
	}
}