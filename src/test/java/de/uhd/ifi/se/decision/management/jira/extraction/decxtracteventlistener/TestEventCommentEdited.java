package de.uhd.ifi.se.decision.management.jira.extraction.decxtracteventlistener;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestEventCommentEdited extends TestSetUpEventListener {
	@Test
	public void testNoCommentContain() {
		IssueEvent issueEvent = createIssueEvent("", EventType.ISSUE_COMMENT_EDITED_ID);
		listener.onIssueEvent(issueEvent);
		assertTrue(checkComment(""));
	}

	@Test
	public void testRationaleTag() {
		IssueEvent issueEvent = createIssueEvent("{issue}This is a very severe issue.{issue}",
				EventType.ISSUE_COMMENT_EDITED_ID);
		listener.onIssueEvent(issueEvent);
		assertTrue(checkComment("{issue}This is a very severe issue.{issue}"));
	}

	@Test
	public void testExcludedTag() {
		IssueEvent issueEvent = createIssueEvent("{code}public static class{code}", EventType.ISSUE_COMMENT_EDITED_ID);
		listener.onIssueEvent(issueEvent);
        assertTrue(checkComment("{code}public static class{code}"));
	}

	@Test
	public void testRationaleIcon() {
		IssueEvent issueEvent = createIssueEvent("(!) This is a very severe issue.", EventType.ISSUE_COMMENT_EDITED_ID);
		listener.onIssueEvent(issueEvent);
        assertTrue(checkComment("{issue} This is a very severe issue.{issue}"));
	}
}
