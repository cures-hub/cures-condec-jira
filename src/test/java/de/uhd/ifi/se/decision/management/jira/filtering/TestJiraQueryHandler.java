package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestJiraQueryHandler extends TestSetUp {

	private JiraQueryHandler jiraQueryHandler;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	public void testConstructorQueryNull() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", null);
		assertEquals(JiraQueryType.OTHER, jiraQueryHandler.getQueryType());
		assertEquals("?jql=resolution = Unresolved AND project = TEST", jiraQueryHandler.getQuery());
	}

	@Test
	public void testConstructorQueryEmpty() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", "");
		assertEquals(JiraQueryType.OTHER, jiraQueryHandler.getQueryType());
		assertEquals("?jql=resolution = Unresolved AND project = TEST", jiraQueryHandler.getQuery());
	}

	@Test
	public void testGetJiraIssuesFromEmptyQuery() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", "");
		assertTrue(jiraQueryHandler.getJiraIssuesFromQuery().size() > 0);
	}

	@Test
	public void testGetJiraIssuesFromFilledQuery() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", "?jql=project=TEST");
		List<Issue> jiraIssues = jiraQueryHandler.getJiraIssuesFromQuery();
		assertEquals(10, jiraIssues.size());
	}

	@Test
	public void testGetJiraIssuesFromFilledQueryNonExistingProject() {
		jiraQueryHandler = new JiraQueryHandler(user, "", "?jql=project=UNKNOWNPROJECT");
		assertEquals(0, jiraQueryHandler.getJiraIssuesFromQuery().size());
	}

	@Test
	public void testGetNamesOfJiraIssueTypesInQueryOneType() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST",
				"?jql=project=TEST AND issuetype = Issue AND resolution = Unresolved");
		assertEquals(JiraQueryType.JQL, jiraQueryHandler.getQueryType());
		assertEquals("?jql=project=TEST AND issuetype = Issue AND resolution = Unresolved",
				jiraQueryHandler.getQuery());
		Set<String> types = jiraQueryHandler.getNamesOfJiraIssueTypesInQuery();
		assertEquals(1, types.size());
		assertEquals("Issue", types.iterator().next());
	}

	@Test
	public void testGetNamesOfJiraIssueTypesInQueryZeroTypes() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", "?jql=project=TEST");
		Set<String> types = jiraQueryHandler.getNamesOfJiraIssueTypesInQuery();
		assertEquals(0, types.size());
	}

	@Test
	public void testGetNamesOfJiraIssueTypesInQueryThreeTypes() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST",
				"abc§jql=project=TEST AND issuetype in (Decision, Issue, Alternative) AND resolution = Unresolved");
		assertEquals("?jql=project=TEST AND issuetype in (Decision, Issue, Alternative) AND resolution = Unresolved",
				jiraQueryHandler.getQuery());
		Set<String> types = jiraQueryHandler.getNamesOfJiraIssueTypesInQuery();
		Iterator<String> iterator = types.iterator();
		assertEquals("Decision", iterator.next());
		assertEquals("Issue", iterator.next());
		assertEquals("Alternative", iterator.next());
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
		jiraQueryHandler = new JiraQueryHandler(user, "TEST",
				"?jql=resolution = Unresolved AND created >= 1970-01-02 AND created <= 1970-01-03");
		assertEquals(
				"?jql=resolution = Unresolved AND created >= 1970-01-02 AND created <= 1970-01-03 AND project = TEST",
				jiraQueryHandler.getQuery());
		assertTrue(jiraQueryHandler.getCreatedEarliest() > 0);
		assertTrue(jiraQueryHandler.getCreatedLatest() > 0);
	}

	@Test
	public void testGetDatesInQueryFromTimeFactor() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST",
				"?jql=project = TEST AND created >= -1m AND created <= -1w");
		assertTrue(jiraQueryHandler.getCreatedEarliest() > 100000000);
		assertTrue(jiraQueryHandler.getCreatedLatest() > 100000000);

		jiraQueryHandler = new JiraQueryHandler(user, "TEST",
				"?jql=project = TEST AND created >= -1d AND created <= -1h");
		assertTrue(jiraQueryHandler.getCreatedEarliest() > 100000000);
		assertTrue(jiraQueryHandler.getCreatedLatest() > 100000000);
	}

	@Test
	public void testGetQueryObject() {
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", "?jql=project=TEST");
		assertEquals("project=TEST", jiraQueryHandler.getQueryObject().getQueryString());
	}

}
