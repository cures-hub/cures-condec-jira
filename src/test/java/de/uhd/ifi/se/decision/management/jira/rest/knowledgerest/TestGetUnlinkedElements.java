package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.rest.impl.KnowledgeRestImpl;

public class TestGetUnlinkedElements extends TestSetUp {
	private KnowledgeRest knowledgeRest;

	private final static String BAD_REQUEST_ERRROR = "Unlinked decision knowledge elements could not be received due to a bad request (element id or project key was missing).";

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRestImpl();
		init();
	}

	@Test
	@Ignore
	public void testElementIdFilledProjectExistentDocumentationLocationEmpty() {
		assertEquals(Response.Status.OK.getStatusCode(), knowledgeRest.getUnlinkedElements(7, "TEST", "").getStatus());
	}

	@Test
	public void testElementIdFilledProjectExistentDocumentationLocationJiraIssue() {
		assertEquals(Response.Status.OK.getStatusCode(), knowledgeRest.getUnlinkedElements(7, "TEST", "i").getStatus());
	}

	@Test
	public void testElementIdZeroProjectExistentDocumentationLocationEmpty() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERRROR))
				.build().getEntity(), knowledgeRest.getUnlinkedElements(0, "TEST", "").getEntity());
	}

	@Test
	public void testElementIdZeroProjectKeyNullDocumentationLocationEmpty() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERRROR))
				.build().getEntity(), knowledgeRest.getUnlinkedElements(0, null, "").getEntity());
	}

	@Test
	public void testElementIdFilledProjectKeyNullDocumentationLocationEmpty() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERRROR))
				.build().getEntity(), knowledgeRest.getUnlinkedElements(7, null, "").getEntity());
	}
}
