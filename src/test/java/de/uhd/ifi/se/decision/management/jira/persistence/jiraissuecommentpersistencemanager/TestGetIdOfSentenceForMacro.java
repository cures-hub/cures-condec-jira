package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.model.TestComment;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetIdOfSentenceForMacro extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testBodyNullIssueIdLessTypeNullKeyNull() {
		assertEquals(0, JiraIssueCommentPersistenceManager.getIdOfSentenceForMacro(null, -1, null, null));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdLessTypeNullKeyNull() {
		assertEquals(0, JiraIssueCommentPersistenceManager
				.getIdOfSentenceForMacro("This is a comment for test purposes", -1, null, null));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdZeroTypeNullKeyNull() {
		assertEquals(0, JiraIssueCommentPersistenceManager.getIdOfSentenceForMacro(null, 0, null, null));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdZeroTypeNullKeyNull() {
		assertEquals(0, JiraIssueCommentPersistenceManager
				.getIdOfSentenceForMacro("This is a comment for test purposes", 0, null, null));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdLessTypeFilledKeyNull() {
		assertEquals(0, JiraIssueCommentPersistenceManager.getIdOfSentenceForMacro(null, -1, "ISSUE", null));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdLessTypeFilledKeyNull() {
		assertEquals(0, JiraIssueCommentPersistenceManager
				.getIdOfSentenceForMacro("This is a comment for test purposes", -1, "ISSUE", null));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdZeroTypeFilledKeyNull() {
		assertEquals(0, JiraIssueCommentPersistenceManager.getIdOfSentenceForMacro(null, 0, "ISSUE", null));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdZeroTypeFilledKeyNull() {
		assertEquals(0, JiraIssueCommentPersistenceManager
				.getIdOfSentenceForMacro("This is a comment for test purposes", 0, "ISSUE", null));
	}

	// (fewfwf

	@Test
	@NonTransactional
	public void testBodyNullIssueIdLessTypeNullKeyFilled() {
		assertEquals(0, JiraIssueCommentPersistenceManager.getIdOfSentenceForMacro(null, -1, null, "TEST"));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdLessTypeNullKeyFilled() {
		assertEquals(0, JiraIssueCommentPersistenceManager
				.getIdOfSentenceForMacro("This is a comment for test purposes", -1, null, "TEST"));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdZeroTypeNullKeyFilled() {
		assertEquals(0, JiraIssueCommentPersistenceManager.getIdOfSentenceForMacro(null, 0, null, "TEST"));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdZeroTypeNullKeyFilled() {
		assertEquals(0, JiraIssueCommentPersistenceManager
				.getIdOfSentenceForMacro("This is a comment for test purposes", 0, null, "TEST"));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdLessTypeFilledKeyFilled() {
		assertEquals(0, JiraIssueCommentPersistenceManager.getIdOfSentenceForMacro(null, -1, "ISSUE", "TEST"));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdLessTypeFilledKeyFilled() {
		assertEquals(0, JiraIssueCommentPersistenceManager
				.getIdOfSentenceForMacro("This is a comment for test purposes", -1, "ISSUE", "TEST"));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdZeroTypeFilledKeyFilled() {
		assertEquals(0, JiraIssueCommentPersistenceManager.getIdOfSentenceForMacro(null, 0, "ISSUE", "TEST"));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdZeroTypeFilledKeyFilled() {
		assertEquals(0, JiraIssueCommentPersistenceManager
				.getIdOfSentenceForMacro("This is a comment for test purposes", 0, "ISSUE", "TEST"));
	}

	@Test
	@NonTransactional
	public void testBodyWrongIssueIdOkTypeFilledKeyFilled() {
		TestComment tc = new TestComment();
		List<Sentence> comment = tc.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(1), null);
		assertEquals(0, JiraIssueCommentPersistenceManager.getIdOfSentenceForMacro("Not the right Body",
				comment.get(0).getJiraIssueId(), "Issue", "TEST"));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdOkTypeFilledKeyFilled() {
		TestComment tc = new TestComment();
		List<Sentence> comment = tc.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(comment.get(1), null);
		assertEquals(3, JiraIssueCommentPersistenceManager.getIdOfSentenceForMacro("testobject",
				comment.get(0).getJiraIssueId(), "Issue", "TEST"));
	}
}
