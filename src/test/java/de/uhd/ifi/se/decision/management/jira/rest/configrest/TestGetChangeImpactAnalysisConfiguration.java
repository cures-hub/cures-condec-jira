package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;

public class TestGetChangeImpactAnalysisConfiguration extends TestSetUp {

	protected ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
	}

	@Test
	public void testProjectKeyNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.getChangeImpactAnalysisConfiguration(null).getStatus());
	}

	@Test
	public void testProjectKeyEmpty() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.getChangeImpactAnalysisConfiguration("").getStatus());
	}

	@Test
	public void testProjectKeyInvalid() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				configRest.getChangeImpactAnalysisConfiguration("InvalidKey").getStatus());
	}

	@Test
	public void testProjectKeyValid() {
		assertEquals(Response.Status.OK.getStatusCode(),
				configRest.getChangeImpactAnalysisConfiguration("TEST").getStatus());
	}
}
