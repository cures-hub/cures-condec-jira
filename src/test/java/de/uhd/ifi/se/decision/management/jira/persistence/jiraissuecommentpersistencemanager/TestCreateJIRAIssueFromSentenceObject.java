package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.JiraIssueComment;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCreateJIRAIssueFromSentenceObject extends TestJiraIssueCommentPersistenceMangerSetUp {

	@Test
	@NonTransactional
	public void testIdLessUserNull() {
		assertNull(manager.createJIRAIssueFromSentenceObject(-1, null));
	}

	@Test
	@NonTransactional
	public void testIdZeroUserNull() {
		assertNull(manager.createJIRAIssueFromSentenceObject(0, null));
	}

	@Test
	@NonTransactional
	public void testIdOkUserNull() {
		assertNull(manager.createJIRAIssueFromSentenceObject(1, null));
	}

	@Test
	@NonTransactional
	public void testIdLessUserFilled() {
		assertNull(manager.createJIRAIssueFromSentenceObject(-1, user));
	}

	@Test
	@NonTransactional
	public void testIdZeroUserFilled() {
		assertNull(manager.createJIRAIssueFromSentenceObject(0, user));
	}

	@Test
	@NonTransactional
	public void testIdOkUserFilled() {
		JiraIssueComment comment = getComment("some sentence in front. {issue} testobject {issue} some sentence in the back.");
		TestJiraIssueCommentPersistenceMangerSetUp.insertDecisionKnowledgeElement(comment, comment.getIssueId(), 1);
		assertNotNull(manager.createJIRAIssueFromSentenceObject(3, user));
	}
}
