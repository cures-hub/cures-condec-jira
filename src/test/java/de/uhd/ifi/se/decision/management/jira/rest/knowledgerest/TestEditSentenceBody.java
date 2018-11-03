package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplateWebhook;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestEditSentenceBody extends TestKnowledgeRestSetUp {

	private final static String CREATION_ERROR = "Update of decision knowledge element failed.";
	
	

	@Test
	@NonTransactional
	public void testRequestNullElementNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.editSentenceBody(null, null, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestNullElementFilled() {
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("this is atest sentence");
		decisionKnowledgeElement = comment.getSentences().get(0);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.editSentenceBody(null, decisionKnowledgeElement, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.editSentenceBody(request, null, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithoutCommentChanedAndKtChande() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("this is atest sentence");
		decisionKnowledgeElement = comment.getSentences().get(0);
		decisionKnowledgeElement.setType(KnowledgeType.ALTERNATIVE);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.editSentenceBody(request, decisionKnowledgeElement, "pro").getStatus());
	}
	
	
	@Test
	@NonTransactional
	public void testRequestF€illedElementFilledButNotExisting() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(500, knowledgeRest.setSentenceIrrelevant(request, decisionKnowledgeElement).getStatus());
	}
	
	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithCommentChaned() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("this is atest sentence");
		decisionKnowledgeElement = comment.getSentences().get(0);
		decisionKnowledgeElement.setDescription("some fancy new text");
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.editSentenceBody(request, decisionKnowledgeElement, "pro").getStatus());
	}
	
	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithCommentChanedCheckValidText() {
		String newText = "some fancy new text";
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("this is atest sentence");
		decisionKnowledgeElement = comment.getSentences().get(0);
		decisionKnowledgeElement.setDescription(newText);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.editSentenceBody(request, decisionKnowledgeElement, "pro").getStatus());
		Sentence sentence = new SentenceImpl(ActiveObjectsManager.getElementFromAO(decisionKnowledgeElement.getId()));
		assertEquals(newText, sentence.getBody());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithCommentChanedCheckValidTextWithManuallTaggedComment() {
		String newText = "some fancy new text";
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("[issue]this is atest sentence[/Issue]");
		decisionKnowledgeElement = comment.getSentences().get(0);
		decisionKnowledgeElement.setDescription(newText);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.editSentenceBody(request, decisionKnowledgeElement, "pro").getStatus());
		Sentence sentence = new SentenceImpl(ActiveObjectsManager.getElementFromAO(decisionKnowledgeElement.getId()));
		assertEquals(newText, sentence.getBody());
		
		MutableComment mc = (MutableComment) ComponentAccessor.getCommentManager().getCommentById(sentence.getCommentId());
		assertEquals("[Issue]some fancy new text[/Issue]",mc.getBody());
		
	}
}
