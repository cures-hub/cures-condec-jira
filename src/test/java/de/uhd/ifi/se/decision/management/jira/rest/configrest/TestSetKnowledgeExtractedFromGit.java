package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestSetKnowledgeExtractedFromGit extends TestConfigSuper {
	@Test
	public void testSetKnowledgeExtractedNullNullNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setKnowledgeExtractedFromGit(null, null, null).getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedNullFilledNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setKnowledgeExtractedFromGit(null, "TEST", null).getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedNullNullFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setKnowledgeExtractedFromGit(null, null, "false").getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedFilledNullNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", false);
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setKnowledgeExtractedFromGit(request, null, null).getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedFilledFilledNull() {
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", false);
		request.setAttribute("SysAdmin", false);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				confRest.setKnowledgeExtractedFromGit(request, "TEST", null).getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedFilledNoFailsFilledNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setKnowledgeExtractedFromGit(request, "TEST", null).getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedNullFilledFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setKnowledgeExtractedFromGit(null, "TEST", "false").getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedFilledFilledFilled() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", false);
		request.setAttribute("SysAdmin", false);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				confRest.setKnowledgeExtractedFromGit(request, "TEST", "false").getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedFilledWithFailsFilledFilled() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("WithFails", true);
		request.setAttribute("NoFails", false);
		request.setAttribute("SysAdmin", false);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				confRest.setKnowledgeExtractedFromGit(request, "TEST", "false").getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedFilledNoFailsFilledFilled() {
		assertEquals(Response.Status.OK.getStatusCode(),
				confRest.setKnowledgeExtractedFromGit(request, "TEST", "false").getStatus());
	}

	@Test
	public void testSetKnowledgeExtractedFilledNoFailsFilledInvalid() {
		assertEquals(Response.Status.OK.getStatusCode(),
				confRest.setKnowledgeExtractedFromGit(request, "TEST", "testNotABoolean").getStatus());
	}
}
