package de.uhd.ifi.se.decision.management.jira.quality.generalmetrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
		assertEquals(1, jiraIssue.getNumberOfComments());
		assertEquals(1, jiraIssue.getNumberOfIrrelevantComments());
		assertEquals(1, jiraIssue.getNumberOfCommits());
	}

	@Test
	public void testCommentWithRelevantDecisionKnowledge() {
		JiraIssues.addComment(JiraIssues.getTestJiraIssues().get(0));
		CharacterizedJiraIssue jiraIssue = new CharacterizedJiraIssue(JiraIssues.getTestJiraIssues().get(0));
		assertEquals(1, jiraIssue.getNumberOfComments());
		assertEquals(1, jiraIssue.getNumberOfRelevantComments());
		assertEquals(0, jiraIssue.getNumberOfCommits());
	}

}
