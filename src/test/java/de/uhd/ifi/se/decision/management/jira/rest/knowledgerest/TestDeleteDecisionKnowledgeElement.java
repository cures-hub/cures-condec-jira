package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestDeleteDecisionKnowledgeElement extends TestKnowledgeRestSetUp {

	private final static String DELETION_ERROR = "Deletion of decision knowledge element failed.";

	@Ignore
	public void testRequestFilledElementFilled() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.Status.OK.getStatusCode(),
				knowledgeRest.deleteDecisionKnowledgeElement(request, decisionKnowledgeElement).getStatus());
	}

	@Test
	public void testRequestErrorElementFilled() {
		request.setAttribute("WithFails", true);
		request.setAttribute("NoFails", false);
		assertEquals(
				Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", DELETION_ERROR))
						.build().getEntity(),
				knowledgeRest.deleteDecisionKnowledgeElement(request, decisionKnowledgeElement).getEntity());
	}

	@Test
	public void testRequestNullElementNull() {
		assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", DELETION_ERROR)).build().getEntity(),
				knowledgeRest.deleteDecisionKnowledgeElement(null, null).getEntity());
	}
}
