package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;

public class TestGetLinkTypes extends TestSetUp {

	protected ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
	}

	@Test
	public void testProjectKeyNull() {
		assertEquals(Status.OK.getStatusCode(), configRest.getLinkTypes(null).getStatus());
	}

	@Test
	public void testProjectKeyEmpty() {
		assertEquals(Status.OK.getStatusCode(), configRest.getLinkTypes("").getStatus());
	}

	@Test
	public void testProjectKeyInvalid() {
		assertEquals(Status.OK.getStatusCode(), configRest.getLinkTypes("InvalidKey").getStatus());
	}

	@Test
	public void testProjectKeyValid() {
		assertEquals(Status.OK.getStatusCode(), configRest.getLinkTypes("TEST").getStatus());
	}
}
