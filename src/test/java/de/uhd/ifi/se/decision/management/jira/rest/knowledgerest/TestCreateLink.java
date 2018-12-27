package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestCreateLink extends TestSetUpWithIssues {
	private EntityManager entityManager;
	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;

	private final static String CREATION_ERROR = "Link could not be created due to a bad request.";

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());

		request = new MockHttpServletRequest();
	}

	@Test
	public void testRequestFilledProjectKeyFilledChildElementFilledParentElementFilled() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", "Decision", 4, "i", 1, "i").getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyFilledChildKnowledgeTypeNullParentElementFilled() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", null, 4, "i", 1, "i").getStatus());
	}

	@Test
	public void testRequestFilledProjectKeyFilledChildElementFilledParentElementFilledDocumentationLocationUnknown() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", "Decision", 4, "", 1, "").getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledProjectKeyFilledChildElementFilledParentElementFilledDocumentationLocationJiraIssueComments() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", "Decision", 4, "s", 1, "s").getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestFilledProjectKeyFilledChildElementFilledParentElementFilledDocumentationLocationDiffer() {
		assertEquals(Status.OK.getStatusCode(),
				knowledgeRest.createLink(request, "TEST", "Decision", 4, "i", 3, "s").getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullChildElementFilledParentElementFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, null, "Decision", 4, "i", 1, "i").getEntity());
	}

	@Test
	public void testRequestFilledProjectKeyNullChildElementFilledParentElementFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(request, null, "Decision", 4, "i", 1, "i").getEntity());
	}

	@Test
	public void testRequestNullProjectKeyFilledChildElementFilledParentElementFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
				.getEntity(), knowledgeRest.createLink(null, "TEST", "Decision", 4, "i", 1, "i").getEntity());
	}

	@Test
	public void testRequestFilledProjectKeyFilledChildElementIdZeroParentElementFilled() {
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
						.getEntity(),
				knowledgeRest.createLink(request, "TEST", "Decision", 0, "i", 1, "i").getEntity());
	}

	@Test
	public void testRequestFilledProjectKeyFilledChildElementFilledParentElementIdZero() {
		assertEquals(
				Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", CREATION_ERROR)).build()
						.getEntity(),
				knowledgeRest.createLink(request, "TEST", "Decision", 4, "i", 0, "i").getEntity());
	}
}
