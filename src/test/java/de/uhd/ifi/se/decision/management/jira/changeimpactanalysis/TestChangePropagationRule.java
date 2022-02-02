package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestChangePropagationRule extends TestSetUp {

	private ChangePropagationRule rule;

	@Before
	public void setUp() {
		init();
		String propagationRuleName = "Boost when element has the same creator as the selected element";
		rule = new ChangePropagationRule(propagationRuleName);
	}

	@Test
	public void testGetType() {
		assertEquals(ChangePropagationRuleType.BOOST_WHEN_EQUAL_CREATOR, rule.getType());
	}

	@Test
	public void testWeightValue() {
		rule.setWeightValue(1.0f);
		assertEquals(1.0f, rule.getWeightValue(), 0.00);
	}

	@Test
	public void testIsActive() {
		rule.setActive(true);
		assertTrue(rule.isActive());
	}

	@Test
	public void testGetDescription() {
		assertEquals("Boost when element has the same creator as the selected element", rule.getDescription());
	}

	@Test
	public void testGetDefaultRules() {
		assertEquals(12, ChangePropagationRule.getDefaultRules().size());
	}

	@Test
	public void testEquals() {
		assertFalse(new ChangePropagationRule(ChangePropagationRuleType.BOOST_WHEN_EQUAL_DECISION_GROUP).equals(rule));
		assertTrue(new ChangePropagationRule(ChangePropagationRuleType.BOOST_WHEN_EQUAL_CREATOR).equals(rule));
	}
}
