package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class TestGetDefinitionOfDone extends TestConfigSuper {

	@Test
	public void testGetDefinitionOfDoneWithInvalidProjectKey() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
			configRest.getDefinitionOfDone(null).getEntity());
	}

	@Test
	public void testGetDefinitionOfDone() {
		String projectKey = "TEST";
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
			configRest.getDefinitionOfDone(projectKey).getStatus());
	}

}
