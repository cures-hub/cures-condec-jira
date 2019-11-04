package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestGetUnlinkedElements extends TestJiraIssuePersistenceManagerSetUp {

	@Test
	public void testIdCannotBeFound() {
		assertEquals(numberOfElements, issueStrategy.getUnlinkedElements(0).size());
	}

	@Test
	public void testIdCanBeFound() {
		assertEquals(numberOfElements-2, issueStrategy.getUnlinkedElements(2).size());
	}
}
