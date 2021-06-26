package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisConfig;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class TestSetCiaSettings extends TestSetUp {

	protected HttpServletRequest request;
	protected ConfigRest configRest;
	protected ChangeImpactAnalysisConfig settings;

	@Before
	public void setUp() {
		init();
		settings = new ChangeImpactAnalysisConfig();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testProjectKeyNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), configRest.setCiaSettings(request, null, settings).getStatus());
	}

	@Test
	public void testProjectKeyEmpty() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), configRest.setCiaSettings(request, "", settings).getStatus());
	}

	@Test
	public void testProjectKeyInvalid() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), configRest.setCiaSettings(request, "InvalidKey", settings).getStatus());
	}

	@Test
	public void testProjectCiaSettingsInvalid() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), configRest.setCiaSettings(request, "Test", null).getStatus());
	}

	@Test
	public void testProjectKeyValid() {
		assertEquals(Response.Status.OK.getStatusCode(), configRest.setCiaSettings(request, "TEST", settings).getStatus());
	}
}
