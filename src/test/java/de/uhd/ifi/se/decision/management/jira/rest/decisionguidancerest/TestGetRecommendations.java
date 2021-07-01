package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.DecisionGuidanceConfiguration;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;

public class TestGetRecommendations extends TestSetUp {

	private static final String projectKey = "TEST";
	private DecisionGuidanceRest decisionGuidanceRest;
	private HttpServletRequest request;
	private String jiraIssueKey;

	@Before
	public void setUp() {
		init();
		decisionGuidanceRest = new DecisionGuidanceRest();
		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		jiraIssueKey = JiraIssues.getTestJiraIssues().get(4).getKey();
	}

	@Test
	public void testGetRecommendations() {
		assertEquals(Status.OK.getStatusCode(),
			decisionGuidanceRest.getRecommendations(request, projectKey, jiraIssueKey).getStatus());
	}


	@Test
	public void testGetRecommendationsEmptyProject() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			decisionGuidanceRest.getRecommendations(request, "", jiraIssueKey).getStatus());
	}

	@Test
	public void testGetRecommendationsProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			decisionGuidanceRest.getRecommendations(request, null, jiraIssueKey).getStatus());
	}

	@Test
	public void testGetRecommendationsRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
			decisionGuidanceRest.getRecommendations(null, projectKey, jiraIssueKey).getStatus());
	}

	@Test
	public void testGetRecommendationsNoKnowledgeSourceConfigured() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), decisionGuidanceRest
			.getRecommendations(request, "Project does not exist", jiraIssueKey).getStatus());
	}

	@Test
	public void testGetRecommendationsGetRecommender() {
		DecisionGuidanceConfiguration decisionGuidanceConfiguration = ConfigPersistenceManager
			.getDecisionGuidanceConfiguration(projectKey);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(projectKey, decisionGuidanceConfiguration);

		assertEquals(Status.OK.getStatusCode(),
			decisionGuidanceRest.getRecommendations(request, "TEST", jiraIssueKey).getStatus());
	}

	//
//	@Test
	public void testGetRecommendationsAddDirectly() {
		decisionGuidanceRest.setAddRecommendationDirectly(request, projectKey, true);
		assertEquals(Status.OK.getStatusCode(),
			decisionGuidanceRest.getRecommendations(request, "TEST", jiraIssueKey).getStatus());
		decisionGuidanceRest.setAddRecommendationDirectly(request, projectKey, false);
	}

	@Test
	public void testGetRecommendationNoKnowledgeSourceNotConfigured() {
		decisionGuidanceRest.setProjectSource(request, projectKey, projectKey, false);
		assertEquals(Status.OK.getStatusCode(),
			decisionGuidanceRest.getRecommendations(request, projectKey, jiraIssueKey).getStatus());
		decisionGuidanceRest.setProjectSource(request, projectKey, projectKey, true);
	}
}
