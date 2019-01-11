package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteCommentsSentences extends TestJiraIssueCommentPersistenceMangerSetUp {

	@Test
	@NonTransactional
	public void testCommentNull() {
		JiraIssueCommentPersistenceManager.deleteCommentsSentences(null);
	}

	@Test
	@NonTransactional
	public void testCommentFilled() {
		addCommentsToIssue("This is a comment for test purposes");
		JiraIssueCommentPersistenceManager.deleteCommentsSentences(comment1);
	}
}
