package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.sun.jersey.api.client.ClientResponse.Status;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.rest.impl.ConfigRestImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetKnowledgeExtractedFromIssues extends TestSetUp {

	protected HttpServletRequest request;
	protected ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRestImpl();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestNullProjectKeyNullIsKnowledgeExtractedNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(null, null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullIsKnowledgeExtractedTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(null, null, "true").getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullIsKnowledgeExtractedFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(null, null, "false").getStatus());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsKnowledgeExtractedNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(null, "TEST", null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsKnowledgeExtractedTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(null, "TEST", "true").getStatus());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsKnowledgeExtractedFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(null, "TEST", "false").getStatus());
	}

	@Test
	public void testRequestNullProjectKeyDoesNotExistIsKnowledgeExtractedNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(null, "NotTEST", null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyDoesNotExistIsKnowledgeExtractedTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(null, "NotTEST", "true").getStatus());
	}

	@Test
	public void testRequestNullProjectKeyDoesNotExistIsKnowledgeExtractedFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(null, "NotTEST", "false").getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyNullIsKnowledgeExtractedNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(request, null, null).getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyNullIsKnowledgeExtractedTrue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(request, null, "true").getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyNullIsKnowledgeExtractedFalse() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(request, null, "false").getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsKnowledgeExtractedNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(request, "TEST", null).getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsKnowledgeExtractedTrue() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(request, "TEST", "true").getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsKnowledgeExtractedFalse() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(request, "TEST", "false").getStatus());
	}

	@Test
	public void testUserUnauthorized() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.BLACK_HEAD.getApplicationUser());
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(request, "TEST", "false").getStatus());
	}

	@Test
	public void testUserNull() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", null);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(request, "TEST", "false").getStatus());
	}
}
