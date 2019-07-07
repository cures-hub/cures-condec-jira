package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;

public class TestJiraFilter extends TestSetUpWithIssues {

	@Before
	public void setUp() {
		initialization();
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
		String query = JiraFilter.getQueryForFilterId(-1, "TEST");
		assertEquals("project = TEST AND " + JiraFilter.MYOPENISSUES.getJqlString(), query);
	}

	@Test
	public void testGetQueryForFilter() {
		assertEquals(JiraFilter.ALLISSUES.getJqlString(), JiraFilter.getQueryForFilter("allissues", "TEST"));
		assertEquals("project = TEST AND " + JiraFilter.ALLISSUES.getJqlString(),
				JiraFilter.getQueryForFilter("-4", "TEST"));
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
