package de.uhd.ifi.se.decision.management.jira.persistence;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.IssueStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.StrategyProvider;

public class TestStrategyProvider extends TestSetUp {
	private StrategyProvider strategyProvider;

	@Before
	public void setUp() {
		initialization();
		strategyProvider = new StrategyProvider();
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testProjectKeyNull() {
		strategyProvider.getStrategy(null);
	}

	@Test
	public void testProjectKeyNotExist() {
		assertTrue(strategyProvider.getStrategy("TESTNOT") instanceof IssueStrategy);
	}

	@Test
	public void testProjectKeyExists() {
		assertTrue(strategyProvider.getStrategy("TEST") instanceof IssueStrategy);
	}
}