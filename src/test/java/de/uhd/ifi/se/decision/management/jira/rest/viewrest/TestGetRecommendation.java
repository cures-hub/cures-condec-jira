package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;

public class TestGetRecommendation extends TestSetUp {

	private ViewRest viewRest;
	private HttpServletRequest request;
	private static final String projectKey = "TEST";
	private static final String validKeyword = "keyword";
	private static final String invalidKeyword = "";

	@Before
	public void setUp() {
		init();
		viewRest = new ViewRest();
		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);

	}

	@Test
	public void testGetRecommendation() {
		assertEquals(Status.OK.getStatusCode(), viewRest.getRecommendation(request, "TEST", validKeyword).getStatus());
	}

	@Test
	public void testGetRecommendationEmptyKeyword() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendation(request, "TEST", invalidKeyword).getStatus());
	}

	@Test
	public void testGetRecommendationEmptyProject() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendation(request, "", validKeyword).getStatus());
	}

	@Test
	public void testGetRecommendationProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendation(request, null, validKeyword).getStatus());
	}

	@Test
	public void testGetRecommendationRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendation(null, projectKey, validKeyword).getStatus());
	}

	@Test
	public void testGetRecommendationNoKnowledgeSourceConfigured() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendation(request, "Project does not exist", validKeyword).getStatus());
	}

	@Test
	public void testGetRecommendationNoKnowledgeSourceNotConfigured() {
		ConfigPersistenceManager.setProjectSource(projectKey, projectKey, false);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendation(request, projectKey, validKeyword).getStatus());
		ConfigPersistenceManager.setProjectSource(projectKey, projectKey, true);
	}

	@Test
	public void testGetRecommendationEvaluationActiveSource() {
		ConfigPersistenceManager.setProjectSource(projectKey, projectKey, true);
		assertEquals(Status.OK.getStatusCode(), viewRest.getRecommendationEvaluation(request, projectKey, validKeyword, "TEST").getStatus());
	}

	@Test
	public void testGetRecommendationEvaluationInactiveSource() {
		ConfigPersistenceManager.setProjectSource(projectKey, projectKey, false);
		assertEquals(Status.OK.getStatusCode(), viewRest.getRecommendationEvaluation(request, projectKey, validKeyword, "TEST").getStatus());
	}

	@Test
	public void testGetRecommendationEvaluationInvalidProject() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendationEvaluation(request, "", validKeyword, "TEST").getStatus());
	}

	@Test
	public void testGetRecommendationEvaluationRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendationEvaluation(null, projectKey, validKeyword, "TEST").getStatus());
	}

	@Test
	public void testGetRecommendationEvaluationBlankKeyword() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendationEvaluation(request, projectKey, "", "TEST").getStatus());
	}
}
