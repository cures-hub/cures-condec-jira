package de.uhd.ifi.se.decision.management.jira.persistence;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;

public class TestStrategyProvider extends TestSetUpWithIssues {

	@Before
	public void setUp() {
		initialization();
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testProjectKeyNull() {
		StrategyProvider.getPersistenceStrategy(null);
	}

	@Test
	public void testProjectKeyNonExistent() {
		assertTrue(StrategyProvider.getPersistenceStrategy("TESTNOT") instanceof IssueStrategy);
	}

	@Test
	public void testProjectKeyExistent() {
		assertTrue(StrategyProvider.getPersistenceStrategy("TEST") instanceof IssueStrategy);
	}
}