package de.uhd.ifi.se.decision.documentation.jira.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.documentation.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestActivationCondition {
	private EntityManager entityManager;
	private ActivationCondition activationCondition;

	@Before
	public void setUp() {
		Map<String, String> context = new HashMap<>();
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
		Map<String, Object> context = new HashMap<>();
		assertFalse(activationCondition.shouldDisplay(context));
	}

	@Test
	public void testShouldDisplayFilled() {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("projectKey", "TEST");
		assertTrue(activationCondition.shouldDisplay(context));
	}
}