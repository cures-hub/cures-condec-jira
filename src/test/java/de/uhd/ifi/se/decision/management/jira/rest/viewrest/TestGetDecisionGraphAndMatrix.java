package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class TestGetDecisionGraphAndMatrix extends TestSetUp {
	private ViewRest viewRest;
	protected HttpServletRequest request;
	private static final String INVALID_PROJECTKEY = "Decision knowledge elements cannot be shown since project key is invalid.";

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		init();
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		request = new MockHttpServletRequest();
		request.setAttribute("user", user);
	}

	@Test
	public void testDecisionGraphProjectKeyNull() {
		assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_PROJECTKEY))
				.build().getEntity(), viewRest.getDecisionGraph(request, null).getEntity());
	}

	@Test
	public void testDecisionGraphProjectKeyNonExistent() {
		assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_PROJECTKEY))
				.build().getEntity(), viewRest.getDecisionGraph(request, "NotTEST").getEntity());
	}

	@Test
	@NonTransactional
	public void testDecisionGraphProjectKeyExistent() {
		assertEquals(200, viewRest.getDecisionGraph(request,"TEST").getStatus());
	}

	@Test
	public void testDecisionMatrixProjectKeyNull() {
		assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_PROJECTKEY))
				.build().getEntity(), viewRest.getDecisionMatrix(request, null).getEntity());
	}

	@Test
	public void testDecisionMatrixProjectKeyNonExistent() {
		assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_PROJECTKEY))
				.build().getEntity(), viewRest.getDecisionMatrix(request, "NotTEST").getEntity());
	}

	@Test
	@NonTransactional
	public void testDecisionMatrixProjectKeyExistent() {
		assertEquals(200, viewRest.getDecisionMatrix(request,"TEST").getStatus());
	}
}
