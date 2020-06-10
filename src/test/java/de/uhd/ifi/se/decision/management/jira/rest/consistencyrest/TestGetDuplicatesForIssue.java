package de.uhd.ifi.se.decision.management.jira.rest.consistencyrest;

import de.uhd.ifi.se.decision.management.jira.consistency.implementation.DuplicateFragment;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGetDuplicatesForIssue extends TestConsistencyRestSuper {

	@Test
	public void testGetDuplicatesForIssue() {
		assertEquals("Request should be valid.", 200, consistencyRest.getDuplicatesForIssue(request, issues.get(0).getKey()).getStatus());
		String oldDescription = issues.get(1).getDescription();
		issues.get(1).setDescription(issues.get(0).getDescription());
		assertEquals("Request should be valid.", 200, consistencyRest.getDuplicatesForIssue(request, issues.get(0).getKey()).getStatus());

		issues.get(1).setDescription(oldDescription);
		assertEquals("Request should be invalid.", 400, consistencyRest.getDuplicatesForIssue(request, "null").getStatus());
		assertEquals("Request should be invalid.", 500, consistencyRest.getDuplicatesForIssue(request, null).getStatus());
	}

	@Test
	public void testDuplicatesToJsonMap() {
		DuplicateFragment testDuplicate = null;
		assertEquals("Request should be valid.", 0, consistencyRest.duplicateToJsonMap(testDuplicate).size());
		testDuplicate = new DuplicateFragment(issues.get(0), issues.get(1), issues.get(1).getSummary(),0, 12, "description");
		assertEquals("Request should be valid.",  issues.get(1).getSummary(), consistencyRest.duplicateToJsonMap(testDuplicate).get("summary"));

	}
}
