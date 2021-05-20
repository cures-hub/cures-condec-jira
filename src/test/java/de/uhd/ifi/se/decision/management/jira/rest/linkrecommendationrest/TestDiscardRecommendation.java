package de.uhd.ifi.se.decision.management.jira.rest.linkrecommendationrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import de.uhd.ifi.se.decision.management.jira.rest.LinkRecommendationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDiscardRecommendation extends TestSetUp {

	protected HttpServletRequest request;
	protected LinkRecommendationRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new LinkRecommendationRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testValidRecommendation() {
		KnowledgeElement knowledgeElement0 = KnowledgeElements.getTestKnowledgeElement();
		KnowledgeElement knowledgeElement1 = KnowledgeElements.getSolvedDecisionProblem();
		assertEquals(Status.OK.getStatusCode(), configRest
				.discardRecommendation(request, "TEST", new LinkRecommendation(knowledgeElement0, knowledgeElement1))
				.getStatus());
	}

	@Test
	@NonTransactional
	public void testRecommendationNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.discardRecommendation(request, "TEST", null).getStatus());
	}

	@Test
	@NonTransactional
	public void testInvalidRecommendation() {
		KnowledgeElement knowledgeElement1 = KnowledgeElements.getSolvedDecisionProblem();
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest
				.discardRecommendation(request, "TEST", new LinkRecommendation(null, knowledgeElement1)).getStatus());
	}

}
