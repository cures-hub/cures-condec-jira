package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
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
		assertEquals(Status.OK.getStatusCode(), viewRest.getRecommendation(request, "TEST", validKeyword, 1).getStatus());
		ConfigPersistenceManager.setRecommendationInput("TEST", "KEYWORD");
		assertEquals(Status.OK.getStatusCode(), viewRest.getRecommendation(request, "TEST", validKeyword, 1).getStatus());

	}

	@Test
	public void testGetRecommendationEmptyKeyword() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendation(request, "TEST", invalidKeyword, 1).getStatus());
	}

	@Test
	public void testGetRecommendationEmptyProject() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendation(request, "", validKeyword, 1).getStatus());
	}

	@Test
	public void testGetRecommendationProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendation(request, null, validKeyword, 1).getStatus());
	}

	@Test
	public void testGetRecommendationRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendation(null, projectKey, validKeyword, 1).getStatus());
	}

	@Test
	public void testGetRecommendationNoKnowledgeSourceConfigured() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendation(request, "Project does not exist", validKeyword, 1).getStatus());
	}

	@Test
	public void testGetRecommendationGetRecommender() {
		ConfigPersistenceManager.setRecommendationInput("TEST", RecommenderType.KEYWORD.toString());
		ConfigPersistenceManager.setAddRecommendationDirectly("TEST", false);
		assertEquals(Status.OK.getStatusCode(), viewRest.getRecommendation(request, "TEST", validKeyword, 1).getStatus());
		ConfigPersistenceManager.setRecommendationInput("TEST", RecommenderType.ISSUE.toString());
		assertEquals(Status.OK.getStatusCode(), viewRest.getRecommendation(request, "TEST", validKeyword, 1).getStatus());
	}

	@Test
	public void testGetRecommendationAddDirectly() {
		ConfigPersistenceManager.setAddRecommendationDirectly("TEST",true);
		assertEquals(Status.OK.getStatusCode(), viewRest.getRecommendation(request, "TEST", validKeyword, 1).getStatus());
	}

	@Test
	public void testGetRecommendationNoKnowledgeSourceNotConfigured() {
		ConfigPersistenceManager.setProjectSource(projectKey, projectKey, false);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendation(request, projectKey, validKeyword, 1).getStatus());
		ConfigPersistenceManager.setProjectSource(projectKey, projectKey, true);
	}



	/*
	 * Test Evaluation
	 * */

	@Test
	public void testGetRecommendationEvaluationActiveSource() {
		assertEquals(Status.OK.getStatusCode(), viewRest.getRecommendationEvaluation(request, projectKey, validKeyword, 2, "TEST").getStatus());
	}

	@Test
	public void testGetRecommendationEvaluationINVALIDISSUE() {
		assertEquals(Status.NOT_FOUND.getStatusCode(), viewRest.getRecommendationEvaluation(request, projectKey, validKeyword, -12354, "TEST").getStatus());
	}

	@Test
	public void testGetRecommendationEvaluationInvalidProject() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendationEvaluation(request, "", validKeyword, 1, "TEST").getStatus());
	}

	@Test
	public void testGetRecommendationEvaluationRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getRecommendationEvaluation(null, projectKey, validKeyword, 1, "TEST").getStatus());
	}

}
