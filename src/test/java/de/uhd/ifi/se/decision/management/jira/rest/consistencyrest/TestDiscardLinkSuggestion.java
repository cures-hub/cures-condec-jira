package de.uhd.ifi.se.decision.management.jira.rest.consistencyrest;

import com.atlassian.jira.issue.Issue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestDiscardLinkSuggestion extends TestConsistencyRestSuper {

	@Test
	public void testWithValidIssues() {
		Issue baseIssue = issues.get(0);
		Issue linkIssue = issues.get(1);
		int actualStatus =
			super.consistencyRest
				.discardLinkSuggestion(request, baseIssue.getProjectObject().getKey(), baseIssue.getKey(), linkIssue.getKey())
				.getStatus();
		assertEquals("The response status should be OK (200).", 200, actualStatus);
	}

	@Test
	public void testWithInvalidIssues() {
		Issue baseIssue = issues.get(0);
		Issue linkIssue = issues.get(1);
		int actualStatus =
			super.consistencyRest
				.discardLinkSuggestion(request, baseIssue.getProjectObject().getKey(), null, linkIssue.getKey())
				.getStatus();
		assertEquals("The response status should be not OK.", 500, actualStatus);
	}


}
