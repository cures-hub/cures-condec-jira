package de.uhd.ifi.se.decision.management.jira.quality;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestKnowledgeElementCheck {

	@Test
	public void testCoverageExplanation() {
		assertEquals("Only 2 decisions are reached.", KnowledgeElementCheck.createCoverageExplanation(2));
		assertEquals("Only 1 decision is reached.", KnowledgeElementCheck.createCoverageExplanation(1));
	}

}