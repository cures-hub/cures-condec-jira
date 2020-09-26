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

public class TestIsKnowledgeTypeEnabled extends TestSetUp {
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
	public void testProjectKeyNullKnowledgeTypeNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isKnowledgeTypeEnabled(null, null).getStatus());
	}

	@Test
	public void testProjectKeyEmptyKnowledgeTypeNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isKnowledgeTypeEnabled("", null).getStatus());
	}

	@Test
	public void testProjectKeyValidKnowledgeTypeNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isKnowledgeTypeEnabled("TEST", null).getStatus());
	}

	@Test
	public void testProjectKeyNullKnowledgeTypeEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isKnowledgeTypeEnabled(null, "").getStatus());
	}

	@Test
	public void testProjectKeyEmptyKnowledgeTypeEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isKnowledgeTypeEnabled("", "").getStatus());
	}

	@Test
	public void testProjectKeyInvalidKnowledgeTypeEmpty() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.isKnowledgeTypeEnabled("InvalidKey", "").getStatus());
	}

	@Test
	public void testProjectKeyNullKnowledgeTypeFilled() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.isKnowledgeTypeEnabled(null, KnowledgeType.SOLUTION.toString()).getStatus());
	}

	@Test
	public void testProjectKeyInvalidKnowledgeTypeFilled() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.isKnowledgeTypeEnabled("InvalidKey", KnowledgeType.SOLUTION.toString()).getStatus());
	}

	@Test
	public void testProjectKeyValidKnowledgeTypeFilled() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.isKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString()).getStatus());
	}
}
