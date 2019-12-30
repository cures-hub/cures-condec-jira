package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestGetNumberOfDecisionKnowledgeElementsFroJiraIssuesCommon extends SetupCommonCalculator {
	@Test
	public void testTypeNull() {
		assertEquals(0, calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(null).size());
	}

	@Test
	public void testTypeFilled() {
		// TODO this should be 1
		assertEquals(0, calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.ARGUMENT).size());
	}
}
