package de.uhd.ifi.se.decision.management.jira.releasenotes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestIssueMetric {

	@Test
	public void testToString() {
		assertEquals("count_decision_knowledge", IssueMetric.COUNT_DECISION_KNOWLEDGE.toString());
	}

	@Test
	public void getissueMetric() {
		assertEquals(IssueMetric.COUNT_DECISION_KNOWLEDGE, IssueMetric.getIssueMetric("count_decision_knowledge"));
		assertEquals(IssueMetric.COUNT_DECISION_KNOWLEDGE, IssueMetric.getIssueMetric(null));
	}

	@Test
	public void toIntegerEnumMap() {
		assertEquals(8, IssueMetric.toIntegerEnumMap().size(), 0.0);
		assertEquals(0, IssueMetric.toIntegerEnumMap().get(IssueMetric.COUNT_DECISION_KNOWLEDGE), 0.0);

	}

	@Test
	public void toDoubleEnumMap() {
		assertEquals(8, IssueMetric.toDoubleEnumMap().size(), 0.0);
		assertEquals(1.0, IssueMetric.toDoubleEnumMap().get(IssueMetric.COUNT_DECISION_KNOWLEDGE), 0.0);
	}

	@Test
	public void toList() {
		assertEquals(8, IssueMetric.toIntegerEnumMap().size(), 0.0);
	}

	@Test
	public void getOriginalList() {
		assertEquals(8, IssueMetric.toIntegerEnumMap().size(), 0.0);
	}
}