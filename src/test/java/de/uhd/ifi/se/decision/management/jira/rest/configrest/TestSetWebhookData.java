package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

public class TestSetWebhookData extends TestConfigSuper {

	@Test
	public void testReqNullProNullAdrNullSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(null, null, null, null).getStatus());
	}

	@Test
	public void testReqNullProNullAdrNullSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(null, null, null, "TEST").getStatus());
	}

	@Test
	public void testReqNullProNullAdrFilledSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(null, null, "TEST", null).getStatus());
	}

	@Test
	public void testReqNullProNullAdrFilledSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(null, null, "TEST", "TEST").getStatus());
	}

	@Test
	public void testReqNullProFilledAdrNullSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(null, "TEST", null, null).getStatus());
	}

	@Test
	public void testReqNullProFilledAdrNullSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(null, "TEST", null, "TEST").getStatus());
	}

	@Test
	public void testReqNullProFilledAdrFilledSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(null, "TEST", "TEST", null).getStatus());
	}

	@Test
	public void testReqNullProFilledAdrFilledSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(null, "TEST", "TEST", "TEST").getStatus());
	}

	@Test
	public void testReqFilledProNullAdrNullSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(request, null, null, null).getStatus());
	}

	@Test
	public void testReqFilledProNullAdrNullSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(request, null, null, "TEST").getStatus());
	}

	@Test
	public void testReqFilledProNullAdrFilledSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(request, null, "TEST", null).getStatus());
	}

	@Test
	public void testReqFilledProNullAdrFilledSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(request, null, "TEST", "TEST").getStatus());
	}

	@Test
	public void testReqFilledProFilledAdrNullSecNull() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("SysAdmin", true);
		((MockHttpServletRequest) request).setParameter("projectKey", "TEST");
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(request, "TEST", null, null).getStatus());
	}

	@Test
	public void testReqFilledProFilledAdrNullSecFilled() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("SysAdmin", true);
		((MockHttpServletRequest) request).setParameter("projectKey", "TEST");
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(request, "TEST", null, "TEST").getStatus());
	}

	@Test
	public void testReqFilledProFilledAdrFilledSecNull() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("SysAdmin", true);
		((MockHttpServletRequest) request).setParameter("projectKey", "TEST");
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setWebhookData(request, "TEST", "TEST", null).getStatus());
	}

	@Test
	public void testReqFilledProFilledAdrFilledSecFilled() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("SysAdmin", true);
		((MockHttpServletRequest) request).setParameter("projectKey", "TEST");
		assertEquals(Response.Status.OK.getStatusCode(),
				configRest.setWebhookData(request, "TEST", "TEST", "TEST").getStatus());
	}
}
