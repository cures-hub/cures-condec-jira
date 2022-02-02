package de.uhd.ifi.se.decision.management.jira.rest.changeimpactanalysisrest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisConfiguration;
import de.uhd.ifi.se.decision.management.jira.rest.ChangeImpactAnalysisRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetChangeImpactAnalysisConfiguration extends TestSetUp {

	protected HttpServletRequest request;
	protected ChangeImpactAnalysisRest ciaRest;
	protected ChangeImpactAnalysisConfiguration ciaConfig;

	@Before
	public void setUp() {
		init();
		ciaConfig = new ChangeImpactAnalysisConfiguration();
		ciaRest = new ChangeImpactAnalysisRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testProjectKeyNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				ciaRest.setChangeImpactAnalysisConfiguration(request, null, ciaConfig).getStatus());
	}

	@Test
	public void testProjectKeyEmpty() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				ciaRest.setChangeImpactAnalysisConfiguration(request, "", ciaConfig).getStatus());
	}

	@Test
	public void testProjectKeyInvalid() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				ciaRest.setChangeImpactAnalysisConfiguration(request, "InvalidKey", ciaConfig).getStatus());
	}

	@Test
	public void testProjectCiaConfigNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				ciaRest.setChangeImpactAnalysisConfiguration(request, "TEST", null).getStatus());
	}

	@Test
	public void testProjectCiaConfigValidEmptyRules() {
		ciaConfig.setPropagationRules(List.of());
		assertEquals(Response.Status.OK.getStatusCode(),
				ciaRest.setChangeImpactAnalysisConfiguration(request, "TEST", ciaConfig).getStatus());
	}

	@Test
	public void testProjectKeyValid() {
		assertEquals(Response.Status.OK.getStatusCode(),
				ciaRest.setChangeImpactAnalysisConfiguration(request, "TEST", ciaConfig).getStatus());
	}
}
