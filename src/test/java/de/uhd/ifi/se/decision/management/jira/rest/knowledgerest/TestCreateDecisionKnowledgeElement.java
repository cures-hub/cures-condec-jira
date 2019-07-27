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
	public void testRequestNullElementNullParentIdZeroParentDocumentationLocationNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR)).build()
				.getEntity(), knowledgeRest.createDecisionKnowledgeElement(null, null, 0, null).getEntity());
	}

	@Test
	public void testRequestNullElementFilledParentIdZeroParentDocumentationLocationNull() {
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR)).build()
						.getEntity(),
				knowledgeRest.createDecisionKnowledgeElement(null, decisionKnowledgeElement, 0, null).getEntity());
	}

	@Test
	public void testRequestFilledElementNullParentIdZeroParentDocumentationLocationNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR)).build()
				.getEntity(), knowledgeRest.createDecisionKnowledgeElement(request, null, 0, null).getEntity());
	}

	@Test
	public void testRequestFilledElementFilledParentIdZeroParentDocumentationLocationNull() {

		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledAsProArgumentParentIdZeroParentDocumentationLocationNull() {
		decisionKnowledgeElement.setType("Pro-argument");
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledAsConArgumentParentIdZeroParentDocumentationLocationNull() {
		decisionKnowledgeElement.setType("Con-argument");
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdZeroParentDocumentationLocationJiraIssue() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "i").getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdFilledParentDocumentationLocationJiraIssue() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 7, "i").getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdFilledParentDocumentationLocationEmpty() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 7, "").getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdFilledParentDocumentationLocationNull() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 7, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledParentIdZeroParentDocumentationLocationJiraIssueComment() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "s").getStatus());
	}
}
