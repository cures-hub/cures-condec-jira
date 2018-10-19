package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestSetWebhookData extends TestConfigSuper {

	@Test
	public void testReqNullProNullAdrNullSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setWebhookData(null, null, null, null).getStatus());
	}

	@Test
	public void testReqNullProNullAdrNullSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setWebhookData(null, null, null, "TEST").getStatus());
	}

	@Test
	public void testReqNullProNullAdrFilledSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setWebhookData(null, null, "TEST", null).getStatus());
	}

	@Test
	public void testReqNullProNullAdrFilledSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setWebhookData(null, null, "TEST", "TEST").getStatus());
	}

	@Test
	public void testReqNullProFilledAdrNullSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setWebhookData(null, "TEST", null, null).getStatus());
	}

	@Test
	public void testReqNullProFilledAdrNullSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setWebhookData(null, "TEST", null, "TEST").getStatus());
	}

	@Test
	public void testReqNullProFilledAdrFilledSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setWebhookData(null, "TEST", "TEST", null).getStatus());
	}

	@Test
	public void testReqNullProFilledAdrFilledSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setWebhookData(null, "TEST", "TEST", "TEST").getStatus());
	}

	@Test
	public void testReqFilledProNullAdrNullSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setWebhookData(request, null, null, null).getStatus());
	}

	@Test
	public void testReqFilledProNullAdrNullSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setWebhookData(request, null, null, "TEST").getStatus());
	}

	@Test
	public void testReqFilledProNullAdrFilledSecNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setWebhookData(request, null, "TEST", null).getStatus());
	}

	@Test
	public void testReqFilledProNullAdrFilledSecFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				confRest.setWebhookData(request, null, "TEST", "TEST").getStatus());
	}

//	@Test
//	public void testReqFilledProFilledAdrNullSecNull() {
//		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
//				confRest.setWebhookData(request, "TEST", null, null).getStatus());
//	}
//
//	@Test
//	public void testReqFilledProFilledAdrNullSecFilled() {
//		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
//				confRest.setWebhookData(request, "TEST", null, "TEST").getStatus());
//	}
//
//	@Test
//	public void testReqFilledProFilledAdrFilledSecNull() {
//		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
//				confRest.setWebhookData(request, "TEST", "TEST", null).getStatus());
//	}
//
//	@Test
//	public void testReqFilledProFilledAdrFilledSecFilled() {
//		assertEquals(Response.Status.OK.getStatusCode(),
//				confRest.setWebhookData(request, "TEST", "TEST", "TEST").getStatus());
//	}
}
