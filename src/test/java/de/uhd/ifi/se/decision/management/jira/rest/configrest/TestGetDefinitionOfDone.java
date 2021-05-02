package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetDefinitionOfDone extends TestSetUp {

	protected HttpServletRequest request;
	protected ConfigRest configRest;

	protected static String INVALID_PROJECT_KEY = "";
	protected static String VALID_PROJECT_KEY = "TEST";

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@DisplayName("Test the method getDefinitionOfDone with valid value.")
	public void testGetDefinitionOfDoneValidProject() {
		assertEquals(200, configRest.getDefinitionOfDone(request, VALID_PROJECT_KEY).getStatus());
	}

	@Test
	@DisplayName("Test the method getDefinitionOfDone with invalid project key.")
	public void testGetDefinitionOfDoneInvalidProject() {
		assertEquals(400, configRest.getDefinitionOfDone(request, INVALID_PROJECT_KEY).getStatus());
	}
}
