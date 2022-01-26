package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestChangePropagationRuleType extends TestSetUp {

	@Test
	public void testGetPropagationRuleValidInput() {
		String propagationRuleName = "STOP_AT_SAME_ELEMENT_TYPE";
		assertEquals(ChangePropagationRuleType.STOP_AT_SAME_ELEMENT_TYPE,
				ChangePropagationRuleType.valueOf(propagationRuleName));
	}

	@Test
	public void testConstructorFromString() {
		ChangePropagationRule propagationRule = new ChangePropagationRule("Stop at elements with the same type as the selected element");
		assertEquals(ChangePropagationRuleType.STOP_AT_SAME_ELEMENT_TYPE,
			propagationRule.getType());
	}

	@Test
	public void testConstructorFromJSON() {
		ChangePropagationRule propagationRule = new ChangePropagationRule("STOP_AT_SAME_ELEMENT_TYPE", true, 1.6f);
		
		assertEquals(ChangePropagationRuleType.STOP_AT_SAME_ELEMENT_TYPE, propagationRule.getType());
		assertTrue(propagationRule.isActive());
		assertEquals(1.6f, propagationRule.getWeightValue(), 0.0);
	}
}
