package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestGetAlternativeArgumentsCommon extends SetupCommonCalculator {

	@Test
	public void testCase() {
		assertEquals(2, calculator.getAlternativesHavingArguments().size(), 0.0);
	}
}
