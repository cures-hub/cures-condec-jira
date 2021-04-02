package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.DecisionGuidanceConfiguration;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetRecommendation extends TestSetUp {

	private DecisionGuidanceRest decisionGuidanceRest;
	private HttpServletRequest request;
	private static final String projectKey = "TEST";
	private static final String validKeyword = "keyword";
	private static final String invalidKeyword = "";

	@Before
	public void setUp() {
		init();
		decisionGuidanceRest = new DecisionGuidanceRest();
		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
	}

	@Test
	public void testGetRecommendation() {
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendation(request, "TEST", validKeyword, 1, "i").getStatus());
		ConfigPersistenceManager.setRecommendationInput("TEST", RecommenderType.KEYWORD.toString(), true);
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendation(request, "TEST", validKeyword, 1, "i").getStatus());
	}

	@Test
	public void testGetRecommendationEmptyKeyword() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendation(request, "TEST", invalidKeyword, 1, "s").getStatus());
	}

	@Test
	public void testGetRecommendationEmptyProject() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendation(request, "", validKeyword, 1, "s").getStatus());
	}

	@Test
	public void testGetRecommendationProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendation(request, null, validKeyword, 1, "s").getStatus());
	}

	@Test
	public void testGetRecommendationRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendation(null, projectKey, validKeyword, 1, "s").getStatus());
	}

	@Test
	public void testGetRecommendationNoKnowledgeSourceConfigured() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), decisionGuidanceRest
				.getRecommendation(request, "Project does not exist", validKeyword, 1, "s").getStatus());
	}

	@Test
	public void testGetRecommendationGetRecommender() {
		DecisionGuidanceConfiguration decisionGuidanceConfiguration = ConfigPersistenceManager
				.getDecisionGuidanceConfiguration(projectKey);
		decisionGuidanceConfiguration.setRecommendationInput(RecommenderType.KEYWORD.toString(), true);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);

		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendation(request, "TEST", validKeyword, 1, "s").getStatus());
		decisionGuidanceConfiguration.setRecommendationInput(RecommenderType.ISSUE.toString(), true);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendation(request, "TEST", validKeyword, 1, "i").getStatus());
	}

	// @Test
	// public void testGetRecommendationAddDirectly() {
	// ConfigPersistenceManager.setAddRecommendationDirectly("TEST",true);
	// assertEquals(Status.OK.getStatusCode(), viewRest.getRecommendation(request,
	// "TEST", validKeyword, 1).getStatus());
	// }

	@Test
	public void testGetRecommendationNoKnowledgeSourceNotConfigured() {
		ConfigPersistenceManager.setProjectSource(projectKey, projectKey, false);
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendation(request, projectKey, validKeyword, 1, "s").getStatus());
		ConfigPersistenceManager.setProjectSource(projectKey, projectKey, true);
	}

	/*
	 * Test Evaluation
	 */

	@Test
	public void testGetRecommendationEvaluationActiveSource() {
		assertEquals(Status.OK.getStatusCode(), decisionGuidanceRest
				.getRecommendationEvaluation(request, projectKey, validKeyword, 2, "TEST", 5, "i").getStatus());
	}

	@Test
	public void testGetRecommendationEvaluationINVALIDISSUE() {
		assertEquals(Status.NOT_FOUND.getStatusCode(), decisionGuidanceRest
				.getRecommendationEvaluation(request, projectKey, validKeyword, -12354, "TEST", 5, "s").getStatus());
	}

	@Test
	public void testGetRecommendationEvaluationInvalidProject() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), decisionGuidanceRest
				.getRecommendationEvaluation(request, "", validKeyword, 1, "TEST", 5, "s").getStatus());
	}

	@Test
	public void testGetRecommendationEvaluationRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), decisionGuidanceRest
				.getRecommendationEvaluation(null, projectKey, validKeyword, 1, "TEST", 5, "s").getStatus());
	}
}
