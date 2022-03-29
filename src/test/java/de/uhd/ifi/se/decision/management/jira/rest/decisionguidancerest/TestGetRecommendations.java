package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.recommendation.DiscardedRecommendationPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource.ProjectSource;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

import java.util.List;

public class TestGetRecommendations extends TestSetUp {

	private DecisionGuidanceRest decisionGuidanceRest;
	private HttpServletRequest request;
	private FilterSettings filterSettings;

	@Before
	public void setUp() {
		init();
		decisionGuidanceRest = new DecisionGuidanceRest();
		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElementObject(KnowledgeElements.getSolvedDecisionProblem());
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
	public void testFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, null).getStatus());
	}

	@Test
	public void testSelectedElementNull() {
		filterSettings.setSelectedElementObject((KnowledgeElement) null);
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, filterSettings).getStatus());
	}

	@Test
	public void testRequestNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendations(null, filterSettings).getStatus());
	}

	@Test
	public void testNoKnowledgeSourceConfigured() {
		filterSettings.setProjectKey("Project does not exist");
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, filterSettings).getStatus());
	}

	@Test
	public void testAddRecommendationsDirectly() {
		decisionGuidanceRest.setAddRecommendationDirectly(request, "TEST", true);
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, filterSettings).getStatus());
		decisionGuidanceRest.setAddRecommendationDirectly(request, "TEST", false);
	}

	@Test
	public void testNoKnowledgeSourceNotConfigured() {
		decisionGuidanceRest.setProjectSource(request, "TEST", "TEST", false);
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.getRecommendations(request, filterSettings).getStatus());
		decisionGuidanceRest.setProjectSource(request, "TEST", "TEST", true);
	}

	@Test
	public void testMoreDiscardedRecommendationsThanMaxLimit() {
		decisionGuidanceRest.setProjectSource(request, "TEST", "TEST", false);
		KnowledgeElement target = new KnowledgeElement(0, "Dummy Target", "Dummy Description", KnowledgeType.ISSUE, "TEST",
				"Dummy key", DocumentationLocation.UNKNOWN, KnowledgeStatus.UNRESOLVED);
		DiscardedRecommendationPersistenceManager.saveDiscardedElementRecommendation(new ElementRecommendation("Dummy recommendation 1", target), "TEST");
		DiscardedRecommendationPersistenceManager.saveDiscardedElementRecommendation(new ElementRecommendation("Dummy recommendation 2", target), "TEST");
		ConfigPersistenceManager.getDecisionGuidanceConfiguration("TEST").setMaxNumberOfRecommendations(1);

		Response response = decisionGuidanceRest.getRecommendations(request, new FilterSettings("TEST", target.getSummary()));
		List<ElementRecommendation> recommendations = (List<ElementRecommendation>) response.getEntity();

		assertEquals(1,	recommendations.size());
	}
}
