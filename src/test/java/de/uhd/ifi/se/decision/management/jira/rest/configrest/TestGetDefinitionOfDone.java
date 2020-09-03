package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;

public class TestGetDefinitionOfDone extends TestSetUp {

	protected ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
	}

	@Test
	public void testGetDefinitionOfDoneWithInvalidProjectKey() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), configRest.getDefinitionOfDone(null).getStatus());
	}

	@Test
	public void testGetDefinitionOfDone() {
		String projectKey = "TEST";
		assertEquals(Response.Status.OK.getStatusCode(), configRest.getDefinitionOfDone(projectKey).getStatus());
	}

}
