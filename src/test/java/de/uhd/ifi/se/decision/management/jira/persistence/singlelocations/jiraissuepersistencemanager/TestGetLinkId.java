package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestGetLinkId extends TestSetUp {

	private Link link;

	@Before
	public void setUp() {
		init();
		link = Links.getTestLink();
	}

	@Test
	public void testLinkFilled() {
		assertEquals(1, JiraIssuePersistenceManager.getLinkId(link));
	}

	@Test
	public void testLinkNull() {
		assertEquals(0, JiraIssuePersistenceManager.getLinkId(null));
	}

}
