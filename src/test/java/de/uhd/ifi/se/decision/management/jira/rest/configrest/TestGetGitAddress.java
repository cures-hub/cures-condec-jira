package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGetGitAddress extends TestConfigSuper {

	@Test
	public void testProNull() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getStatus(), confRest.getGitAddress(null).getStatus());
	}

	@Test
	public void testProEmpty() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getStatus(), confRest.getGitAddress("").getStatus());
	}

	@Test
	public void testProjectKeyInvalid() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				confRest.getGitAddress("InvalidKey").getStatus());
	}

	@Test
	public void testProjectKeyValid() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				confRest.getGitAddress("TEST").getStatus());
	}
}
