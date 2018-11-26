package de.uhd.ifi.se.decision.management.jira.persistence;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;

public class TestAbstractPersistenceManager extends TestSetUpWithIssues {

	@Before
	public void setUp() {
		initialization();
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testProjectKeyNull() {
		AbstractPersistenceManager.getPersistenceStrategy(null);
	}

	@Test
	public void testProjectKeyNonExistent() {
		assertTrue(AbstractPersistenceManager.getPersistenceStrategy("TESTNOT") instanceof JiraIssuePersistenceManager);
	}

	@Test
	public void testProjectKeyExistent() {
		assertTrue(AbstractPersistenceManager.getPersistenceStrategy("TEST") instanceof JiraIssuePersistenceManager);
	}
}