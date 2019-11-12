package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestPostFeatureBranchCommits extends TestConfigSuper {
	@Test
	public void testOk() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
			configRest.setPostFeatureBranchCommits(request, "TEST", "true").getStatus());
	}

	@Test
	public void testInvalidKey() {
		assertNotEquals(Response.status(Response.Status.OK).build().getStatus(),
			configRest.setPostFeatureBranchCommits(request, null, "true").getStatus());
	}

	@Test
	public void testEmptyKey() {
		assertNotEquals(Response.status(Response.Status.OK).build().getStatus(),
			configRest.setPostFeatureBranchCommits(request, "", "true").getStatus());
	}

	@Test
	public void testInvalidBooleanValue() {
		configRest.setPostFeatureBranchCommits(request, "TEST", "ok");
		assertEquals(false, ConfigPersistenceManager.isPostFeatureBranchCommitsActivated("TEST"));
	}
}
