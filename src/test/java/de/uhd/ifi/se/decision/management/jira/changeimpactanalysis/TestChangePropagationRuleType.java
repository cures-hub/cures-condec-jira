package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestChangePropagationRuleType extends TestSetUp {

	@Test
	public void testGetPropagationRuleValidInput() {
		String propagationRuleName = "BOOST_IF_SOLUTION_OPTION";
		assertEquals(ChangePropagationRuleType.BOOST_IF_SOLUTION_OPTION,
				ChangePropagationRuleType.valueOf(propagationRuleName));
	}

	@Test
	public void testConstructorFromString() {
		ChangePropagationRule propagationRule = new ChangePropagationRule("Boost when element is a solution option");
		assertEquals(ChangePropagationRuleType.BOOST_IF_SOLUTION_OPTION,
			propagationRule.getType());
	}

	@Test
	public void testConstructorFromStringNoMatch() {
		ChangePropagationRule propagationRule = new ChangePropagationRule("FooBar");
		assertNull(propagationRule.getType());
	}

	@Test
	public void testConstructorFromJSON() {
		ChangePropagationRule propagationRule = new ChangePropagationRule("BOOST_IF_SOLUTION_OPTION", true, 1.6f);
		
		assertEquals(ChangePropagationRuleType.BOOST_IF_SOLUTION_OPTION, propagationRule.getType());
		assertTrue(propagationRule.isActive());
		assertEquals(1.6f, propagationRule.getWeightValue(), 0.0);
	}
}
