package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetKnowledgeElements extends TestSetUp {
	private KnowledgeRest knowledgeRest;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		init();
		knowledgeRest = new KnowledgeRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestNullFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.getKnowledgeElements(null, null).getStatus());
	}

	@Test
	public void testRequestValidFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.getKnowledgeElements(request, null).getStatus());
	}

	@Test
	public void testProjectKeyInvalid() {
		FilterSettings filterSettings = new FilterSettings(null, "");
		Response response = knowledgeRest.getKnowledgeElements(request, filterSettings);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	@Test
	public void testFilled() {
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		Response response = knowledgeRest.getKnowledgeElements(request, filterSettings);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
}