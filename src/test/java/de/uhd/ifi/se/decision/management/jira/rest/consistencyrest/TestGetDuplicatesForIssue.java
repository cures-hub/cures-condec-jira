package de.uhd.ifi.se.decision.management.jira.rest.consistencyrest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGetDuplicatesForIssue extends TestConsistencyRestSuper {

	@Test
	public void testGetDuplicatesForIssue() {
		assertEquals("Request should be valid.", 200, consistencyRest.getDuplicatesForIssue(request, issues.get(0).getKey()).getStatus());
		assertEquals("Request should be invalid.", 400, consistencyRest.getDuplicatesForIssue(request, "null").getStatus());

		assertEquals("Request should be invalid.", 500, consistencyRest.getDuplicatesForIssue(request, null).getStatus());

	}
}
