package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetSimilarityThreshold extends TestSetUp {
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
	public void testSetSimilarityThresholdValid() {
		assertEquals(200, decisionGuidanceRest.setSimilarityThreshold(request, "TEST", 1).getStatus());
	}

	@Test
	public void testSetSimilarityThresholdInvalidProjectKey() {
		assertEquals(400, decisionGuidanceRest.setSimilarityThreshold(request, "", 1).getStatus());
	}

	@Test
	public void testSetSimilarityThresholdInvalidThreshold() {
		assertEquals(400, decisionGuidanceRest.setSimilarityThreshold(request, "TEST", -1).getStatus());
		assertEquals(400, decisionGuidanceRest.setSimilarityThreshold(request, "TEST", 3).getStatus());
	}
}
