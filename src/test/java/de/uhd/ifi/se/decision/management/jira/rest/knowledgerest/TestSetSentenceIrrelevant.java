package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.rest.impl.KnowledgeRestImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestSetSentenceIrrelevant extends TestSetUp {

	private final static String BAD_REQUEST_ERROR = "Setting element irrelevant failed due to a bad request.";

	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRestImpl();
		init();

		request = new MockHttpServletRequest();
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
	}

	@Test
	@NonTransactional
	public void testRequestNullElementNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR)).build()
				.getEntity(), knowledgeRest.setSentenceIrrelevant(null, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestNullElementFilled() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("This is a test sentence.");
		DecisionKnowledgeElement decisionKnowledgeElement = comment.get(0);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR)).build()
				.getEntity(), knowledgeRest.setSentenceIrrelevant(null, decisionKnowledgeElement).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR)).build()
				.getEntity(), knowledgeRest.setSentenceIrrelevant(request, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilled() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("This is a test sentence.");
		DecisionKnowledgeElement decisionKnowledgeElement = comment.get(0);
		decisionKnowledgeElement.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.setSentenceIrrelevant(request, decisionKnowledgeElement).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledButNotDocumentedInJiraIssueComment() {
		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-3");
		DecisionKnowledgeElement decisionKnowledgeElement = new DecisionKnowledgeElementImpl(issue);
		assertEquals(503, knowledgeRest.setSentenceIrrelevant(request, decisionKnowledgeElement).getStatus());
	}
}
