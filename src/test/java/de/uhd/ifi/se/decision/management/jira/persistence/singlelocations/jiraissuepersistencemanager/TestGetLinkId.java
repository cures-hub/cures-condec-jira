package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;

public class TestGetLinkId extends TestJiraIssuePersistenceManagerSetUp {

	@Test
	public void testLinkFilled() {
		assertEquals(1, JiraIssuePersistenceManager.getLinkId(link));
	}

	@Test
	public void testLinkNull() {
		assertEquals(0, JiraIssuePersistenceManager.getLinkId(null));
	}

}
