package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

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
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGetLinkedElements extends TestKnowledgeRestSetUp {
	private EntityManager entityManager;
	private KnowledgeRest knowledgeRest;

	private final static String LINKED_ERRROR = "Linked decision knowledge elements could not be received due to a bad request (element id or project key was missing).";

	@Override
	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
	}

	@Test
	public void testElementIdZeroProjectKeyNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", LINKED_ERRROR))
				.build().getEntity(), knowledgeRest.getLinkedElements(0, null).getEntity());
	}

	@Test
	public void testElementIdFilledProjectKeyNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", LINKED_ERRROR))
				.build().getEntity(), knowledgeRest.getLinkedElements(7, null).getEntity());
	}

	@Test
	public void testElementIdFilledProjectKeyNonExistent() {
		assertEquals(200, knowledgeRest.getLinkedElements(7, "NotTEST").getStatus());
	}

	@Test
	public void testElementIdFilledProjectKeyExistent() {
		assertEquals(Response.Status.OK.getStatusCode(), knowledgeRest.getLinkedElements(7, "TEST").getStatus());
	}
}
