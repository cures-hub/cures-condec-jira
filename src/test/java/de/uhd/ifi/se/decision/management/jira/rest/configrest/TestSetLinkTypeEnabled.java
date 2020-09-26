package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetLinkTypeEnabled extends TestSetUp {

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
	public void testRequestNullProjectKeyNullIsActivatedFalseLinkTypeNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setLinkTypeEnabled(null, null, false, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyExistsIsActivatedFalseLinkTypeNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setLinkTypeEnabled(null, "TEST", false, null).getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsActivatedFalseLinkTypeNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setLinkTypeEnabled(request, "TEST", false, null).getStatus());
	}

	@Test
	public void testUserUnauthorized() {
		request.setAttribute("user", null);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),
				configRest.setLinkTypeEnabled(request, "TEST", false, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyNullIsActivatedTrueLinkTypeFilled() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setLinkTypeEnabled(null, null, true, LinkType.FORBID.toString()).getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsActivatedTrueLinkTypeFilled() {
		assertEquals(Response.Status.OK.getStatusCode(),
				configRest.setLinkTypeEnabled(request, "TEST", true, LinkType.FORBID.toString()).getStatus());
	}

	@Test
	public void testRequestExistsProjectKeyExistsIsActivatedFalseLinkTypeFilled() {
		assertEquals(Response.Status.OK.getStatusCode(),
				configRest.setLinkTypeEnabled(request, "TEST", false, LinkType.FORBID.toString()).getStatus());
	}
}
