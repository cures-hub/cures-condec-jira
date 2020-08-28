package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;


public class TestRationaleBacklogConfig extends TestConfigSuper {

	protected HttpServletRequest request;
	protected ConfigRest configRest;

	protected static String INVALID_PROJECT_KEY = "";
	protected static String VALID_PROJECT_KEY = "TEST";

	protected static String VALID_VALUE = "valid_value";
	protected static String INVALID_VALUE = "";

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	/**
	 * Test Set Definition of Done
	 */

	@Test
	@DisplayName("Test the method setDefinitionOfDone with valid value.")
	public void testSetDefinitionOfDoneValidProject() {
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		Gson gson = new Gson();
		assertEquals(200, configRest.setDefinitionOfDone(request, VALID_PROJECT_KEY, gson.toJson(definitionOfDone)).getStatus());
	}


	@Test
	@DisplayName("Test the method setDefinitionOfDone with invalid project key.")
	public void testSetDefinitionOfDoneInvalidProject() {
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		Gson gson = new Gson();
		assertEquals(400, configRest.setDefinitionOfDone(request, INVALID_PROJECT_KEY, gson.toJson(definitionOfDone)).getStatus());
	}

	@Test(expected = JsonSyntaxException.class)
	@DisplayName("Test the method setDefinitionOfDone with invalid JSON.")
	public void testSetDefinitionOfDoneInvalidJSON() {
		assertEquals(400, configRest.setDefinitionOfDone(request, VALID_PROJECT_KEY, "-123").getStatus());
	}

}
