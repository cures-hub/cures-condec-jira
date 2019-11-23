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
	public void testSetActivatedRequestNullProjectKeyNullIsActivatedNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(null, null, null).getStatus());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyNullIsActivatedTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(null, null, "true").getStatus());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyNullIsActivatedFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(null, null, "false").getStatus());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyExistsIsActivatedNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(null, "TEST", null).getStatus());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyExistsIsActivatedTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(null, "TEST", "true").getStatus());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyExistsIsActivatedFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(null, "TEST", "false").getStatus());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyDoesNotExistIsActivatedNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(null, "NotTEST", null).getStatus());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyDoesNotExistIsActivatedTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(null, "NotTEST", "true").getStatus());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyDoesNotExistIsActivatedFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(null, "NotTEST", "false").getStatus());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyNullIsActivatedNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(request, null, null).getStatus());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyNullIsActivatedTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(request, null, "true").getStatus());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyNullIsActivatedFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(request, null, "false").getStatus());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyExistsIsActivatedNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.setActivated(request, "TEST", null).getStatus());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyExistsIsActivatedTrue() {
		assertEquals(Status.OK.getStatusCode(), configRest.setActivated(request, "TEST", "true").getStatus());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyExistsIsActivatedFalse() {
		assertEquals(Status.OK.getStatusCode(), configRest.setActivated(request, "TEST", "false").getStatus());
	}

	@Test
	public void testSetActivatedUserNull() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", null);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				configRest.setActivated(request, "NotTEST", "false").getStatus());
	}

	@Test
	public void testSetActivatedUserUnauthorized() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.BLACK_HEAD.getApplicationUser());
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				configRest.setActivated(request, "NotTEST", "false").getStatus());
	}
}
