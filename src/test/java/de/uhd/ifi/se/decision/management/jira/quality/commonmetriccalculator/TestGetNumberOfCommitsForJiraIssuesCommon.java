package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestGetNumberOfCommitsForJiraIssuesCommon extends SetupCommonCalculator {

	@Test
	public void testCase() {
		Map<String, Integer> expectedCommits = new HashMap<>();
		// TODO: setup some test commits
		//expectedCommits.put(baseIssueKey,0);
		assertEquals(expectedCommits, calculator.getNumberOfCommitsForJiraIssues());
	}
}
