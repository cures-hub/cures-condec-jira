package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestJiraQueryType {
	
	@Test
	public void testGetQueryForFilterName() {
		assertEquals(JiraQueryType.JQL, JiraQueryType.getJiraQueryType("?jql="));
		assertEquals(JiraQueryType.FILTER, JiraQueryType.getJiraQueryType("?filter="));
		assertEquals(JiraQueryType.FILTER, JiraQueryType.getJiraQueryType("?filter=allissues&jql"));
	}

}
