package de.uhd.ifi.se.decision.management.jira.quality;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import net.java.ao.test.jdbc.NonTransactional;

public class TestGetNumberOfCommentsForJiraIssues extends SetupCommentCalculator {

	@Test
	@NonTransactional
	public void testCase() {
		Map<String, Integer> map = calculator.getNumberOfCommentsForJiraIssues();
		// TODO this should be 1
		assertEquals(0, map.size());
	}
}
