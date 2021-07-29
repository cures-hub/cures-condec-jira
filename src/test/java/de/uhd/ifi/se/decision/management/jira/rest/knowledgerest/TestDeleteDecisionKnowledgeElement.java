package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestDeleteDecisionKnowledgeElement extends TestSetUp {

	private final static String DELETION_ERROR = "Deletion of decision knowledge element failed.";

	private KnowledgeRest knowledgeRest;
	private KnowledgeElement decisionKnowledgeElement;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		init();

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-3");
		decisionKnowledgeElement = new KnowledgeElement(issue);
		decisionKnowledgeElement.setType(KnowledgeType.SOLUTION);

		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestFilledElementFilled() {
		assertEquals(Response.Status.OK.getStatusCode(),
				knowledgeRest.deleteDecisionKnowledgeElement(request, decisionKnowledgeElement).getStatus());
	}

	@Test
	public void testRequestErrorElementFilled() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.BLACK_HEAD.getApplicationUser());
		assertEquals(
				Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", DELETION_ERROR))
						.build().getEntity(),
				knowledgeRest.deleteDecisionKnowledgeElement(request, decisionKnowledgeElement).getEntity());
	}

	@Test
	public void testRequestNullElementNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR))
				.build().getEntity(), knowledgeRest.deleteDecisionKnowledgeElement(null, null).getEntity());
	}

	@Test
	public void testRequestNullElementFilled() {
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR)).build()
						.getEntity(),
				knowledgeRest.deleteDecisionKnowledgeElement(null, decisionKnowledgeElement).getEntity());
	}

	@Test
	public void testRequestFilledElementNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR))
				.build().getEntity(), knowledgeRest.deleteDecisionKnowledgeElement(request, null).getEntity());
	}
}
