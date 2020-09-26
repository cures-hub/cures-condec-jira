package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Before;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettings;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public abstract class TestConfigSuper extends TestSetUp {
	protected HttpServletRequest request;
	protected ConfigRest configRest;

	protected static final String INVALID_PROJECTKEY = "The project key is invalid.";
	protected static final String INVALID_REQUEST = "request = null";
	protected static final String INVALID_STRATEGY = "isIssueStrategy = null";
	protected static final String INVALID_ACTIVATION_NULL = "isActivated = null";
	protected static final String INVALID_ACTIVATION_STRING = "isActivated is invalid";
	protected static ApplicationUser user;

	@Before
	public void setUp() {
		configRest = new ConfigRest();
		init();

		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request = new MockHttpServletRequest();
		request.setAttribute("user", user);
	}

	protected Response getBadRequestResponse(String errorMessage) {
		return Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", errorMessage)).build();
	}

	@AfterClass
	public static void tearDown() {
		// reset plugin settings to default settings
		MockPluginSettingsFactory.pluginSettings = new MockPluginSettings();
	}

}
