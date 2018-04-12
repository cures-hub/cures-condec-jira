package de.uhd.ifi.se.decision.documentation.jira.persistence.DecisionRest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestPostDecision extends TestDecisionSetUp {

	@Test
	public void testActionTypeNullReqNullDecNull() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build()
				.getEntity(), decRest.insertDecisionKnowledgeElement(null, null).getEntity());
	}

	@Test
	public void testActionTypeNullReqNullDecFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build()
				.getEntity(), decRest.insertDecisionKnowledgeElement(null, dec).getEntity());
	}

	@Test
	public void testActionTypeNullReqFilledDecNull() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build()
				.getEntity(), decRest.insertDecisionKnowledgeElement( req, null).getEntity());
	}

	@Test
	public void testActionTypecreateReqNullDecNull() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build()
				.getEntity(), decRest.insertDecisionKnowledgeElement(null, null).getEntity());
	}

	@Test
	public void testActionTypecreateReqNullDecFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build()
				.getEntity(), decRest.insertDecisionKnowledgeElement( null, dec).getEntity());
	}

	@Test
	public void testActionTypecreateReqFilledDecNull() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build()
				.getEntity(), decRest.insertDecisionKnowledgeElement( req, null).getEntity());
	}

	@Test
	public void testActionTypecreateReqFilledDecFilled() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Status.OK.getStatusCode(), decRest.insertDecisionKnowledgeElement( req, dec).getStatus());
	}



	@Test
	public void testActionTypeEditReqNullDecNull() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build()
				.getEntity(), decRest.insertDecisionKnowledgeElement(null, null).getEntity());
	}

	@Test
	public void testActionTypeEditReqNullDecFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build()
				.getEntity(), decRest.insertDecisionKnowledgeElement(null, dec).getEntity());
	}
}
