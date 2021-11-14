package de.uhd.ifi.se.decision.management.jira.git.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestDecisionKnowledgeElementInCommitMessage extends TestSetUpGit {

	private DecisionKnowledgeElementInCommitMessage codeCommentElement;

	@Before
	public void setUp() {
		super.setUp();
		codeCommentElement = new DecisionKnowledgeElementInCommitMessage();
		codeCommentElement.setSummary("I am an issue");
		codeCommentElement.setType(KnowledgeType.ISSUE);
		codeCommentElement.setProject("TEST");
	}

	@Test
	public void testCommit() {
		RevCommit commit = gitClient.getDefaultBranchCommits().get(0);
		codeCommentElement.setCommit(commit);
		assertEquals(commit.getName(), codeCommentElement.getCommitName());
	}

	@Test
	public void testImage() {
		assertTrue(codeCommentElement.getImage().contains("issue.png"));
	}
}
