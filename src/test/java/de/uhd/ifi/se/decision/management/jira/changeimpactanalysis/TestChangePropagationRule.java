package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestChangePropagationRule extends TestSetUp {
    
    private ChangePropagationRule rule;

    @Before
    public void setUp() {
		init();
		String propagationRuleName = "STOP_AT_SAME_ELEMENT_TYPE";
        rule = new ChangePropagationRule(propagationRuleName);
	}

    @Test
	public void testGetType() {
		assertEquals(ChangePropagationRuleType.STOP_AT_SAME_ELEMENT_TYPE, rule.getType());
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
}
