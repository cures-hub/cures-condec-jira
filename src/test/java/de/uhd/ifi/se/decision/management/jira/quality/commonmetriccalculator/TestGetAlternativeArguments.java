package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.java.ao.test.jdbc.NonTransactional;

public class TestGetAlternativeArguments extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testCase() {
		assertEquals(2, calculator.getAlternativesHavingArguments().size(), 0.0);
	}
}
