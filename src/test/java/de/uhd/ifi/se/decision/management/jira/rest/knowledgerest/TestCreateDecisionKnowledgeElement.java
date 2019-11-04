package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCreateDecisionKnowledgeElement extends TestSetUp {
	private KnowledgeRest knowledgeRest;
	private DecisionKnowledgeElement decisionKnowledgeElement;
	private HttpServletRequest request;

	private final static String BAD_REQUEST_ERROR = "Creation of decision knowledge element failed due to a bad request (element or request is null).";

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		init();

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-3");
		decisionKnowledgeElement = new DecisionKnowledgeElementImpl(issue);
		decisionKnowledgeElement.setType(KnowledgeType.SOLUTION);

		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
	}

	@Test
	public void testRequestNullElementNullParentIdZeroParentDocumentationLocationNullKeyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR)).build()
				             .getEntity(), knowledgeRest.createDecisionKnowledgeElement(null, null, 0, null, null).getEntity());
	}

	@Test
	public void testRequestNullElementFilledParentIdZeroParentDocumentationLocationNullKeyNull() {
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR)).build()
						.getEntity(),
				knowledgeRest.createDecisionKnowledgeElement(null, decisionKnowledgeElement, 0, null, null).getEntity());
	}

	@Test
	public void testRequestFilledElementNullParentIdZeroParentDocumentationLocationNullKeyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR)).build()
				             .getEntity(), knowledgeRest.createDecisionKnowledgeElement(request, null, 0, null, null).getEntity());
	}

	@Test
	public void testRequestFilledElementFilledParentIdZeroParentDocumentationLocationNullKeyNull() {

		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null, null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledAsProArgumentParentIdZeroParentDocumentationLocationNullKeyNull() {
		decisionKnowledgeElement.setType("Pro-argument");
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null, null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledAsConArgumentParentIdZeroParentDocumentationLocationNullKeyNull() {
		decisionKnowledgeElement.setType("Con-argument");
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null, null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdZeroParentDocumentationLocationJiraIssueKeyNull() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "i", null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdFilledParentDocumentationLocationJiraIssueKeyNull() {
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 7, "i", null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdFilledParentDocumentationLocationEmptyKeyNull() {
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 7, "", null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdFilledParentDocumentationLocationNullKeyNull() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 3, null, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledParentIdZeroParentDocumentationLocationJiraIssueCommentKeyEmpty() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "s", "").getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdZeroParentDocumentationLocationNullKeyExisting() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null, "TEST-3").getStatus());
	}
}