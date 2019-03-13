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
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestGetDecisionKnowledgeElement extends TestSetUpWithIssues {
	private EntityManager entityManager;
	private KnowledgeRest knowledgeRest;

	private final static String BAD_REQUEST_ERROR = "Decision knowledge element could not be received due to a bad request (element id or project key was missing).";

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		super.initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
	}

	@Test
	public void testElementIdFilledProjectExistentDocumentationLocationNull() {
		assertEquals(Status.OK.getStatusCode(), knowledgeRest.getDecisionKnowledgeElement(7, "TEST", null).getStatus());
	}

	@Test
	public void testElementIdZeroProjectKeyNullDocumentationLocationNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				.build().getEntity(), knowledgeRest.getDecisionKnowledgeElement(0, null, null).getEntity());
	}

	@Test
	public void testElementIdZeroProjectExistentDocumentationLocationNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				.build().getEntity(), knowledgeRest.getDecisionKnowledgeElement(0, "TEST", null).getEntity());
	}

	@Test
	public void testElementIdFilledProjectKeyNullDocumentationLocationNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				.build().getEntity(), knowledgeRest.getDecisionKnowledgeElement(7, null, null).getEntity());
	}

	@Test
	public void testElementNotExistentProjectExistentDocumentationLocationJiraIssue() {
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.getDecisionKnowledgeElement(300, "TEST", "i").getStatus());
	}

	@Test
	public void testElementNotExistentProjectExistentDocumentationLocationJiraIssueComment() {
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				knowledgeRest.getDecisionKnowledgeElement(7, "TEST", "s").getStatus());
	}
}
