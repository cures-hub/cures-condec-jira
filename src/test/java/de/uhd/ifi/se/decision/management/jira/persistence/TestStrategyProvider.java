package de.uhd.ifi.se.decision.management.jira.persistence;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestStrategyProvider extends TestSetUp {
	private StrategyProvider strategyProvider;

	@Before
	public void setUp() {
		initialization();
		strategyProvider = new StrategyProvider();
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testProjectKeyNull() {
		strategyProvider.getPersistenceStrategy(null);
	}

	@Test
	public void testProjectKeyNotExist() {
		assertTrue(strategyProvider.getPersistenceStrategy("TESTNOT") instanceof IssueStrategy);
	}

	@Test
	public void testProjectKeyExists() {
		assertTrue(strategyProvider.getPersistenceStrategy("TEST") instanceof IssueStrategy);
	}
}