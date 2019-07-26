package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

public class TestSetKnowledgeExtractedFromIssues extends TestConfigSuper {
	private static final String INVALID_EXTRACTEDISSUES = "isKnowledgeExtractedFromIssues = null";

	@Test
	public void testRequestNullProjectKeyNullIsKnowledgeExtractedNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeExtractedFromIssues(null, null, null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyNullIsKnowledgeExtractedTrue() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeExtractedFromIssues(null, null, "true").getEntity());
	}

	@Test
	public void testRequestNullProjectKeyNullIsKnowledgeExtractedFalse() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeExtractedFromIssues(null, null, "false").getEntity());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsKnowledgeExtractedNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeExtractedFromIssues(null, "TEST", null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsKnowledgeExtractedTrue() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeExtractedFromIssues(null, "TEST", "true").getEntity());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsKnowledgeExtractedFalse() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeExtractedFromIssues(null, "TEST", "false").getEntity());
	}

	@Test
	public void testRequestNullProjectKeyDoesNotExistIsKnowledgeExtractedNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeExtractedFromIssues(null, "NotTEST", null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyDoesNotExistIsKnowledgeExtractedTrue() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeExtractedFromIssues(null, "NotTEST", "true").getEntity());
	}

	@Test
	public void testRequestNullProjectKeyDoesNotExistIsKnowledgeExtractedFalse() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeExtractedFromIssues(null, "NotTEST", "false").getEntity());
	}

	@Test
	public void testRequestExistsProjectKeyNullIsKnowledgeExtractedNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.setKnowledgeExtractedFromIssues(request, null, null).getEntity());
	}

	@Test
	public void testRequestExistsProjectKeyNullIsKnowledgeExtractedTrue() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.setKnowledgeExtractedFromIssues(request, null, "true").getEntity());
	}

	@Test
	public void testRequestExistsProjectKeyNullIsKnowledgeExtractedFalse() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.setKnowledgeExtractedFromIssues(request, null, "false").getEntity());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsKnowledgeExtractedNull() {
		assertEquals(getBadRequestResponse(INVALID_EXTRACTEDISSUES).getEntity(),
				configRest.setKnowledgeExtractedFromIssues(request, "TEST", null).getEntity());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsKnowledgeExtractedTrue() {
		assertEquals(Response.ok().build().getClass(),
				configRest.setKnowledgeExtractedFromIssues(request, "TEST", "true").getClass());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsKnowledgeExtractedFalse() {
		assertEquals(Response.ok().build().getClass(),
				configRest.setKnowledgeExtractedFromIssues(request, "TEST", "false").getClass());
	}

	@Test
	public void testUserUnauthorized() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("WithFails", true);
		request.setAttribute("NoFails", false);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(request, "NotTEST", "false").getStatus());
	}

	@Test
	public void testUserNull() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", false);
		request.setAttribute("SysAdmin", false);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				configRest.setKnowledgeExtractedFromIssues(request, "NotTEST", "false").getStatus());
	}
}
