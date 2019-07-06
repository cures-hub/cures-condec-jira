package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;

public class TestJiraQueryHandler extends TestSetUpWithIssues {

	private JiraQueryHandler jiraQueryHandler;

	@Before
	public void setUp() {
		initialization();
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("NoFails");
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", false);
	}

	@Test
	public void testGetNamesOfJiraIssueTypesInQuery() {
		List<String> types = jiraQueryHandler.getNamesOfJiraIssueTypesInQuery("issuetype = (Decision, Issue)");
		assertEquals(1, types.size());
		//assertEquals("Issue", types.get(0));
	}

}
