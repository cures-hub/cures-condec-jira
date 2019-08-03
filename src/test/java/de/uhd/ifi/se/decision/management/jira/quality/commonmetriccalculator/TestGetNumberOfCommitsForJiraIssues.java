package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.java.ao.test.jdbc.NonTransactional;

public class TestGetNumberOfCommitsForJiraIssues extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testCase() {
		Map<String, Integer> expectedCommits = new HashMap<>();
		// TODO: setup some test commits
		//expectedCommits.put(baseIssueKey,0);
		assertEquals(expectedCommits, calculator.getNumberOfCommitsForJiraIssues());
	}
}
