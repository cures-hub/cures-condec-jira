package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UndefinedPassRuleTest {

	@Test
	public void passTest() {
		assertEquals("undefined", PassRule.UNDEFINED.getTranslation());
		assertEquals("undefined.descr", PassRule.UNDEFINED.getDescription());
		assertEquals(true, PassRule.UNDEFINED.getPredicate().pass(null, 0.0, null, 0.0, null));
		assertEquals(PassRule.UNDEFINED, PassRule.getPropagationRule(""));
		assertEquals(PassRule.UNDEFINED, PassRule.getPropagationRule(null));
		assertEquals(PassRule.UNDEFINED, PassRule.getPropagationRule("Test"));
	}

}
