package de.uhd.ifi.se.decision.management.jira.rest.decisionsrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestPostDecision extends TestDecisionSetUp {

	private final static String CREATION_ERROR = "Creation of decision knowledge element failed.";
	@Test
	public void testActionTypeNullReqNullDecNull() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decisionsRest.createDecisionKnowledgeElement(null, null).getEntity());
	}

	@Test
	public void testActionTypeNullReqNullDecFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decisionsRest.createDecisionKnowledgeElement(null, dec).getEntity());
	}

	@Test
	public void testActionTypeNullReqFilledDecNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decisionsRest.createDecisionKnowledgeElement( request, null).getEntity());
	}

	@Test
	public void testActionTypecreateReqNullDecNull() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decisionsRest.createDecisionKnowledgeElement(null, null).getEntity());
	}

	@Test
	public void testActionTypecreateReqNullDecFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decisionsRest.createDecisionKnowledgeElement( null, dec).getEntity());
	}

	@Test
	public void testActionTypecreateReqFilledDecNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decisionsRest.createDecisionKnowledgeElement( request, null).getEntity());
	}

	@Test
	public void testActionTypecreateReqFilledDecFilled() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(Status.OK.getStatusCode(), decisionsRest.createDecisionKnowledgeElement( request, dec).getStatus());
	}



	@Test
	public void testActionTypeEditReqNullDecNull() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decisionsRest.createDecisionKnowledgeElement(null, null).getEntity());
	}

	@Test
	public void testActionTypeEditReqNullDecFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decisionsRest.createDecisionKnowledgeElement(null, dec).getEntity());
	}
}
