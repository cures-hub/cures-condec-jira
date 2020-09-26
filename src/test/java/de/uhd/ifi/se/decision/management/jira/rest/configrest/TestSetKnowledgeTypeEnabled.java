package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetKnowledgeTypeEnabled extends TestSetUp {

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
	public void testRequestNullProjectKeyNullIsActivatedNullKnowledgeTypeNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeTypeEnabled(null, null, false, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullIsActivatedTrueNullKnowledgeTypeNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeTypeEnabled(null, null, true, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsActivatedFalseKnowledgeTypeNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeTypeEnabled(null, "TEST", false, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyDoesNotExistIsActivatedFalseKnowledgeTypeNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeTypeEnabled(null, "NotTEST", false, null).getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsActivatedFalseKnowledgeTypeNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeTypeEnabled(request, "TEST", false, null).getStatus());
	}

	@Test
	public void testUserUnauthorized() {
		request.setAttribute("user", null);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				configRest.setKnowledgeTypeEnabled(request, "NotTEST", false, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullIsActivatedTrueKnowledgeTypeFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeTypeEnabled(null, null, true, KnowledgeType.SOLUTION.toString()).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyDoesNotExistIsActivatedTrueKnowledgeTypeFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), configRest
				.setKnowledgeTypeEnabled(null, "NotTEST", true, KnowledgeType.SOLUTION.toString()).getStatus());
	}

	@Test
	public void tesRequestExistsProjectKeyExistsIsActivatedTrueKnowledgeTypeFilled() {
		assertEquals(Response.Status.OK.getStatusCode(), configRest
				.setKnowledgeTypeEnabled(request, "TEST", true, KnowledgeType.SOLUTION.toString()).getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsActivatedFalseKnowledgeTypeFilled() {
		assertEquals(Response.Status.OK.getStatusCode(), configRest
				.setKnowledgeTypeEnabled(request, "TEST", false, KnowledgeType.SOLUTION.toString()).getStatus());
	}
}
