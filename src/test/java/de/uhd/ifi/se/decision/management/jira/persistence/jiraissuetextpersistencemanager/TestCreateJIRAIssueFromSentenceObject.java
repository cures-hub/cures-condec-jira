package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCreateJIRAIssueFromSentenceObject extends TestJiraIssueCommentPersistenceManagerSetUp {

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
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(comment.get(1), null);
		assertNotNull(manager.createJIRAIssueFromSentenceObject(3, user));
	}
}
