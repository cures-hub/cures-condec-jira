package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestIsLinkTypeEnabled extends TestSetUp {
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
	public void testProjectKeyNullLinkTypeNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isLinkTypeEnabled(null, null).getStatus());
	}

	@Test
	public void testProjectKeyEmptyLinkTypeNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isLinkTypeEnabled("", null).getStatus());
	}

	@Test
	public void testProjectKeyInvalidLinkTypeNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isLinkTypeEnabled("InvalidKey", null).getStatus());
	}

	@Test
	public void testProjectKeyNullLinkTypeEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isLinkTypeEnabled(null, "").getStatus());
	}

	@Test
	public void testProjectKeyEmptyLinkTypeEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isLinkTypeEnabled("", "").getStatus());
	}

	@Test
	public void testProjectKeyInvalidLinkTypeFilled() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.isLinkTypeEnabled("InvalidKey", KnowledgeType.SOLUTION.toString()).getStatus());
	}

	@Test
	public void testProjectKeyValidLinkTypeFilled() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.isLinkTypeEnabled("TEST", KnowledgeType.SOLUTION.toString()).getStatus());
	}
}
