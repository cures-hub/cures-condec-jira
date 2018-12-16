package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.TestComment;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplateWebhook;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestChangeKnowledgeType extends TestKnowledgeRestSetUp {

	private final static String UPDATING_ERROR = "Knowledge type of element could not be updated due to a bad request.";

	@Test
	@NonTransactional
	public void testRequestNullElementNull() {
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("This is a test sentence.");
		decisionKnowledgeElement = comment.getSentences().get(0);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", UPDATING_ERROR)).build()
				.getEntity(), knowledgeRest.changeKnowledgeType(null, null, 0, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestNullElementFilled() {
		TestComment tc = new TestComment();
		Comment comment = tc.getComment("This is a test sentence.");
		decisionKnowledgeElement = comment.getSentences().get(0);
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", UPDATING_ERROR)).build()
						.getEntity(),
				knowledgeRest.changeKnowledgeType(null, decisionKnowledgeElement, 0, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", UPDATING_ERROR)).build()
				.getEntity(), knowledgeRest.changeKnowledgeType(request, null, 0, null).getEntity());
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
				knowledgeRest.changeKnowledgeType(request, decisionKnowledgeElement, 0, null).getStatus());
	}
}
