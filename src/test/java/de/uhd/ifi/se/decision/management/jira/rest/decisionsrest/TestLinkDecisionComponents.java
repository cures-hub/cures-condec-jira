package de.uhd.ifi.se.decision.management.jira.rest.decisionsrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionsRest;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestLinkDecisionComponents extends TestDecisionSetUp {
	private EntityManager entityManager;
	private DecisionsRest decRest;

	private final static String UNLINKED_ERRROR = "Unlinked decision components could not be received due to a bad request (element id or project key was missing).";

	@Override
	@Before
	public void setUp() {
		decRest = new DecisionsRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
	}

	@Test
	public void testIssueIdZeroProjectKeyNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", UNLINKED_ERRROR))
				.build().getEntity(), decRest.getLinkedDecisionComponents(0, null).getEntity());
	}

	@Test
	public void testIssueIdFilledProjectKeyNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", UNLINKED_ERRROR))
				.build().getEntity(), decRest.getLinkedDecisionComponents(7, null).getEntity());
	}

	@Test
	public void testIssueIdFilledProjectKeyDontExist() {
		assertEquals(200, decRest.getLinkedDecisionComponents(7, "NotTEST").getStatus());
	}

	@Test
	public void testIssueIdFilledProjectKeyExist() {
		assertEquals(Response.Status.OK.getStatusCode(), decRest.getLinkedDecisionComponents(7, "TEST").getStatus());
	}
}
