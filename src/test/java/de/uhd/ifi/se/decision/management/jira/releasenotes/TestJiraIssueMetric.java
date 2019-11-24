package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestJiraIssueMetric {

	@Test
	public void testToString() {
		assertEquals("count_decision_knowledge", JiraIssueMetric.COUNT_DECISION_KNOWLEDGE.toString());
	}

	@Test
	public void getJiraIssueMetric() {
		assertEquals(JiraIssueMetric.COUNT_DECISION_KNOWLEDGE,
				JiraIssueMetric.getJiraIssueMetric("count_decision_knowledge"));
		assertEquals(JiraIssueMetric.COUNT_DECISION_KNOWLEDGE, JiraIssueMetric.getJiraIssueMetric(null));
	}

	@Test
	public void toIntegerEnumMap() {
		assertEquals(8, JiraIssueMetric.toIntegerEnumMap().size());
		assertEquals(0, JiraIssueMetric.toIntegerEnumMap().get(JiraIssueMetric.COUNT_DECISION_KNOWLEDGE), 0.0);
	}

	@Test
	public void toDoubleEnumMap() {
		assertEquals(8, JiraIssueMetric.toDoubleEnumMap().size());
		assertEquals(1, JiraIssueMetric.toDoubleEnumMap().get(JiraIssueMetric.COUNT_DECISION_KNOWLEDGE), 0.0);
	}

	@Test
	public void toList() {
		assertEquals(8, JiraIssueMetric.toIntegerEnumMap().size());
	}

	@Test
	public void getOriginalList() {
		assertEquals(8, JiraIssueMetric.toIntegerEnumMap().size());
	}
}