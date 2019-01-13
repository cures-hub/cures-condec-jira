package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteCommentsSentences extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testCommentNull() {
		assertFalse(JiraIssueCommentPersistenceManager.deleteAllSentencesOfComments(null));
	}

	@Test
	@NonTransactional
	public void testCommentFilledButNotInAODatabase() {
		addCommentsToIssue("This is a comment for test purposes");
		assertFalse(JiraIssueCommentPersistenceManager.deleteAllSentencesOfComments(comment1));
	}
}
