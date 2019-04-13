package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ofbiz.core.entity.GenericEntityException;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.view.treant.TestTreant;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestTreant.AoSentenceTestDatabaseUpdater.class)
public class TestGetTreant extends TestSetUpWithIssues {
	private EntityManager entityManager;

	protected HttpServletRequest request;

	private ViewRest viewRest;

	private static final String INVALID_PROJECTKEY = "Decision knowledge elements cannot be shown since project key is invalid.";
	private static final String INVALID_ELEMETNS = "Treant cannot be shown since element key is invalid.";
	private static final String INVALID_DEPTH = "Treant cannot be shown since depth of Tree is NaN";

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		request = new MockHttpServletRequest();
	}

	@Test
	public void testElementKeyNullDepthNull() {
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_ELEMETNS))
				.build().getEntity(), viewRest.getTreant(null, null, "", null).getEntity());
	}

	@Test
	public void testElementNotExistsDepthNull() throws GenericEntityException {
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_PROJECTKEY))
				.build().getEntity(), viewRest.getTreant("NotTEST", null, "", null).getEntity());
	}

	@Test
	public void testElementNotExistsDepthFilled() throws GenericEntityException {
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_PROJECTKEY))
				.build().getEntity(), viewRest.getTreant("NotTEST", "3", "", null).getEntity());
	}

	@Test
	public void testElementExistsDepthNaN() {
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_DEPTH))
				.build().getEntity(), viewRest.getTreant("TEST-12", "test", "", null).getEntity());
	}

	@Test
	@NonTransactional
	public void testElemetExistsDepthNumber() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
		assertEquals(200, viewRest.getTreant("TEST-12", "3", "", request).getStatus());
	}

	// @Test
	// @NonTransactional
	// public void testElementExistsDepthNumberSearchNotEmpty() {
	// request.setAttribute("WithFails", false);
	// request.setAttribute("NoFails", true);
	// assertEquals(200,viewRest.getTreant("TEST-12", "3", "?jql= type!=null",
	// request).getStatus());
	// }

	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater {
		@SuppressWarnings("unchecked")
		@Override
		public void update(EntityManager entityManager) throws Exception {
			entityManager.migrate(PartOfJiraIssueTextInDatabase.class);
			entityManager.migrate(LinkInDatabase.class);
		}
	}

}
