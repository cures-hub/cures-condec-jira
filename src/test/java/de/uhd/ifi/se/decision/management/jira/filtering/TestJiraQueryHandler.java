package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;

public class TestJiraQueryHandler extends TestSetUpWithIssues {

	private JiraQueryHandler jiraQueryHandler;
	private ApplicationUser user;

	@Before
	public void setUp() {
		initialization();
		user = ComponentAccessor.getUserManager().getUserByName("NoFails");
	}

	@Test
	public void testConstructorQueryNull() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", null);
		assertEquals(JiraQueryType.OTHER, jiraQueryHandler.getQueryType());
		assertEquals("type = null", jiraQueryHandler.getQuery());
	}

	@Test
	public void testConstructorQueryEmpty() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", "");
		assertEquals(JiraQueryType.OTHER, jiraQueryHandler.getQueryType());
		assertEquals("type = null", jiraQueryHandler.getQuery());
	}

	@Test
	public void testGetJiraIssuesFromEmptyQuery() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", "");
		assertEquals(0, jiraQueryHandler.getJiraIssuesFromQuery().size());
	}

	@Test
	public void testGetNamesOfJiraIssueTypesInQueryOneType() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", "?jql=project=TEST AND issuetype = Issue AND resolution = Unresolved");
		assertEquals(JiraQueryType.JQL, jiraQueryHandler.getQueryType());
		assertEquals("project=TEST AND issuetype = Issue AND resolution = Unresolved", jiraQueryHandler.getQuery());
		List<String> types = jiraQueryHandler.getNamesOfJiraIssueTypesInQuery();
		assertEquals(1, types.size());
		assertEquals("Issue", types.get(0));
	}

	@Test
	public void testGetNamesOfJiraIssueTypesInQueryZeroTypes() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", "?jql=project=TEST");
		List<String> types = jiraQueryHandler.getNamesOfJiraIssueTypesInQuery();
		assertEquals(0, types.size());
	}

	@Test
	public void testGetNamesOfJiraIssueTypesInQueryThreeTypes() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST",
				"abc§jql=project=TEST AND issuetype in (Decision, Issue, Alternative) AND resolution = Unresolved");
		assertEquals("project=TEST AND issuetype in (Decision, Issue, Alternative) AND resolution = Unresolved", jiraQueryHandler.getQuery());
		List<String> types = jiraQueryHandler.getNamesOfJiraIssueTypesInQuery();
		assertEquals("Decision", types.get(0));
		assertEquals("Issue", types.get(1));
		assertEquals("Alternative", types.get(2));
		assertEquals(3, types.size());
	}

	@Test
	public void testGetDatesNoDatesInQuery() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", "");
		assertEquals(-1, jiraQueryHandler.getCreatedEarliest());
		assertEquals(-1, jiraQueryHandler.getCreatedLatest());
	}

	@Test
	public void testGetDatesInQuery() {
		assertTrue("1970-01-01".matches("\\d\\d\\d\\d-\\d\\d-\\d\\d(.)*"));
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", "?jql=created >= 1970-01-02 AND created <= 1970-01-03");
		assertEquals("created >= 1970-01-02 AND created <= 1970-01-03", jiraQueryHandler.getQuery());
		assertEquals("1970-01-02 AND created <= 1970-01-03", jiraQueryHandler.getQuerySubstringWithTimeInformation());
		assertTrue(jiraQueryHandler.getCreatedEarliest() > 0);
		assertTrue(jiraQueryHandler.getCreatedLatest() > 0);
	}

	@Test
	public void testGetDatesInQueryFromTimeFactor() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", "?jql=created >= -1m AND created <= -1w");
		assertEquals("-1m AND created <= -1w", jiraQueryHandler.getQuerySubstringWithTimeInformation());
		assertTrue(jiraQueryHandler.getCreatedEarliest() > 100000000);
		assertTrue(jiraQueryHandler.getCreatedLatest() > 100000000);

		jiraQueryHandler = new JiraQueryHandler(user, "TEST", "?jql=created >= -1d AND created <= -1h");
		assertTrue(jiraQueryHandler.getCreatedEarliest() > 100000000);
		assertTrue(jiraQueryHandler.getCreatedLatest() > 100000000);
	}

	@Test
	public void testGetQueryObject() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", "?jql=project");
		assertEquals("project", jiraQueryHandler.getQueryObject().getQueryString());
	}

}
