package de.uhd.ifi.se.decision.documentation.jira.rest.DecisionRest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.documentation.jira.rest.DecisionsRest;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGetUnlinkedIssues extends TestSetUp {
	private EntityManager entityManager;
	private DecisionsRest decRest;

	@Before
	public void setUp() {
		decRest = new DecisionsRest();
		initialization();
		new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());
	}

	@Test
	public void testIssueIdZeroProjectKeyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error",
						"Unlinked decision components could not be received due to a bad request (element id or project key was missing)."))
				.build().getEntity(), decRest.getUnlinkedDecisionComponents(0, null).getEntity());
	}

	@Test
	public void testIssueIdFilledProjectKeyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error",
						"Unlinked decision components could not be received due to a bad request (element id or project key was missing)."))
				.build().getEntity(), decRest.getUnlinkedDecisionComponents(7, null).getEntity());
	}

	 @Test
	 (expected = java.lang.NullPointerException.class)
	 public void testIssueIdFilledProjectKeyDontExist() {
	 	assertEquals(200,decRest.getUnlinkedDecisionComponents(7,"NotTEST").getStatus());
	 }


	 @Test
	 public void testIssueIdFilledProjectKeyExist() {
	 assertEquals(Status.OK.getStatusCode(),decRest.getUnlinkedDecisionComponents(7,
	 "TEST").getStatus());
	 }
}
