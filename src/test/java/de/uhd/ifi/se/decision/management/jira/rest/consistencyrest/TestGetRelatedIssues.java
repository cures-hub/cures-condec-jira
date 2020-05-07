package de.uhd.ifi.se.decision.management.jira.rest.consistencyrest;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGetRelatedIssues  extends TestConsistencyRestSuper {

	@Test
	public void testWithValidIssue(){
		Issue baseIssue = issues.get(0);
		int actualStatus = super.consistencyRest.getRelatedIssues(request, baseIssue.getKey()).getStatus();
		assertEquals("The response status should be OK (200).", 200, actualStatus);
	}

	@Test
	public void testWithInvalidIssue(){
		int actualStatus = super.consistencyRest.getRelatedIssues(request, "GIBBERISH").getStatus();
		assertEquals("The response status should be server error (500).", 500, actualStatus);
	}
}
