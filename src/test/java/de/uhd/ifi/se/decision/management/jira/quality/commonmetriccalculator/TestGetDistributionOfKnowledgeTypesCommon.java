package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestGetDistributionOfKnowledgeTypesCommon extends SetupCommonCalculator {

	@Test
	public void testCase() {
		assertEquals(4, calculator.getDistributionOfKnowledgeTypes().size());
	}
}
