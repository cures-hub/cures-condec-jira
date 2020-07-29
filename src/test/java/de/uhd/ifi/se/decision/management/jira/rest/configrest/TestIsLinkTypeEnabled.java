package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class TestIsLinkTypeEnabled extends TestConfigSuper {
	@Test
	public void testProjectKyNullKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
			configRest.isLinkTypeEnabled(null, null).getEntity());
	}

	@Test
	public void testProjectKeyEmptyKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
			configRest.isLinkTypeEnabled("", null).getEntity());
	}

	@Test
	public void testProjectKeyFalseKnowledgeTypeNull() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
			configRest.isLinkTypeEnabled("InvalidKey", null).getStatus());
	}

	@Test
	public void testProjectKyNullKnowledgeTypeEmpty() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
			configRest.isLinkTypeEnabled(null, "").getEntity());
	}

	@Test
	public void testProjectKeyEmptyKnowledgeTypeEmpty() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
			configRest.isLinkTypeEnabled("", "").getEntity());
	}

	@Test
	public void testProjectKeyFalseKnowledgeTypeEmpty() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
			configRest.isLinkTypeEnabled("InvalidKey", "").getStatus());
	}

	@Test
	public void testProjectKyNullKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
			configRest.isLinkTypeEnabled(null, KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testProjectKeyEmptyKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
			configRest.isLinkTypeEnabled("", KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testProjectKeyFalseKnowledgeTypeFilled() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
			configRest.isLinkTypeEnabled("InvalidKey", KnowledgeType.SOLUTION.toString()).getStatus());
	}

	@Test
	public void testIsIssueStrategyProjectKeyOK() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
			configRest.isLinkTypeEnabled("TEST", KnowledgeType.SOLUTION.toString()).getStatus());
	}
}
