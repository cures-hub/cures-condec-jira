package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplateWebhook;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestCreateDecisionKnowledgeElement extends TestKnowledgeRestSetUp {

	private final static String CREATION_ERROR = "Creation of decision knowledge element failed.";

	@Test
	public void testRequestNullElementNullParentIdZeroParentDocumentationLocationNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createDecisionKnowledgeElement(null, null, 0, null).getEntity());
	}

	@Test
	public void testRequestNullElementFilledParentIdZeroParentDocumentationLocationNull() {
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
						.getEntity(),
				knowledgeRest.createDecisionKnowledgeElement(null, decisionKnowledgeElement, 0, null).getEntity());
	}

	@Test
	public void testRequestFilledElementNullParentIdZeroParentDocumentationLocationNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createDecisionKnowledgeElement(request, null, 0, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledParentIdZeroParentDocumentationLocationNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null).getStatus());
	}
	
	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithProArgumentParentIdZeroParentDocumentationLocationNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		decisionKnowledgeElement.setType("Pro-argument");
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithConArgumentParentIdZeroParentDocumentationLocationNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		decisionKnowledgeElement.setType("Con-argument");
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, null).getStatus());
	}
	
	@Test
	@NonTransactional
	public void testRequestFilledElementFilledParentIdFilledParentDocumentationLocationFilled() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 2, "i").getStatus());
	}
	
	@Test
	@NonTransactional
	public void testRequestFilledElementFilledParentIdZeroParentDocumentationLocationJiraIssue() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 0, "i").getStatus());
	}
	
	@Test
	@NonTransactional
	public void testRequestFilledElementFilledParentIdFilledParentDocumentationLocationEmpty() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 2, "").getStatus());
	}
	
	@Test
	@NonTransactional
	public void testRequestFilledElementFilledParentIdFilledParentDocumentationLocationNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 2, null).getStatus());
	}
	
	@Test
	@NonTransactional
	public void testRequestFilledElementFilledParentIdFilledParentDocumentationLocationJiraIssueComment() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement, 2, "s").getStatus());
	}
}
