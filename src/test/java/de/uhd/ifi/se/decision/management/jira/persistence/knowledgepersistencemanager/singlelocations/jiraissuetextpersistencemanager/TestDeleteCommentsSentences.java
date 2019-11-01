package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
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
		Issue issue = ComponentAccessor.getIssueManager().getIssueObject("TEST-30");
		Comment comment = JiraIssues.addCommentsToIssue(issue, "This is a comment for test purposes");
		assertFalse(JiraIssueTextPersistenceManager.deletePartsOfComment(comment));
	}
}
