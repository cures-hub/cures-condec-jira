package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestJiraIssueMetric {

	@Test
	public void testToString() {
		assertEquals("decision_knowledge_count", JiraIssueMetric.DECISION_KNOWLEDGE_COUNT.toString());
	}

	@Test
	public void toEnumMap() {
		assertEquals(8, JiraIssueMetric.toEnumMap().size());
		assertEquals(JiraIssueMetric.DECISION_KNOWLEDGE_COUNT.getDefaultWeight(),
				JiraIssueMetric.toEnumMap().get(JiraIssueMetric.DECISION_KNOWLEDGE_COUNT), 0.0);
	}
}