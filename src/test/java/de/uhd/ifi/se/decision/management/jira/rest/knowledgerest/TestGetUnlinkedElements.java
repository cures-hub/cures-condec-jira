package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGetUnlinkedElements extends TestSetUpWithIssues {
	private EntityManager entityManager;
	private KnowledgeRest knowledgeRest;

	private final static String UNLINKED_ERRROR = "Unlinked decision knowledge elements could not be received due to a bad request (element id or project key was missing).";

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
	}

	@Test
	public void testElementIdZeroProjectKeyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", UNLINKED_ERRROR)).build()
				.getEntity(), knowledgeRest.getUnlinkedElements(0, null).getEntity());
	}

	@Test
	public void testElementIdFilledProjectKeyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", UNLINKED_ERRROR)).build()
				.getEntity(), knowledgeRest.getUnlinkedElements(7, null).getEntity());
	}

	@Test
	public void testIssueIdFilledProjectKeyDontExist() {
		assertEquals(Status.OK.getStatusCode(), knowledgeRest.getUnlinkedElements(7, "NotTEST").getStatus());
	}

	@Test
	public void testIssueIdFilledProjectKeyExist() {
		assertEquals(Status.OK.getStatusCode(), knowledgeRest.getUnlinkedElements(7, "TEST").getStatus());
	}
}
