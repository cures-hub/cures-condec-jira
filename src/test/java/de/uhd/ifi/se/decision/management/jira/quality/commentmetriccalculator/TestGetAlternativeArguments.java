package de.uhd.ifi.se.decision.management.jira.quality.commentmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.java.ao.test.jdbc.NonTransactional;

public class TestGetAlternativeArguments extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testCase() {
		assertEquals(2, calculator.getAlternativeArguments().size(), 0.0);
	}
}
