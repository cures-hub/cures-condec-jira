package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;

public class TestGetDecisionKnowledgeTypes extends TestSetUp {

	protected ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
	}

	@Test
	public void testProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.getDecisionKnowledgeTypes(null).getStatus());
	}

	@Test
	public void testProjectKeyEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.getDecisionKnowledgeTypes("").getStatus());
	}

	@Test
	public void testProjectKeyInvalid() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.getDecisionKnowledgeTypes("InvalidKey").getStatus());
	}

	@Test
	public void testProjectKeyValid() {
		Response response = configRest.getDecisionKnowledgeTypes("TEST");
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		// assertEquals("[Alternative, Assumption, Assessment, Argument, Pro, Con,
		// Claim, "
		// + "Context, Constraint, Decision, Goal, Issue, Implication, Problem,
		// Rationale, Solution, Other, Question]",
		// response.getEntity().toString());
	}
}
