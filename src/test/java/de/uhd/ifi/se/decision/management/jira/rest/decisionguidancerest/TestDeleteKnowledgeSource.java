package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;
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
		decisionGuidanceRest.createRDFKnowledgeSource(request, "TEST", new RDFSource());
	}

	@Test
	public void testDeleteRDFKnowledgeSourceValid() {
		assertEquals(Status.OK.getStatusCode(),
				decisionGuidanceRest.deleteKnowledgeSource(request, "TEST", new RDFSource().getName()).getStatus());
	}

	@Test
	public void testDeleteRDFKnowledgeSourceInvalidValue() {
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				decisionGuidanceRest.deleteKnowledgeSource(request, "TEST", "").getStatus());
	}

	@Test
	public void testDeleteRDFKnowledgeSourceInvalidProject() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				decisionGuidanceRest.deleteKnowledgeSource(request, "", "DBPedia - Frameworks").getStatus());
	}
}