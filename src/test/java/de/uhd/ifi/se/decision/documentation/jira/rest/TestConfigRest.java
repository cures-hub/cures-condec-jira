package de.uhd.ifi.se.decision.documentation.jira.rest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestConfigRest extends TestSetUp {
	private EntityManager entityManager;
	private HttpServletRequest request;
	private ConfigRest confRest;

	private static final String INVALID_PROJECTKEY = "Project key is invalid.";
	private static final String INVALID_REQUEST = "request = null";
	private static final String INVALID_STRATEGY = "isIssueStrategy = null";
	private static final String INVALID_ACTIVATION = "isActivated = null";

	@Before
	public void setUp() {
		UserManager userManager = new MockDefaultUserManager();
		confRest = new ConfigRest(userManager);
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());

		request = new MockHttpServletRequest();
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
	}

	private Response getBadRequestResponse(String errorMessage) {
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", errorMessage)).build();
	}

	// Testing setActivated
	@Test
	public void testSetActivatedRequestNullProjectKeyNullIsActivatedNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setActivated(null, null, null).getEntity());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyNullIsActivatedTrue() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setActivated(null, null, "true").getEntity());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyNullIsActivatedFalse() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setActivated(null, null, "false").getEntity());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyExistsIsActivatedNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setActivated(null, "TEST", null).getEntity());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyExistsIsActivatedTrue() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setActivated(null, "TEST", "true").getEntity());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyExistsIsActivatedFalse() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setActivated(null, "TEST", "false").getEntity());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyDoesNotExistIsActivatedNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setActivated(null, "NotTEST", null).getEntity());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyDoesNotExistIsActivatedTrue() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setActivated(null, "NotTEST", "true").getEntity());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyDoesNotExistIsActivatedFalse() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setActivated(null, "NotTEST", "false").getEntity());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyNullIsActivatedNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				confRest.setActivated(request, null, null).getEntity());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyNullIsActivatedTrue() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				confRest.setActivated(request, null, "true").getEntity());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyNullIsActivatedFalse() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				confRest.setActivated(request, null, "false").getEntity());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyExistsIsActivatedNull() {
		assertEquals(getBadRequestResponse(INVALID_ACTIVATION).getEntity(),
				confRest.setActivated(request, "TEST", null).getEntity());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyExistsIsActivatedTrue() {
		assertEquals(Response.ok().build().getClass(), confRest.setActivated(request, "TEST", "true").getClass());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyExistsIsActivatedFalse() {
		assertEquals(Response.ok().build().getClass(), confRest.setActivated(request, "TEST", "false").getClass());
	}

	@Test
	public void testSetActivatedUserUnauthorized() {
		request.setAttribute("WithFails", true);
		request.setAttribute("NoFails", false);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				confRest.setActivated(request, "NotTEST", "false").getStatus());
	}

	@Test
	public void testSetActivatedUserNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", false);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				confRest.setActivated(request, "NotTEST", "false").getStatus());
	}

	// Testing setIssueStrategy
	@Test
	public void testdoPutrequestNullKeyNullIsIssueStategydNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setIssueStrategy(null, null, null).getEntity());
	}

	@Test
	public void testdoPutrequestNullKeyNullIsIssueStategyTrue() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setIssueStrategy(null, null, "true").getEntity());
	}

	@Test
	public void testdoPutrequestNullKeyNullIsIssueStategyFalse() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setIssueStrategy(null, null, "false").getEntity());
	}

	@Test
	public void testdoPutrequestNullKeyExistsIsIssueStategyNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setIssueStrategy(null, "TEST", null).getEntity());
	}

	@Test
	public void testdoPutrequestNullKeyExistsIsIssueStategyTrue() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setIssueStrategy(null, "TEST", "true").getEntity());
	}

	@Test
	public void testdoPutrequestNullKeyExistsIsIssueStategyFalse() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setIssueStrategy(null, "TEST", "false").getEntity());
	}

	@Test
	public void testdoPutrequestNullKeyDontExistIsIssueStategyNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setIssueStrategy(null, "NotTEST", null).getEntity());
	}

	@Test
	public void testdoPutrequestNullKeyDontExistIsIssueStategyTrue() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setIssueStrategy(null, "NotTEST", "true").getEntity());
	}

	@Test
	public void testdoPutrequestNullKeyDontExistIsIssueStategyFalse() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				confRest.setIssueStrategy(null, "NotTEST", "false").getEntity());
	}

	@Test
	public void testdoPutrequestExKeyNullIsIssueStategyNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				confRest.setIssueStrategy(request, null, null).getEntity());
	}

	@Test
	public void testdoPutrequestExKeyNullIsIssueStategyTrue() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				confRest.setIssueStrategy(request, null, "true").getEntity());
	}

	@Test
	public void testdoPutrequestExKeyNullIsIssueStategyFalse() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				confRest.setIssueStrategy(request, null, "false").getEntity());
	}

	@Test
	public void testdoPutrequestExKeyExistsIsIssueStategyNull() {
		assertEquals(getBadRequestResponse(INVALID_STRATEGY).getEntity(),
				confRest.setIssueStrategy(request, "TEST", null).getEntity());
	}

	@Test
	public void testdoPutrequestExKeyExistsIsIssueStategyTrue() {
		assertEquals(Response.ok().build().getClass(), confRest.setIssueStrategy(request, "TEST", "true").getClass());
	}

	@Test
	public void testdoPutrequestExKeyExistsIsIssueStategyFalse() {
		assertEquals(Response.ok().build().getClass(), confRest.setIssueStrategy(request, "TEST", "false").getClass());
	}

	@Test
	public void testdoPutUserUnauthorized() {
		request.setAttribute("WithFails", true);
		request.setAttribute("NoFails", false);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				confRest.setIssueStrategy(request, "NotTEST", "false").getStatus());
	}

	@Test
	public void testdoPutUserNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", false);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				confRest.setIssueStrategy(request, "NotTEST", "false").getStatus());
	}
}
