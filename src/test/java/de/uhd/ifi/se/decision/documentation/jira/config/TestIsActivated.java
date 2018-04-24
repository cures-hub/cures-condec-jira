package de.uhd.ifi.se.decision.documentation.jira.config;

import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TestIsActivated {
	private ActivationCondition activationCondition;

	@Before
	public void setUp() {
		Map<String, String> context = new HashMap<>();
		activationCondition = new ActivationCondition();
		activationCondition.init(context);
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

	// TODO Refactor
	@Test
	public void testShouldDisplayFilled() {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("projectKey", "TEST");
		//assertTrue(activationCondition.shouldDisplay(context));
	}
}