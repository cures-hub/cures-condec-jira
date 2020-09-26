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

public class TestIsActivated extends TestSetUp {
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
	public void testIsActivatedNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isActivated(null).getStatus());
	}

	@Test
	public void testIsActivatedProjectKeyEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isActivated("").getStatus());
	}

	@Test
	public void testIsActivatedProjectKeyFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isActivated("InvalidKey").getStatus());
	}

	@Test
	public void testIsActivatedProjectKeyOK() {
		assertEquals(Status.OK.getStatusCode(), configRest.isActivated("TEST").getStatus());
	}
}
