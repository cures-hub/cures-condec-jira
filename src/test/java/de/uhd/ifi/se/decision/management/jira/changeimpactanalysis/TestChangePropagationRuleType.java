package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestChangePropagationRuleType extends TestSetUp {

	@Test
	public void testGetPropagationRuleValidInput() {
		String propagationRuleName = "STOP_AT_SAME_ELEMENT_TYPE";
		assertEquals(ChangePropagationRuleType.STOP_AT_SAME_ELEMENT_TYPE,
				ChangePropagationRuleType.valueOf(propagationRuleName));
	}
}
