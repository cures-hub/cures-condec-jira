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

public class TestGetElements extends TestSetUp {
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
	public void testNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.getElements(null, null).getStatus());
	}

	@Test
	public void testFilled() {
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		Response response = knowledgeRest.getElements(request, filterSettings);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

}
