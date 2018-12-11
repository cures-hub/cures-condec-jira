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
public class TestCreateDecisionKnowledeElementInComment extends TestKnowledgeRestSetUp {

	private final static String CREATION_ERROR = "Creation of decision knowledge element failed.";

	@Test
	public void testRequestNullElementNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createDecisionKnowledgeElement(null, null).getEntity());
	}

	@Test
	public void testRequestNullElementFilled() {
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
						.getEntity(),
				knowledgeRest.createDecisionKnowledgeElement(null, decisionKnowledgeElement).getEntity());
	}

	@Test
	public void testRequestFilledElementNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createDecisionKnowledgeElement(request, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilled() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithArgument() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		decisionKnowledgeElement.setType("Pro-argument");
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledElementFilledWithConArgument() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		ComponentGetter.setTransactionTemplate(new MockTransactionTemplateWebhook());
		decisionKnowledgeElement.setType("Con-argument");
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement).getStatus());
	}
}
