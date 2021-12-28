package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetRDFKnowledgeSourceActivated extends TestSetUp {
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
	public void testActivateRDFKnowledgeSourceValidProjectValidValue() {
		assertEquals(200,
				decisionGuidanceRest.setRDFKnowledgeSourceActivated(request, "TEST", "DBPedia", true).getStatus());
	}

	@Test
	public void testActivateRDFKnowledgeSourceInvalidProjectValidValue() {
		assertEquals(400, decisionGuidanceRest.setRDFKnowledgeSourceActivated(request, null, "DBPedia", true).getStatus());
	}

	@Test
	public void testActivateRDFKnowledgeSourceValidProjectInvalidKnowledgeSourceName() {
		assertEquals(400, decisionGuidanceRest.setRDFKnowledgeSourceActivated(request, "TEST", "", true).getStatus());
	}
}
