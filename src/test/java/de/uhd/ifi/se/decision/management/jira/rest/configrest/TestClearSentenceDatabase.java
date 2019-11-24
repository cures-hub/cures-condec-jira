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

public class TestClearSentenceDatabase extends TestSetUp {

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
	public void testRequestNullProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.clearSentenceDatabase(null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyValid() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.clearSentenceDatabase(null, "TEST").getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyExists() {
		assertEquals(Status.OK.getStatusCode(), configRest.clearSentenceDatabase(request, "TEST").getStatus());
	}

	@Test
	public void testUserUnauthorized() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.BLACK_HEAD.getApplicationUser());
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				configRest.clearSentenceDatabase(request, "TEST").getStatus());
	}

	@Test
	public void testUserNull() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", null);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				configRest.clearSentenceDatabase(request, "TEST").getStatus());
	}
}
