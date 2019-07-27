package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;

public class TestGetElements extends TestSetUp {
	private KnowledgeRest knowledgeRest;

	private final static String BAD_REQUEST_ERROR = "Getting elements failed due to a bad request.";

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		super.init();
	}

	@Test
	public void testNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				.build().getEntity(), knowledgeRest.getElements(false, null, null, null, null).getEntity());
	}
}
