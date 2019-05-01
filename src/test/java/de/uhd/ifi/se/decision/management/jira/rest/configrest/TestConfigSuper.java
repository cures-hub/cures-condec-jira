package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import net.java.ao.EntityManager;

public class TestConfigSuper extends TestSetUpWithIssues {
	protected EntityManager entityManager;
	protected HttpServletRequest request;
	protected ConfigRest configRest;

	protected static final String INVALID_PROJECTKEY = "Project key is invalid.";
	protected static final String INVALID_REQUEST = "request = null";
	protected static final String INVALID_STRATEGY = "isIssueStrategy = null";
	protected static final String INVALID_ACTIVATION = "isActivated = null";

	@Before
	public void setUp() {
		configRest = new ConfigRest();
		initialization();

		request = new MockHttpServletRequest();
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", false);
		request.setAttribute("NoSysAdmin", false);
		request.setAttribute("SysAdmin", true);
	}

	protected Response getBadRequestResponse(String errorMessage) {
		return Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", errorMessage)).build();
	}
}
