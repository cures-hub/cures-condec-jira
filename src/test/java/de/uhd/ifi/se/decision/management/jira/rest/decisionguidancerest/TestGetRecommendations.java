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

public class TestGetRecommendations extends TestSetUp {

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
	public void testGetRecommendations() {
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, "TEST", validKeyword, 1, "i").getStatus());
		decisionGuidanceRest.setRecommendationInput(request, "TEST", RecommenderType.KEYWORD.toString(), true);
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, "TEST", validKeyword, 1, "i").getStatus());
	}

	@Test
	public void testGetRecommendationsEmptyKeyword() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, "TEST", invalidKeyword, 1, "s").getStatus());
	}

	@Test
	public void testGetRecommendationsEmptyProject() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, "", validKeyword, 1, "s").getStatus());
	}

	@Test
	public void testGetRecommendationsProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, null, validKeyword, 1, "s").getStatus());
	}

	@Test
	public void testGetRecommendationsRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendations(null, projectKey, validKeyword, 1, "s").getStatus());
	}

	@Test
	public void testGetRecommendationsNoKnowledgeSourceConfigured() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), decisionGuidanceRest
				.getRecommendations(request, "Project does not exist", validKeyword, 1, "s").getStatus());
	}

	@Test
	public void testGetRecommendationsGetRecommender() {
		DecisionGuidanceConfiguration decisionGuidanceConfiguration = ConfigPersistenceManager
				.getDecisionGuidanceConfiguration(projectKey);
		decisionGuidanceConfiguration.setRecommendationInput(RecommenderType.KEYWORD.toString(), true);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);

		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, "TEST", validKeyword, 1, "s").getStatus());
		decisionGuidanceConfiguration.setRecommendationInput(RecommenderType.ISSUE.toString(), true);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, "TEST", validKeyword, 1, "i").getStatus());
	}

	// @Test
	// public void testGetRecommendationAddDirectly() {
	// ConfigPersistenceManager.setAddRecommendationDirectly("TEST",true);
	// assertEquals(Status.OK.getStatusCode(), viewRest.getRecommendation(request,
	// "TEST", validKeyword, 1).getStatus());
	// }

	@Test
	public void testGetRecommendationNoKnowledgeSourceNotConfigured() {
		decisionGuidanceRest.setProjectSource(request, projectKey, projectKey, false);
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, projectKey, validKeyword, 1, "s").getStatus());
		decisionGuidanceRest.setProjectSource(request, projectKey, projectKey, true);
	}
}
