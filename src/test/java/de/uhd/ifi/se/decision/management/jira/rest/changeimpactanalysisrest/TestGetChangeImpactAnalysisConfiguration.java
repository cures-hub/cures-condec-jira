package de.uhd.ifi.se.decision.management.jira.rest.changeimpactanalysisrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ChangeImpactAnalysisRest;

public class TestGetChangeImpactAnalysisConfiguration extends TestSetUp {

	protected ChangeImpactAnalysisRest ciaRest;

	@Before
	public void setUp() {
		init();
		ciaRest = new ChangeImpactAnalysisRest();
	}

	@Test
	public void testProjectKeyNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				ciaRest.getChangeImpactAnalysisConfiguration(null).getStatus());
	}

	@Test
	public void testProjectKeyEmpty() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				ciaRest.getChangeImpactAnalysisConfiguration("").getStatus());
	}

	@Test
	public void testProjectKeyInvalid() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				ciaRest.getChangeImpactAnalysisConfiguration("InvalidKey").getStatus());
	}

	@Test
	public void testProjectKeyValid() {
		assertEquals(Response.Status.OK.getStatusCode(),
				ciaRest.getChangeImpactAnalysisConfiguration("TEST").getStatus());
	}
}
