package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteCommentsSentences extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testCommentNull() {
		assertFalse(JiraIssueTextPersistenceManager.deletePartsOfComment(null));
	}

	@Test
	@NonTransactional
	public void testCommentFilledButNotInAODatabase() {
		addCommentsToIssue("This is a comment for test purposes");
		assertFalse(JiraIssueTextPersistenceManager.deletePartsOfComment(comment1));
	}
}
