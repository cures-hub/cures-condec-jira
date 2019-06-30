package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestSetKnowledgeTypeEnabled extends TestConfigSuper {

	private static final String INVALID_KNOWLEDGE_ENABLED = "isKnowledgeTypeEnabled = null";

	@Test
	public void testRequestNullProjectKeyNullIsActivatedNullKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeTypeEnabled(null, null, null, null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyNullIsActivatedTrueNullKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeTypeEnabled(null, null, "true", null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyNullIsActivatedFalseKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeTypeEnabled(null, null, "false", null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsActivatedNullKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeTypeEnabled(null, "TEST", null, null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsActivatedTrueKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeTypeEnabled(null, "TEST", "true", null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsActivatedFalseKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeTypeEnabled(null, "TEST", "false", null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyDoesNotExistIsActivatedNullKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeTypeEnabled(null, "NotTEST", null, null).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyDoesNotExistIsActivatedTrueKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeTypeEnabled(null, "NotTEST", "true", null).getEntity());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyDoesNotExistIsActivatedFalseKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeTypeEnabled(null, "NotTEST", "false", null).getEntity());
	}

	@Test
	public void testRequestExistsProjectKeyNullIsActivatedNullKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.setKnowledgeTypeEnabled(request, null, null, null).getEntity());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyNullIsActivatedTrueKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.setKnowledgeTypeEnabled(request, null, "true", null).getEntity());
	}

	@Test
	public void testRequestExistsProjectKeyNullIsActivatedFalseKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.setKnowledgeTypeEnabled(request, null, "false", null).getEntity());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsActivatedNullKnowledgeTypeNull() {
		assertEquals(getBadRequestResponse(INVALID_KNOWLEDGE_ENABLED).getEntity(),
				configRest.setKnowledgeTypeEnabled(request, "TEST", null, null).getEntity());
	}

	@Test
	public void tesRequestExistsProjectKeyExistsIsActivatedTrueKnowledgeTypeNull() {
		assertEquals(Response.ok().build().getClass(),
				configRest.setKnowledgeTypeEnabled(request, "TEST", "true", null).getClass());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsActivatedFalseKnowledgeTypeNull() {
		assertEquals(Response.ok().build().getClass(),
				configRest.setKnowledgeTypeEnabled(request, "TEST", "false", null).getClass());
	}

	@Test
	public void testUserUnauthorized() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("WithFails", true);
		request.setAttribute("NoFails", false);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				configRest.setKnowledgeTypeEnabled(request, "NotTEST", "false", null).getStatus());
	}

	@Test
	public void testUserNull() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", false);
		request.setAttribute("SysAdmin", false);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				configRest.setKnowledgeTypeEnabled(request, "NotTEST", "false", null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullIsActivatedNullKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeTypeEnabled(null, null, null, KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyNullIsActivatedTrueNullKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeTypeEnabled(null, null, "true", KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyNullIsActivatedFalseKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeTypeEnabled(null, null, "false", KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsActivatedNullKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(),
				configRest.setKnowledgeTypeEnabled(null, "TEST", null, KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsActivatedTrueKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(), configRest
				.setKnowledgeTypeEnabled(null, "TEST", "true", KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsActivatedFalseKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(), configRest
				.setKnowledgeTypeEnabled(null, "TEST", "false", KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyDoesNotExistIsActivatedNullKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(), configRest
				.setKnowledgeTypeEnabled(null, "NotTEST", null, KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testRequestNullProjectKeyDoesNotExistIsActivatedTrueKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(), configRest
				.setKnowledgeTypeEnabled(null, "NotTEST", "true", KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testSetActivatedRequestNullProjectKeyDoesNotExistIsActivatedFalseKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_REQUEST).getEntity(), configRest
				.setKnowledgeTypeEnabled(null, "NotTEST", "false", KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testRequestExistsProjectKeyNullIsActivatedNullKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.setKnowledgeTypeEnabled(request, null, null, KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testSetActivatedRequestExistsProjectKeyNullIsActivatedTrueKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(), configRest
				.setKnowledgeTypeEnabled(request, null, "true", KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testRequestExistsProjectKeyNullIsActivatedFalseKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(), configRest
				.setKnowledgeTypeEnabled(request, null, "false", KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsActivatedNullKnowledgeTypeFilled() {
		assertEquals(getBadRequestResponse(INVALID_KNOWLEDGE_ENABLED).getEntity(), configRest
				.setKnowledgeTypeEnabled(request, "TEST", null, KnowledgeType.SOLUTION.toString()).getEntity());
	}

	@Test
	public void tesRequestExistsProjectKeyExistsIsActivatedTrueKnowledgeTypeFilled() {
		assertEquals(Response.ok().build().getClass(), configRest
				.setKnowledgeTypeEnabled(request, "TEST", "true", KnowledgeType.SOLUTION.toString()).getClass());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsActivatedFalseKnowledgeTypeFilled() {
		assertEquals(Response.ok().build().getClass(), configRest
				.setKnowledgeTypeEnabled(request, "TEST", "false", KnowledgeType.SOLUTION.toString()).getClass());
	}
}
