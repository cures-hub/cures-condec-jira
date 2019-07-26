package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

public class TestSetKnowledgeExtractedFromGit extends TestConfigSuper {
	@Test
	public void testSetKnowledgeExtractedNullNullNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(null, null, null).getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedNullFilledNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(null, "TEST", null).getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedNullNullFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(null, null, "false").getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedFilledNullNull() {
		request.setAttribute("user", null);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(request, null, null).getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedFilledFilledNull() {
		request.setAttribute("user", null);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(request, "TEST", null).getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedFilledNoFailsFilledNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(request, "TEST", null).getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedNullFilledFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(null, "TEST", "false").getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedFilledFilledFilled() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", null);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(request, "TEST", "false").getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedFilledWithFailsFilledFilled() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", null);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(request, "TEST", "false").getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedFilledNoFailsFilledFilled() {
		assertEquals(Response.Status.OK.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(request, "TEST", "false").getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedFilledNoFailsFilledInvalid() {
		assertEquals(Response.Status.OK.getStatusCode(),
				configRest.setKnowledgeExtractedFromGit(request, "TEST", "testNotABoolean").getStatus());
	}
}
