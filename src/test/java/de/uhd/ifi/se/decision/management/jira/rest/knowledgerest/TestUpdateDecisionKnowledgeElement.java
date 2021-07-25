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
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateDecisionKnowledgeElement extends TestSetUp {
	private KnowledgeRest knowledgeRest;
	private KnowledgeElement decisionKnowledgeElement;
	private HttpServletRequest request;

	private final static String BAD_REQUEST_ERROR = "Element could not be updated due to a bad request.";

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		init();

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("3");
		decisionKnowledgeElement = new KnowledgeElement(issue);
		decisionKnowledgeElement.setType(KnowledgeType.SOLUTION);

		request = new MockHttpServletRequest();
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledParentElementExistingParentDocumentationLocationJiraIssue() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("This is a test sentence.");
		KnowledgeElement sentence = comment.get(0);

		Link link = new Link(sentence, decisionKnowledgeElement);
		GenericLinkManager.insertLink(link, null);

		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, sentence, 3, "i").getStatus());
	}

	@Test
	public void testRequestFilledElementEmptyParentIdZeroParentDocumentationLocationEmpty() {
		KnowledgeElement decisionKnowledgeElement = new KnowledgeElement();
		decisionKnowledgeElement.setProject("TEST");

		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "").getStatus());
	}

	@Test
	public void testRequestNullElementNullParentIdZeroParentDocumentationLocationNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR)).build()
				.getEntity(), knowledgeRest.updateDecisionKnowledgeElement(null, null, 0, null).getEntity());
	}

	@Test
	public void testRequestNullElementFilledParentIdZeroParentDocumentationLocationNull() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("This is a test sentence.");
		KnowledgeElement decisionKnowledgeElement = comment.get(0);
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR)).build()
						.getEntity(),
				knowledgeRest.updateDecisionKnowledgeElement(null, decisionKnowledgeElement, 0, null).getEntity());
	}

	@Test
	public void testRequestFilledElementNullParentIdZeroParentDocumentationLocationNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR)).build()
				.getEntity(), knowledgeRest.updateDecisionKnowledgeElement(request, null, 0, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledParentIdZeroParentDocumentationLocationNull() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("This is a test sentence.");
		KnowledgeElement decisionKnowledgeElement = comment.get(0);
		decisionKnowledgeElement.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithCommentChangedParentIdZeroParentDocumentationLocationEmpty() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText("This is a test sentence.");
		KnowledgeElement decisionKnowledgeElement = comment.get(0);
		assertEquals(decisionKnowledgeElement.getType(), KnowledgeType.OTHER);

		String newText = "some fancy new text";
		decisionKnowledgeElement.setDescription(newText);
		decisionKnowledgeElement.setType(KnowledgeType.PRO);

		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "").getStatus());
		PartOfJiraIssueText sentence = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getKnowledgeElement(decisionKnowledgeElement.getId());
		assertEquals(sentence.getType(), KnowledgeType.PRO);
		assertEquals(newText, sentence.getDescription());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithCommentChangedCheckValidTextWithManuallTaggedComment() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{issue}This is a test sentence.{Issue}");
		KnowledgeElement decisionKnowledgeElement = comment.get(0);

		String newText = "some fancy new text";
		decisionKnowledgeElement.setDescription(newText);
		decisionKnowledgeElement.setType(KnowledgeType.ISSUE);
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "s").getStatus());
		PartOfJiraIssueText sentence = (PartOfJiraIssueText) new JiraIssueTextPersistenceManager("")
				.getKnowledgeElement(decisionKnowledgeElement.getId());
		assertEquals(sentence.getType(), KnowledgeType.ISSUE);
		assertEquals(newText, sentence.getDescription());

		MutableComment mutableComment = (MutableComment) ComponentAccessor.getCommentManager()
				.getCommentById(sentence.getCommentId());
		assertEquals("{issue}some fancy new text{issue}", mutableComment.getBody());
	}
}
