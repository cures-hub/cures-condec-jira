package de.uhd.ifi.se.decision.management.jira.rest.webhookrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.WebhookRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetObservedType extends TestSetUp {

	private HttpServletRequest request;
	private WebhookRest webhookRest;

	@Before
	public void setUp() {
		init();
		webhookRest = new WebhookRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestNullProjectKeyNullTypeNullActivationFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setObservedType(null, null, null, false).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyNullTypeNullActivationFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setObservedType(request, null, null, false).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyValidTypeNullActivationFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setObservedType(null, "TEST", null, false).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidTypeNullActivationFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setObservedType(request, "TEST", null, false).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidTypeBlankActivationFalse() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				webhookRest.setObservedType(request, "TEST", "", false).getStatus());
	}

	@Test
	public void testRequestValidProjectKeyValidTypeValidActivationFalse() {
		assertEquals(Response.Status.OK.getStatusCode(),
				webhookRest.setObservedType(request, "TEST", "Issue", false).getStatus());
	}
}
