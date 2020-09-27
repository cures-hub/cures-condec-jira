package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetDecisionTableCriteriaQuery extends TestSetUp {

	protected ConfigRest configRest;
	protected HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	private String projectKey = "TEST";
	private String testQuery = "project=Test";

	@Test
	public void testGetDecisionTableCriteriaQueryWithInvalidProjectKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.getDesionTabelCriteriaQuery(null).getStatus());
	}

	@Test
	public void testSetDecisionTableCriteriaQuery() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.setDesionTabelCriteriaQuery(request, projectKey, testQuery).getStatus());
	}

	@Test
	public void testGetDecisionTableCriteriaQuery() {
		assertEquals(Status.OK.getStatusCode(), configRest.getDesionTabelCriteriaQuery(projectKey).getStatus());
	}
}
