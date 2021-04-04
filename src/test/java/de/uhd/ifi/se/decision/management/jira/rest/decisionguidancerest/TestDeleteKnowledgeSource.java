package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestDeleteKnowledgeSource extends TestSetUp {
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
	public void testDeleteRDFKnowledgeSourceValid() {
		assertEquals(200,
				decisionGuidanceRest.deleteKnowledgeSource(request, "TEST", "DBPedia - Frameworks").getStatus());
	}

	@Test
	public void testDeleteRDFKnowledgeSourceInvalidValue() {
		assertEquals(400, decisionGuidanceRest.deleteKnowledgeSource(request, "TEST", "").getStatus());
	}

	@Test
	public void testDeleteRDFKnowledgeSourceInvalidProject() {
		assertEquals(400, decisionGuidanceRest.deleteKnowledgeSource(request, "", "DBPedia - Frameworks").getStatus());
	}
}