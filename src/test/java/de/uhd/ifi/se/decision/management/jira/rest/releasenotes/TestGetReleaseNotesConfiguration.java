package de.uhd.ifi.se.decision.management.jira.rest.releasenotes;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ReleaseNotesRest;

public class TestGetReleaseNotesConfiguration extends TestSetUp {

	private ReleaseNotesRest releaseNotesRest;

	@Before
	public void setUp() {
		init();
		releaseNotesRest = new ReleaseNotesRest();
	}

	@Test
	public void testProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				releaseNotesRest.getReleaseNotesConfiguration(null).getStatus());
	}

	@Test
	public void testProjectKeyValid() {
		assertEquals(Status.OK.getStatusCode(), releaseNotesRest.getReleaseNotesConfiguration("TEST").getStatus());
	}
}