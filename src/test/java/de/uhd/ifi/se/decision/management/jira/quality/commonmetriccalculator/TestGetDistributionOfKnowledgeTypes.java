package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.java.ao.test.jdbc.NonTransactional;

public class TestGetDistributionOfKnowledgeTypes extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testCase() {
		assertEquals(4, calculator.getDistributionOfKnowledgeTypes().size(), 0.0);
	}
}
