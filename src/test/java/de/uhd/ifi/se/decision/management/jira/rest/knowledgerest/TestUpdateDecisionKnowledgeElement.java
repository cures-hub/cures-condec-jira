package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
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
}
