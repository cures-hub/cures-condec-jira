package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCreateDecisionKnowledgeElement extends TestSetUp {
	private KnowledgeRest knowledgeRest;
	private KnowledgeElement decisionKnowledgeElement;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		knowledgeRest = new KnowledgeRest();

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-3");
		decisionKnowledgeElement = new KnowledgeElement(issue);
		decisionKnowledgeElement.setType(KnowledgeType.SOLUTION);

		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
	}

	@Test
	public void testRequestNullElementNullParentIdZeroParentDocumentationLocationNullKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(null, null, 0, null, null).getStatus());
	}

	@Test
	public void testRequestNullElementFilledParentIdZeroParentDocumentationLocationNullKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest
				.createDecisionKnowledgeElement(null, decisionKnowledgeElement, 0, null, null).getStatus());
	}

	@Test
	public void testRequestFilledElementNullParentIdZeroParentDocumentationLocationNullKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, null, 0, null, null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdZeroParentDocumentationLocationNullKeyNull() {

		assertEquals(Status.OK.getStatusCode(), knowledgeRest
				.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null, null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledAsProArgumentParentIdZeroParentDocumentationLocationNullKeyNull() {
		decisionKnowledgeElement.setType("Pro-argument");
		assertEquals(Status.OK.getStatusCode(), knowledgeRest
				.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null, null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledAsConArgumentParentIdZeroParentDocumentationLocationNullKeyNull() {
		decisionKnowledgeElement.setType("Con-argument");
		assertEquals(Status.OK.getStatusCode(), knowledgeRest
				.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null, null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdZeroParentDocumentationLocationJiraIssueKeyNull() {
		assertEquals(Status.OK.getStatusCode(), knowledgeRest
				.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "i", null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdFilledParentDocumentationLocationJiraIssueKeyNull() {
		assertEquals(Status.OK.getStatusCode(), knowledgeRest
				.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 7, "i", null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdFilledParentDocumentationLocationEmptyKeyNull() {
		assertEquals(Status.OK.getStatusCode(), knowledgeRest
				.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 7, "", null).getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdFilledParentDocumentationLocationNullKeyNull() {
		assertEquals(Status.OK.getStatusCode(), knowledgeRest
				.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 3, null, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledParentIdZeroParentDocumentationLocationJiraIssueCommentKeyEmpty() {
		assertEquals(Status.OK.getStatusCode(), knowledgeRest
				.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "s", "").getStatus());
	}

	@Test
	public void testRequestFilledElementFilledParentIdZeroParentDocumentationLocationNullKeyExisting() {
		assertEquals(Status.OK.getStatusCode(), knowledgeRest
				.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null, "TEST-3").getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementDocumentationLocationNull() {
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("TEST");
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, element, 1, "i", null).getStatus());
	}
}