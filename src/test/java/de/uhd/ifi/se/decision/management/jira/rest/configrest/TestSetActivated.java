package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetActivated extends TestSetUp {
	protected HttpServletRequest request;
	protected ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestNullProjectKeyNullIsActivatedFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(null, null, false).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullIsActivatedTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(null, null, true).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsActivatedFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(null, "TEST", false).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsActivatedTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(null, "TEST", true).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyDoesNotExistIsActivatedTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(null, "NotTEST", true).getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyNullIsActivatedFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(request, null, false).getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsActivatedTrue() {
		assertEquals(Status.OK.getStatusCode(), configRest.setActivated(request, "TEST", true).getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsActivatedFalse() {
		assertEquals(Status.OK.getStatusCode(), configRest.setActivated(request, "TEST", false).getStatus());
	}

	@Test
	public void testUserNull() {
		request.setAttribute("user", null);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), configRest.setActivated(request, "TEST", false).getStatus());
	}

	@Test
	public void testUserUnauthorized() {
		request.setAttribute("user", JiraUsers.BLACK_HEAD.getApplicationUser());
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), configRest.setActivated(request, "TEST", false).getStatus());
	}
}
