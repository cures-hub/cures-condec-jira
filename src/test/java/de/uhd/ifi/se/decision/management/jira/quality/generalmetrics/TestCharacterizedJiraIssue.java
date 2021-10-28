package de.uhd.ifi.se.decision.management.jira.quality.generalmetrics;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestCharacterizedJiraIssue extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testCommentWithTranscribedCommitMessage() {
		JiraIssues.addCommentsToIssue(JiraIssues.getTestJiraIssues().get(0), "Hash:");
		CharacterizedJiraIssue jiraIssue = new CharacterizedJiraIssue(JiraIssues.getTestJiraIssues().get(0));
		assertTrue(jiraIssue.getNumberOfComments() == 1);
		assertTrue(jiraIssue.getNumberOfIrrelevantComments() == 1);
		assertTrue(jiraIssue.getNumberOfCommits() == 1);
	}

	@Test
	public void testCommentWithRelevantDecisionKnowledge() {
		JiraIssues.addComment(JiraIssues.getTestJiraIssues().get(0));
		CharacterizedJiraIssue jiraIssue = new CharacterizedJiraIssue(JiraIssues.getTestJiraIssues().get(0));
		assertTrue(jiraIssue.getNumberOfComments() == 1);
		assertTrue(jiraIssue.getNumberOfRelevantComments() == 1);
		assertTrue(jiraIssue.getNumberOfCommits() == 0);
	}

}
