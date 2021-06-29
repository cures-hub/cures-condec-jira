package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisConfiguration;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetChangeImpactAnalysisConfiguration extends TestSetUp {

	protected HttpServletRequest request;
	protected ConfigRest configRest;
	protected ChangeImpactAnalysisConfiguration ciaConfig;

	@Before
	public void setUp() {
		init();
		ciaConfig = new ChangeImpactAnalysisConfiguration();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testProjectKeyNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setChangeImpactAnalysisConfiguration(request, null, ciaConfig).getStatus());
	}

	@Test
	public void testProjectKeyEmpty() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setChangeImpactAnalysisConfiguration(request, "", ciaConfig).getStatus());
	}

	@Test
	public void testProjectKeyInvalid() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setChangeImpactAnalysisConfiguration(request, "InvalidKey", ciaConfig).getStatus());
	}

	@Test
	public void testProjectCiaConfigInvalid() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.setChangeImpactAnalysisConfiguration(request, "TEST", null).getStatus());
	}

	@Test
	public void testProjectKeyValid() {
		assertEquals(Response.Status.OK.getStatusCode(),
				configRest.setChangeImpactAnalysisConfiguration(request, "TEST", ciaConfig).getStatus());
	}
}
