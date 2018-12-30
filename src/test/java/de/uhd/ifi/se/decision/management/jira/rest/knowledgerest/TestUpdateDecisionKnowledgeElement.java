package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.TestComment;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplateWebhook;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestUpdateDecisionKnowledgeElement extends TestSetUpWithIssues {
	private EntityManager entityManager;
	private KnowledgeRest knowledgeRest;
	private DecisionKnowledgeElement decisionKnowledgeElement;
	private HttpServletRequest request;

	private final static String BAD_REQUEST_ERROR = "Element could not be updated due to a bad request.";

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("3");
		decisionKnowledgeElement = new DecisionKnowledgeElementImpl(issue);
		decisionKnowledgeElement.setType(KnowledgeType.SOLUTION);

		request = new MockHttpServletRequest();
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledParentElementExistingParentDocumentationLocationJiraIssue() {
		TestComment testComment = new TestComment();
		Comment comment = testComment.getComment("This is a test sentence.");
		DecisionKnowledgeElement sentence = comment.getSentences().get(0);

		Link link = new LinkImpl(sentence, decisionKnowledgeElement);
		GenericLinkManager.insertLink(link, null);

		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, sentence, 3, "i").getStatus());
	}

	@Test
	public void testRequestFilledElementEmptyParentIdZeroParentDocumentationLocationEmpty() {
		DecisionKnowledgeElement decisionKnowledgeElement = new DecisionKnowledgeElementImpl();
		decisionKnowledgeElement.setProject("TEST");

		assertEquals(Status.NOT_FOUND.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "").getStatus());
	}

	@Test
	public void testRequestNullElementNullParentIdZeroParentDocumentationLocationNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR)).build()
				.getEntity(), knowledgeRest.updateDecisionKnowledgeElement(null, null, 0, null).getEntity());
	}

	@Test
	public void testRequestNullElementFilledParentIdZeroParentDocumentationLocationNull() {
		TestComment testComment = new TestComment();
		Comment comment = testComment.getComment("This is a test sentence.");
		DecisionKnowledgeElement decisionKnowledgeElement = comment.getSentences().get(0);
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
		TestComment testComment = new TestComment();
		Comment comment = testComment.getComment("This is a test sentence.");
		DecisionKnowledgeElement decisionKnowledgeElement = comment.getSentences().get(0);
		decisionKnowledgeElement.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithCommentChangedParentIdZeroParentDocumentationLocationEmpty() {
		TestComment testComment = new TestComment();
		Comment comment = testComment.getComment("This is a test sentence.");
		DecisionKnowledgeElement decisionKnowledgeElement = comment.getSentences().get(0);
		assertEquals(decisionKnowledgeElement.getType(), KnowledgeType.OTHER);

		String newText = "some fancy new text";
		decisionKnowledgeElement.setDescription(newText);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		decisionKnowledgeElement.setType(KnowledgeType.PRO);

		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "").getStatus());
		Sentence sentence = (Sentence) new JiraIssueCommentPersistenceManager("")
				.getDecisionKnowledgeElement(decisionKnowledgeElement.getId());
		assertEquals(newText, sentence.getBody());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithCommentChangedCheckValidTextWithManuallTaggedComment() {
		String newText = "some fancy new text";
		TestComment testComment = new TestComment();
		Comment comment = testComment.getComment("{issue}This is a test sentence.{Issue}");

		DecisionKnowledgeElement decisionKnowledgeElement = comment.getSentences().get(0);
		decisionKnowledgeElement.setDescription(newText);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		decisionKnowledgeElement.setType(KnowledgeType.ISSUE);
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "s").getStatus());
		Sentence sentence = (Sentence) new JiraIssueCommentPersistenceManager("")
				.getDecisionKnowledgeElement(decisionKnowledgeElement.getId());
		assertEquals(newText, sentence.getBody());

		MutableComment mutableComment = (MutableComment) ComponentAccessor.getCommentManager()
				.getCommentById(sentence.getCommentId());
		assertEquals("{Issue}some fancy new text{Issue}", mutableComment.getBody());
	}
}
