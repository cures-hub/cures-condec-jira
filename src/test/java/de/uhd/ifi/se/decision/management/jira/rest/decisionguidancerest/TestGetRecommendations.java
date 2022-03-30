package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.recommendation.DiscardedRecommendationPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.DecisionGuidanceConfiguration;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetRecommendations extends TestSetUp {

	private DecisionGuidanceRest decisionGuidanceRest;
	private HttpServletRequest request;
	private FilterSettings filterSettings;
	private List<Issue> issues;

	@Before
	public void setUp() {
		init();
		decisionGuidanceRest = new DecisionGuidanceRest();
		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElementObject(KnowledgeElements.getSolvedDecisionProblem());
		issues = JiraIssues.getTestJiraIssues();
	}

	@Test
	public void testGetRecommendationsForSingleDecisionProblem() {
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, filterSettings).getStatus());
	}

	@Test
	public void testGetRecommendationsForAllDecisionProblemsForWorkItem() {
		filterSettings.setSelectedElementObject(KnowledgeElements.getTestKnowledgeElement());
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, filterSettings).getStatus());
	}

	@Test
	public void testEmptyProject() {
		filterSettings.setProjectKey("");
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, filterSettings).getStatus());
	}

	@Test
	public void testProjectKeyNull() {
		filterSettings.setProjectKey(null);
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, filterSettings).getStatus());
	}

	@Test
	@NonTransactional
	public void testFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testSelectedElementNull() {
		filterSettings.setSelectedElementObject((KnowledgeElement) null);
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, filterSettings).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendations(null, filterSettings).getStatus());
	}

	@Test
	@NonTransactional
	public void testNoKnowledgeSourceConfigured() {
		filterSettings.setProjectKey("Project does not exist");
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, filterSettings).getStatus());
	}

	@Test
	@NonTransactional
	public void testAddRecommendationsDirectly() {
		decisionGuidanceRest.setAddRecommendationDirectly(request, "TEST", true);
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, filterSettings).getStatus());
		decisionGuidanceRest.setAddRecommendationDirectly(request, "TEST", false);
	}

	@Test
	@NonTransactional
	public void testNoKnowledgeSourceNotConfigured() {
		decisionGuidanceRest.setProjectSource(request, "TEST", "TEST", false);
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, filterSettings).getStatus());
		decisionGuidanceRest.setProjectSource(request, "TEST", "TEST", true);
	}

	@Test
	@NonTransactional
	public void testMoreDiscardedRecommendationsThanMaxLimit() {
		KnowledgeElement target = new KnowledgeElement(issues.get(0));
		ElementRecommendation recommendation1 = new ElementRecommendation("Dummy recommendation 1", target);
		ElementRecommendation recommendation2 = new ElementRecommendation("Dummy recommendation 2", target);
		recommendation1.setDiscarded(true);
		recommendation2.setDiscarded(true);
		DiscardedRecommendationPersistenceManager.saveDiscardedElementRecommendation(recommendation1,
				target.getProject().getProjectKey());
		DiscardedRecommendationPersistenceManager.saveDiscardedElementRecommendation(recommendation2,
				target.getProject().getProjectKey());
		System.out
				.println(DiscardedRecommendationPersistenceManager.getDiscardedDecisionGuidanceRecommendations(target));
		System.out.println("Project Test: ");
		System.out.println(target.getProject().getProjectKey());
		DecisionGuidanceConfiguration config = ConfigPersistenceManager
				.getDecisionGuidanceConfiguration(target.getProject().getProjectKey());
		config.setMaxNumberOfRecommendations(1);
		ConfigPersistenceManager.saveDecisionGuidanceConfiguration(target.getProject().getProjectKey(), config);
		System.out.print("MaxNr Test: ");
		System.out.println(ConfigPersistenceManager
				.getDecisionGuidanceConfiguration(target.getProject().getProjectKey()).getMaxNumberOfRecommendations());
		FilterSettings filterSettings = new FilterSettings(target.getProject().getProjectKey(), "");
		filterSettings.setSelectedElementObject(target);
		Response response = decisionGuidanceRest.getRecommendations(request, filterSettings);
		System.out.println(response.getEntity());
		System.out.println(response.getStatus());
		System.out.println(response.getMetadata());
		System.out.println(response.getEntity().getClass());
		List<ElementRecommendation> recommendations = (List<ElementRecommendation>) response.getEntity();

		assertEquals(1, recommendations.size());
	}
}
