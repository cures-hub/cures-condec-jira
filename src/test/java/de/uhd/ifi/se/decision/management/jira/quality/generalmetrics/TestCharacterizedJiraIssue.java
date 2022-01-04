package de.uhd.ifi.se.decision.management.jira.quality.generalmetrics;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCharacterizedJiraIssue extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testCommentWithTranscribedCommitMessage() {
		JiraIssues.addCommentsToIssue(JiraIssues.getTestJiraIssues().get(0), "Hash:");
		CharacterizedJiraIssue jiraIssue = new CharacterizedJiraIssue(JiraIssues.getTestJiraIssues().get(0));
		assertEquals(1, jiraIssue.getNumberOfComments());
		assertEquals(0, jiraIssue.getNumberOfRelevantComments());
		assertEquals(1, jiraIssue.getNumberOfIrrelevantComments());
		assertEquals(1, jiraIssue.getNumberOfCommits());
	}

	@Test
	@NonTransactional
	public void testCommentWithRelevantDecisionKnowledge() {
		JiraIssues.getSentencesForCommentText("{decision} We will use Nutch! {decision}");
		CharacterizedJiraIssue jiraIssue = new CharacterizedJiraIssue(JiraIssues.getJiraIssueByKey("TEST-30"));
		assertEquals(1, jiraIssue.getNumberOfComments());
		assertEquals(1, jiraIssue.getNumberOfRelevantComments());
		assertEquals(0, jiraIssue.getNumberOfIrrelevantComments());
		assertEquals(0, jiraIssue.getNumberOfCommits());
	}
}