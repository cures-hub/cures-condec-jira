package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetInputType extends TestSetUp {
	protected HttpServletRequest request;
	protected DecisionGuidanceRest decisionGuidanceRest;

	@Before
	public void setUp() {
		init();
		decisionGuidanceRest = new DecisionGuidanceRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testSetRecommendationInputValid() {
		assertEquals(200, decisionGuidanceRest.setRecommendationInput(request, "TEST", "KEYWORD", true).getStatus());
	}

	@Test
	public void testSetRecommendationInputRequestNull() {
		assertEquals(400, decisionGuidanceRest.setRecommendationInput(null, "TEST", "KEYWORD", true).getStatus());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetRecommendationInputUnknownType() {
		assertEquals(200, decisionGuidanceRest.setRecommendationInput(request, "TEST", "NON-EXISTING-INPUT-TYPE", true)
				.getStatus());
		assertEquals(0, ConfigPersistenceManager.getDecisionGuidanceConfiguration("TEST").getInputTypes().size());
	}
}
