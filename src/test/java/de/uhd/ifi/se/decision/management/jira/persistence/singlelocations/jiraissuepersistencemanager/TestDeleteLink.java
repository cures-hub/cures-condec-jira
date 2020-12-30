package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteLink extends TestJiraIssuePersistenceManagerSetUp {

	@Test
	public void testLinkNullUserNull() {
		assertFalse(JiraIssuePersistenceManager.deleteLink(null, null));
	}

	@Test
	public void testLinkNullUserFilled() {
		assertFalse(JiraIssuePersistenceManager.deleteLink(null, user));
	}

	@Test
	public void testLinkFilledUserNull() {
		assertFalse(JiraIssuePersistenceManager.deleteLink(link, null));
	}

	@Test
	@NonTransactional
	public void testLinkFilledUserFilled() {
		assertTrue(JiraIssuePersistenceManager.deleteLink(link, user));
	}
}
