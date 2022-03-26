package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCleanDatabase extends TestSetUp {

	protected HttpServletRequest request;
	protected ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestNullProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.cleanDatabases(null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyValid() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.cleanDatabases(null, "TEST").getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestExistsProjectKeyExists() {
		JiraIssues.addComment(JiraIssues.getTestJiraIssues().get(2));
		JiraIssues.addCommentsToIssue(JiraIssues.getTestJiraIssues().get(0), "Commit Hash: 42\n Author: dev0");
		JiraIssues.addCommentsToIssue(JiraIssues.getTestJiraIssues().get(1),
				"{decision} We integrate jGit! {decision}");
		JiraIssues.addElementToDataBase();
		assertEquals(Status.OK.getStatusCode(), configRest.cleanDatabases(request, "TEST").getStatus());
	}

	@Test
	public void testUserUnauthorized() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.BLACK_HEAD.getApplicationUser());
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), configRest.cleanDatabases(request, "TEST").getStatus());
	}

	@Test
	public void testUserNull() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", null);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), configRest.cleanDatabases(request, "TEST").getStatus());
	}
}
