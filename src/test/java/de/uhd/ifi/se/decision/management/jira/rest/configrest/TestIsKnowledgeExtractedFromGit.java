package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestIsKnowledgeExtractedFromGit extends TestConfigSuper {
	@Test
	public void testIsKnowledgeExtractedNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.isKnowledgeExtractedFromGit(null).getStatus());
	}

	@Test
	public void testIsKnowledgeExtractedNonExistend() {
		assertEquals(Status.OK.getStatusCode(), configRest.isKnowledgeExtractedFromGit("NotTEST").getStatus());
	}

	@Test
	public void testIsKnowledgeExtratedExistend() {
		assertEquals(Status.OK.getStatusCode(), configRest.isKnowledgeExtractedFromGit("TEST").getStatus());
	}
}
