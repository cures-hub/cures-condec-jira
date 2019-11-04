package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestJiraFilter extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testGetFilterId() {
		assertEquals(-4, JiraFilter.ALLISSUES.getId());
	}

	@Test
	public void testValueOfFilterId() {
		assertEquals(JiraFilter.ALLISSUES, JiraFilter.valueOf(0));
		assertEquals(JiraFilter.MYOPENISSUES, JiraFilter.valueOf(-1));
	}

	@Test
	public void testGetQueryForFilterName() {
		assertEquals(JiraFilter.ALLISSUES.getJqlString(), JiraFilter.getQueryForFilterName("allissues"));
		assertEquals(JiraFilter.ALLISSUES.getJqlString(), JiraFilter.getQueryForFilterName("bliblablub"));
	}

	@Test
	public void testGetQueryForFilterId() {
		String query = JiraFilter.getQueryForFilterId(-1);
		assertEquals(JiraFilter.MYOPENISSUES.getJqlString(), query);
	}

	@Test
	public void testGetQueryForFilter() {
		assertEquals(JiraFilter.ALLISSUES.getJqlString(), JiraFilter.getQueryForFilter("allissues"));
		assertEquals(JiraFilter.ALLISSUES.getJqlString(), JiraFilter.getQueryForFilter("-4"));
	}

	@Test
	public void testContainsJiraFilter() {
		assertTrue(JiraFilter.containsJiraFilter("?filter = allissues"));
		assertFalse(JiraFilter.containsJiraFilter("?jql="));
	}

	// TODO Implement this test, mock SearchRequestManager for it
	// @Test
	// public void testGetQueryFromCustomFilter() {
	// JiraFilter.getQueryForFilterId(1, "TEST");
	// }
}
