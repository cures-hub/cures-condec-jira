package de.uhd.ifi.se.decision.management.jira.quality.commentmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;

public class TestIssueWithNoExistingLinksToDecisionKnowledge extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testTypeNull() {
		assertEquals("", calculator.issuesWithNoExistingLinksToDecisionKnowledge(null));
	}

	@Test
	@NonTransactional
	public void testTypeNoElements() {
		assertEquals(" ", calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.DECISION));
	}

	@Test
	@NonTransactional
	public void testTypeFilled() {
		assertEquals(" TEST-1 ", calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.ARGUMENT));
	}
}
