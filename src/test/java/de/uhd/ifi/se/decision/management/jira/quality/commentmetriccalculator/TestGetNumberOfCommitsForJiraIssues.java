package de.uhd.ifi.se.decision.management.jira.quality.commentmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.java.ao.test.jdbc.NonTransactional;

public class TestGetNumberOfCommitsForJiraIssues extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testCase() {
		// TODO this should be 1
		assertEquals(0, calculator.getNumberOfCommitsForJiraIssues().size(), 0.0);
	}
}
