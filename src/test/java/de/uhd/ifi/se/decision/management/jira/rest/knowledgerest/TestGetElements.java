package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;

public class TestGetElements extends TestSetUp {
	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;

	private final static String BAD_REQUEST_ERROR = "Getting elements failed due to a bad request.";

	@Before
	public void setUp() {
		super.init();
		knowledgeRest = new KnowledgeRest();
		request = new MockHttpServletRequest();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);

	}

	@Test
	@NonTransactional
	public void testNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", BAD_REQUEST_ERROR))
				.build().getEntity(), knowledgeRest.getElements(false, null, null, null, null).getEntity());
	}

	@Test
	@NonTransactional
	public void testFilledTrue() {
		assertEquals(Response.Status.OK.getStatusCode(),
				knowledgeRest.getElements(true, "TEST", "", "TEST-4",request).getStatus());
	}

	@Test
	@NonTransactional
	public void testFilledFalse() {
		assertEquals(Response.Status.OK.getStatusCode(),
				knowledgeRest.getElements(false, "TEST", "", "TEST-4",request).getStatus());
	}
}
