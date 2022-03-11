package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;

public class TestDiscardUndiscardRecommendations extends TestSetUp {

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
	public void testDiscardInvalidRecommendations() {
		KnowledgeElement target = new KnowledgeElement();
		assertEquals("If null is given as recommendation to be discarded, the response should be 'BAD_REQUEST'.",
				Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.discardRecommendation(request, null, filterSettings.getProjectKey()).getStatus());
		ElementRecommendation recommendationWithDiscardedFalse = new ElementRecommendation("The early bird catches the worm.", target);
		recommendationWithDiscardedFalse.setDiscarded(false);
		assertEquals("If a recommendation with discarded = false is given to be discarded, the response should be 'BAD_REQUEST'.",
				Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.discardRecommendation(request, recommendationWithDiscardedFalse, filterSettings.getProjectKey()).getStatus());
		ElementRecommendation recommendationWithSummaryNull = new ElementRecommendation((String) null, target);
		assertEquals("If a recommendation with summary = null is given to be discarded, the response should be 'BAD_REQUEST'.",
				Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.discardRecommendation(request, recommendationWithSummaryNull, filterSettings.getProjectKey()).getStatus());
		ElementRecommendation recommendationWithTargetNull = new ElementRecommendation("The early bird catches the worm.", null,
				new RDFSource(), new DecisionKnowledgeProject("TES"), "wikipedia.org");
		assertEquals("If a recommendation with target = null is given to be discarded, the response should be 'BAD_REQUEST'.",
				Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.discardRecommendation(request, recommendationWithSummaryNull, filterSettings.getProjectKey()).getStatus());
	}

	@Test
	public void testDiscardValidRecommendation() {
		KnowledgeElement target = new KnowledgeElement();
		ElementRecommendation recommendation = new ElementRecommendation("The early bird catches the worm.", target);
		recommendation.setDiscarded(true);
		assertEquals("Valid recommendation should be discarded with the response 'OK'.",
				Status.OK.getStatusCode(),
				decisionGuidanceRest.discardRecommendation(request, recommendation, filterSettings.getProjectKey()).getStatus());
	}

	@Test
	public void testUndoDiscardInvalidRecommendations() {
		KnowledgeElement target = new KnowledgeElement();
		assertEquals("If null is given as recommendation to be un-discarded, the response should be 'BAD_REQUEST'.",
				Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.undiscardRecommendation(request, null, filterSettings.getProjectKey()).getStatus());
		ElementRecommendation recommendation = new ElementRecommendation("The early bird catches the worm.", target);
		assertEquals("If a recommendation which has not been previously discarded is given to be un-discarded, the response should be 'BAD_REQUEST'.",
				Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.undiscardRecommendation(request, recommendation, filterSettings.getProjectKey()).getStatus());
	}

	@Test
	public void testUndoDiscardValidRecommendation() {
		KnowledgeElement target = new KnowledgeElement();
		ElementRecommendation recommendation = new ElementRecommendation("The early bird catches the worm.", target);
		recommendation.setDiscarded(true);
		decisionGuidanceRest.discardRecommendation(request, recommendation, filterSettings.getProjectKey());
		assertEquals("Valid recommendation should be un-discarded with the response 'OK'.",
				Status.OK.getStatusCode(),
				decisionGuidanceRest.undiscardRecommendation(request, recommendation, filterSettings.getProjectKey()).getStatus());

	}

}
