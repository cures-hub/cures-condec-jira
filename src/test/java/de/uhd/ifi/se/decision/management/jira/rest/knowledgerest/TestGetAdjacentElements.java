package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.rest.impl.KnowledgeRestImpl;

public class TestGetAdjacentElements extends TestSetUp {
	private KnowledgeRest knowledgeRest;

	private final static String BAD_REQUEST_ERRROR = "Linked decision knowledge elements could not be received due to a bad request (element id or project key was missing).";

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRestImpl();
		init();

	}

	@Test
	public void testElementIdFilledProjectExistentDocumentationLocationEmpty() {
		assertEquals(Response.Status.OK.getStatusCode(), knowledgeRest.getAdjacentElements(7, "TEST", "").getStatus());
	}

	@Test
	public void testElementIdFilledProjectExistentDocumentationLocationJiraIssue() {
		assertEquals(Response.Status.OK.getStatusCode(), knowledgeRest.getAdjacentElements(7, "TEST", "i").getStatus());
	}

	@Test
	public void testElementIdZeroProjectExistentDocumentationLocationEmpty() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERRROR))
				.build().getEntity(), knowledgeRest.getAdjacentElements(0, "TEST", "").getEntity());
	}

	@Test
	public void testElementIdZeroProjectKeyNullDocumentationLocationEmpty() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERRROR))
				.build().getEntity(), knowledgeRest.getAdjacentElements(0, null, "").getEntity());
	}

	@Test
	public void testElementIdFilledProjectKeyNullDocumentationLocationEmpty() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERRROR))
				.build().getEntity(), knowledgeRest.getAdjacentElements(7, null, "").getEntity());
	}
}