package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class TestPostFeatureBranchCommits extends TestConfigSuper {

	@Test
	public void testOk() {
		assertEquals(Status.OK.getStatusCode(),
				configRest.setPostFeatureBranchCommits(request, "TEST", "true").getStatus());
	}

	@Test
	public void testInvalidKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setPostFeatureBranchCommits(request, null, "true").getStatus());
	}

	@Test
	public void testEmptyKey() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setPostFeatureBranchCommits(request, "", "true").getStatus());
	}

	@Test
	public void testInvalidBooleanValue() {
		configRest.setPostFeatureBranchCommits(request, "TEST", "ok");
		assertEquals(false, ConfigPersistenceManager.isPostFeatureBranchCommitsActivated("TEST"));
	}

	@Test
	public void testNullBooleanValue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setPostFeatureBranchCommits(request, "TEST", null).getStatus());
	}
}
