package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestIsKnowledgeTypeEnabled extends TestConfigSuper {
	@Test
	public void testProjectKyNullKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.isKnowledgeTypeEnabled(null, null).getEntity());
	}

	@Test
	public void testProjectKeyEmptyKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.isKnowledgeTypeEnabled("", null).getEntity());
	}

	@Test
	public void testProjectKeyFalseKnowledgeTypeNull() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				configRest.isKnowledgeTypeEnabled("InvalidKey", null).getStatus());
	}

	@Test
	public void testProjectKyNullKnowledgeTypeEmpty() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.isKnowledgeTypeEnabled(null, "").getEntity());
	}

	@Test
	public void testProjectKeyEmptyKnowledgeTypeEmpty() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.isKnowledgeTypeEnabled("", "").getEntity());
	}

	@Test
	public void testProjectKeyFalseKnowledgeTypeEmpty() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				configRest.isKnowledgeTypeEnabled("InvalidKey", "").getStatus());
	}

	@Test
	public void testProjectKyNullKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.isKnowledgeTypeEnabled(null, KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testProjectKeyEmptyKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.isKnowledgeTypeEnabled("", KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testProjectKeyFalseKnowledgeTypeFilled() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				configRest.isKnowledgeTypeEnabled("InvalidKey", KnowledgeType.SOLUTION.toString()).getStatus());
	}

	@Test
	public void testIsIssueStrategyProjectKeyOK() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				configRest.isKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString()).getStatus());
	}
}
