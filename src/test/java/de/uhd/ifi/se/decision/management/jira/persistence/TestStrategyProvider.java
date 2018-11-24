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
		PersistenceProvider.getPersistenceStrategy(null);
	}

	@Test
	public void testProjectKeyNonExistent() {
		assertTrue(PersistenceProvider.getPersistenceStrategy("TESTNOT") instanceof JiraIssuePersistence);
	}

	@Test
	public void testProjectKeyExistent() {
		assertTrue(PersistenceProvider.getPersistenceStrategy("TEST") instanceof JiraIssuePersistence);
	}
}