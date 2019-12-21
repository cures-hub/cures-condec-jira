package de.uhd.ifi.se.decision.management.jira.quality;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.java.ao.test.jdbc.NonTransactional;

public class TestGetNumberOfRelevantSentencesComment extends SetupCommentCalculator {

	@Test
	@NonTransactional
	public void testCase() {
		assertEquals(2, calculator.getNumberOfRelevantSentences().size(), 0.0);
	}
}
