package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
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
	public void testGetQueryFromFilterName() {
		assertEquals(JiraFilter.ALLISSUES.getJqlString(), JiraFilter.getQueryFromFilterName("allissues"));
		assertEquals(JiraFilter.ALLISSUES.getJqlString(), JiraFilter.getQueryFromFilterName("bliblablub"));
	}

	@Test
	public void testGetQueryFromFilterId() {
		String query = JiraFilter.getQueryFromFilterId(-1, "TEST");
		assertEquals("project = TEST AND " + JiraFilter.MYOPENISSUES.getJqlString(), query);
	}

	@Ignore
	@Test
	public void testGetQueryFromCustomFilter() {
		// TODO Implement this test, mock SearchRequestManager for it
		JiraFilter.getQueryFromFilterId(1, "TEST");
	}
}
