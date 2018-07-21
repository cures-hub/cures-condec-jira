package de.uhd.ifi.se.decision.management.jira.persistence.issuestrategy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGetUnlinkedDecisionComponents extends TestIssueStrategySetUp {

	//TODO
//	@Test
//	public void tesIdNullKeyNull() {
//		assertEquals(0, issueStrategy.getUnlinkedDecisionComponents((long) 0, null).size());
//	}

	@Test
	public void testIdNullKeyFilled() {
		assertEquals(0, issueStrategy.getUnlinkedElements((long) 0).size());
	}

//	@Test
//	public void testIdFilledKeyFilled() {
//		assertEquals(11, issueStrategy.getUnlinkedDecisionComponents((long) 15).size());
//	}
}
