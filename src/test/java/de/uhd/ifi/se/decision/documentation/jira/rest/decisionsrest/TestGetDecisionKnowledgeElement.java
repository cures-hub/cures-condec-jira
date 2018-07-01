package de.uhd.ifi.se.decision.documentation.jira.rest.decisionsrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionsRest;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGetDecisionKnowledgeElement extends TestDecisionSetUp {
	private EntityManager entityManager;
	private DecisionsRest decRest;

	private final static String ERROR_MISSING_KEY_ID = "Decision knowledge element could not be received due to a bad request (element id or project key was missing).";

	@Before
	public void setUp() {
		decRest = new DecisionsRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
	}

	@Test
	public void testIssueIdZeroProjectKeyNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", ERROR_MISSING_KEY_ID))
				.build().getEntity(), decRest.getDecisionKnowledgeElement(0, null).getEntity());
	}

	@Test
	public void testIssueIdFilledProjectKeyNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", ERROR_MISSING_KEY_ID))
				.build().getEntity(), decRest.getDecisionKnowledgeElement(7, null).getEntity());
	}

	@Test
	public void testIssueIdFilledProjectKeyDontExist() {
		assertEquals(200, decRest.getDecisionKnowledgeElement(7, "NotTEST").getStatus());
	}

	@Test
	public void testIssueIdFilledProjectKeyExist() {
		assertEquals(Response.Status.OK.getStatusCode(), decRest.getDecisionKnowledgeElement(7, "TEST").getStatus());
	}
}
