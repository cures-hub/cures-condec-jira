package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestCreateDecisionKnowledgeElement extends TestKnowledgeRestSetUp {

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

	@Ignore
	public void testRequestFilledElementFilled() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createDecisionKnowledgeElement(request, decisionKnowledgeElement).getStatus());
	}
}
