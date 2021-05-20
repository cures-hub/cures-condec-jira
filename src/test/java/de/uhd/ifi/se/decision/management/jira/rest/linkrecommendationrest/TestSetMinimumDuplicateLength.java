package de.uhd.ifi.se.decision.management.jira.rest.linkrecommendationrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.LinkRecommendationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetMinimumDuplicateLength extends TestSetUp {

	protected HttpServletRequest request;
	protected LinkRecommendationRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new LinkRecommendationRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testInvalidValueSmallerThanThree() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setMinimumDuplicateLength(request, "TEST", 0).getStatus());
	}

	@Test
	public void testValidEdgeCaseValueThree() {
		assertEquals(Status.OK.getStatusCode(), configRest.setMinimumDuplicateLength(request, "TEST", 3).getStatus());
	}

	@Test
	public void testValidValue() {
		assertEquals(Status.OK.getStatusCode(), configRest.setMinimumDuplicateLength(request, "TEST", 9).getStatus());
	}

	@Test
	public void testInvalidProjectKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setMinimumDuplicateLength(request, "", 0).getStatus());
	}
}
