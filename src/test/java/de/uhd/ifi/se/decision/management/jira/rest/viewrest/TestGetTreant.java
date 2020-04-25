package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.ofbiz.core.entity.GenericEntityException;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetTreant extends TestSetUp {

	private ViewRest viewRest;

	@Before
	public void setUp() {
		init();
		viewRest = new ViewRest();
	}

	@Test
	public void testElementKeyNullDepthNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreant(null, null, "", null).getStatus());
	}

	@Test
	public void testElementNotExistsDepthNull() throws GenericEntityException {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreant(null, "NotTEST", null, "").getStatus());
	}

	@Test
	public void testElementNotExistsDepthFilled() throws GenericEntityException {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreant(null, "NotTEST", "3", "").getStatus());
	}

	@Test
	public void testElementExistsDepthNaN() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), viewRest.getTreant(null, "TEST-12", "test", "").getStatus());
	}

	@Test
	public void testElemetExistsDepthNumber() {
		HttpServletRequest request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		assertEquals(Status.OK.getStatusCode(), viewRest.getTreant(request, "TEST-12", "3", "").getStatus());
	}
}
