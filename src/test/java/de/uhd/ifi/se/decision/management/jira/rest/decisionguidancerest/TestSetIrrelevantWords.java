package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetIrrelevantWords extends TestSetUp {
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
	public void testSetIrrelevantWordsValid() {
		assertEquals(200, decisionGuidanceRest.setIrrelevantWords(request, "TEST", "WHICH;WHAT;SHOULD").getStatus());
	}

	@Test
	public void testSetIrrelevantWordsEmpty() {
		assertEquals(400, decisionGuidanceRest.setIrrelevantWords(request, "TEST", "").getStatus());
	}

	@Test
	public void testSetIrrelevantWordsProjectKeyInvalid() {
		assertEquals(400, decisionGuidanceRest.setIrrelevantWords(request, "", "WHICH;WHAT;SHOULD").getStatus());
	}
}
