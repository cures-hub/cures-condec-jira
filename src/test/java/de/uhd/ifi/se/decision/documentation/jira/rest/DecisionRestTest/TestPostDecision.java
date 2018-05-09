package de.uhd.ifi.se.decision.documentation.jira.rest.DecisionRestTest;

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
				.getEntity(), decRest.createDecisionKnowledgeElement(null, null).getEntity());
	}

	@Test
	public void testActionTypeNullReqNullDecFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decRest.createDecisionKnowledgeElement(null, dec).getEntity());
	}

	@Test
	public void testActionTypeNullReqFilledDecNull() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decRest.createDecisionKnowledgeElement( req, null).getEntity());
	}

	@Test
	public void testActionTypecreateReqNullDecNull() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decRest.createDecisionKnowledgeElement(null, null).getEntity());
	}

	@Test
	public void testActionTypecreateReqNullDecFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decRest.createDecisionKnowledgeElement( null, dec).getEntity());
	}

	@Test
	public void testActionTypecreateReqFilledDecNull() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decRest.createDecisionKnowledgeElement( req, null).getEntity());
	}

	@Test
	public void testActionTypecreateReqFilledDecFilled() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Status.OK.getStatusCode(), decRest.createDecisionKnowledgeElement( req, dec).getStatus());
	}



	@Test
	public void testActionTypeEditReqNullDecNull() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decRest.createDecisionKnowledgeElement(null, null).getEntity());
	}

	@Test
	public void testActionTypeEditReqNullDecFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), decRest.createDecisionKnowledgeElement(null, dec).getEntity());
	}
}
