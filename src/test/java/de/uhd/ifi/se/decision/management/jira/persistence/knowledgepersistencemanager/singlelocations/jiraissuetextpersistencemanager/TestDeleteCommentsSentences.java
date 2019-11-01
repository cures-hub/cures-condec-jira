package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteCommentsSentences extends TestSetUp {

	protected static JiraIssueTextPersistenceManager manager;
	protected static ApplicationUser user;

	@Before
	public void setUp() {
		init();
		manager = new JiraIssueTextPersistenceManager("TEST");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

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
