package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.view.treant.TestTreant;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestTreant.AoSentenceTestDatabaseUpdater.class)
public class TestGetVis extends TestSetUpWithIssues {

	private ViewRest viewRest;
	private EntityManager entityManager;
	private FilterSettings filterSettings;
	protected HttpServletRequest request;

	private static final String INVALID_PROJECTKEY = "Decision knowledge elements cannot be shown since project key is invalid.";
	private static final String INVALID_ELEMENTS = "Visualization cannot be shown since element key is invalid.";
	private static final String INVALID_FILTER_SETTINGS = "The filter settings are null. Vis graph could not be created.";
	private static final String INVALID_REQUEST = "HttpServletRequest is null. Vis graph could not be created.";

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		request = new MockHttpServletRequest();
		String jql = "project%20%3D%20CONDEC%20AND%20assignee%20%3D%20currentUser()%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20updated%20DESC";
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("NoFails");
		filterSettings = new FilterSettingsImpl("TEST", jql, user);
	}

	@Test
	public void testRequestNullFilterDataSettingsElementNull() {
		assertEquals(
				Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", INVALID_ELEMENTS)).build().getEntity(),
				viewRest.getVis(null, null, null).getEntity());
	}

	@Test
	public void testRequestNullFilerSettingsNullElementNotExisting() {
		assertEquals(
				Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", INVALID_PROJECTKEY)).build().getEntity(),
				viewRest.getVis(null, null, "NotTEST").getEntity());
	}

	@Test
	public void testRequestNullFilterSettingsNullElementExisting() {
		assertEquals(
				Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", INVALID_FILTER_SETTINGS)).build().getEntity(),
				viewRest.getVis(null, null, "TEST-12").getEntity());
	}

	@Test
	public void testRequestNullFilterSettingsFilledElementFilled() {
		assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", INVALID_REQUEST)).build().getEntity(),
				viewRest.getVis(null, filterSettings, "TEST-12").getEntity());
	}

	// TODO
	@Test
	@Ignore
	public void testRequestFilledFilterSettingsFilledElementFilled() {
		assertEquals(Response.Status.OK.getStatusCode(),
				viewRest.getVis(request, filterSettings, "TEST-12").getStatus());
	}
}
