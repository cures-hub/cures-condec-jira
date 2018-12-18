package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.MutableComment;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.TestComment;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplateWebhook;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestUpdateDecisionKnowledgeElement extends TestKnowledgeRestSetUp {
	private final static String UPDATE_ERROR = "Element could not be updated due to a bad request.";

	@Test
	public void testActionTypeNullReqNullDecNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
				.getEntity(), knowledgeRest.updateDecisionKnowledgeElement(null, null, 0, "").getEntity());
	}

	@Test
	public void testActionTypeNullReqNullDecFilled() {
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
						.getEntity(),
				knowledgeRest.updateDecisionKnowledgeElement(null, decisionKnowledgeElement, 0, "").getEntity());
	}

	@Test
	public void testActionTypeNullReqFilledDecNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
				.getEntity(), knowledgeRest.updateDecisionKnowledgeElement(request, null, 0, "").getEntity());
	}

	@Test
	public void testActionTypecreateReqNullDecNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
				.getEntity(), knowledgeRest.updateDecisionKnowledgeElement(null, null, 0, "").getEntity());
	}

	@Test
	public void testActionTypecreateReqNullDecFilled() {
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
						.getEntity(),
				knowledgeRest.updateDecisionKnowledgeElement(null, decisionKnowledgeElement, 0, "").getEntity());
	}

	@Test
	public void testActionTypecreateReqFilledDecNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
				.getEntity(), knowledgeRest.updateDecisionKnowledgeElement(request, null, 0, "").getEntity());
	}

	@Ignore
	public void testActionTypecreateReqFilledDecFilled() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.Status.OK.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "").getStatus());
	}

	@Test
	public void testActionTypeEditReqNullDecNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
				.getEntity(), knowledgeRest.updateDecisionKnowledgeElement(null, null, 0, "").getEntity());
	}

	@Test
	public void testActionTypeEditReqNullDecFilled() {
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", UPDATE_ERROR)).build()
						.getEntity(),
				knowledgeRest.updateDecisionKnowledgeElement(null, decisionKnowledgeElement, 0, "").getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestNullElementNull() {
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", UPDATE_ERROR)).build().getEntity(),
				knowledgeRest.updateDecisionKnowledgeElement(null, null, 0, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestNullElementFilled() {
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("This is a test sentence.");
		decisionKnowledgeElement = comment.getSentences().get(0);
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", UPDATE_ERROR)).build().getEntity(),
				knowledgeRest.updateDecisionKnowledgeElement(null, decisionKnowledgeElement, 0, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", UPDATE_ERROR)).build().getEntity(),
				knowledgeRest.updateDecisionKnowledgeElement(request, null, 0, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilled() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("This is a test sentence.");
		decisionKnowledgeElement = comment.getSentences().get(0);
		decisionKnowledgeElement.setType(KnowledgeType.ALTERNATIVE);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithoutCommentChangedAndKnowledgeTypeChanged() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("This is a test sentence.");
		decisionKnowledgeElement = comment.getSentences().get(0);
		decisionKnowledgeElement.setType(KnowledgeType.PRO);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "s").getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledButNotExisting() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(500, knowledgeRest.setSentenceIrrelevant(request, decisionKnowledgeElement).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithCommentChanged() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("This is a test sentence.");
		decisionKnowledgeElement = comment.getSentences().get(0);
		decisionKnowledgeElement.setDescription("some fancy new text");
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		decisionKnowledgeElement.setType(KnowledgeType.PRO);
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "s").getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithCommentChangedCheckValidText() {
		String newText = "some fancy new text";
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("This is a test sentence.");
		decisionKnowledgeElement = comment.getSentences().get(0);
		decisionKnowledgeElement.setDescription(newText);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		decisionKnowledgeElement.setType(KnowledgeType.PRO);
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "s").getStatus());
		Sentence sentence = (Sentence) ActiveObjectsManager.getElementFromAO(decisionKnowledgeElement.getId());
		assertEquals(newText, sentence.getBody());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithCommentChangedCheckValidTextWithManuallTaggedComment() {
		String newText = "some fancy new text";
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("{issue}This is a test sentence.{Issue}");
		decisionKnowledgeElement = comment.getSentences().get(0);
		decisionKnowledgeElement.setDescription(newText);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		decisionKnowledgeElement.setType(KnowledgeType.ISSUE);
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.updateDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "s").getStatus());
		Sentence sentence = (Sentence) ActiveObjectsManager.getElementFromAO(decisionKnowledgeElement.getId());
		assertEquals(newText, sentence.getBody());

		MutableComment mutableComment = (MutableComment) ComponentAccessor.getCommentManager()
				.getCommentById(sentence.getCommentId());
		assertEquals("{Issue}some fancy new text{Issue}", mutableComment.getBody());
	}
}
