package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestJiraIssueMetric {

	@Test
	public void testToString() {
		assertEquals("count_decision_knowledge", JiraIssueMetric.COUNT_DECISION_KNOWLEDGE.toString());
	}

	@Test
	public void toDoubleEnumMap() {
		assertEquals(8, JiraIssueMetric.toEnumMap().size());
		assertEquals(1, JiraIssueMetric.toEnumMap().get(JiraIssueMetric.COUNT_DECISION_KNOWLEDGE), 0.0);
	}
}