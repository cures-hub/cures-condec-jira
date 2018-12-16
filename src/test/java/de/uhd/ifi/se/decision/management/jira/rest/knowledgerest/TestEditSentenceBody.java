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
public class TestEditSentenceBody extends TestKnowledgeRestSetUp {

	private final static String CREATION_ERROR = "Element could not be updated due to a bad request.";

	@Test
	@NonTransactional
	public void testRequestNullElementNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.updateDecisionKnowledgeElement(null, null, 0, "s").getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestNullElementFilled() {
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("This is a test sentence.");
		decisionKnowledgeElement = comment.getSentences().get(0);
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
						.getEntity(),
				knowledgeRest.updateDecisionKnowledgeElement(null, decisionKnowledgeElement, 0, "s").getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.updateDecisionKnowledgeElement(request, null, 0, "s").getEntity());
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
	@Ignore
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
