package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class TestGetPropagationRules extends TestSetUp {

	protected ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
	}

	@Test
	public void testProjectKeyNull() {
		assertEquals(Response.Status.OK.getStatusCode(), configRest.getPropagationRules().getStatus());
	}
}
