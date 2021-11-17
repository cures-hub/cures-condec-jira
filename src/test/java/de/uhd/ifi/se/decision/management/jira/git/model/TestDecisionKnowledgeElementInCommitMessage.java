package de.uhd.ifi.se.decision.management.jira.git.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestDecisionKnowledgeElementInCommitMessage extends TestSetUpGit {

	private DecisionKnowledgeElementInCommitMessage commitMessageElement;

	@Before
	public void setUp() {
		super.setUp();
		commitMessageElement = new DecisionKnowledgeElementInCommitMessage();
		commitMessageElement.setSummary("I am an issue");
		commitMessageElement.setType(KnowledgeType.ISSUE);
		commitMessageElement.setProject("TEST");
	}

	@Test
	public void testCommit() {
		RevCommit commit = gitClient.getDefaultBranchCommits().get(0);
		commitMessageElement.setCommit(commit);
		assertEquals(commit.getName(), commitMessageElement.getCommitName());
	}

	@Test
	public void testUrl() {
		commitMessageElement.setRepoUri(GIT_URI);
		RevCommit commit = gitClient.getDefaultBranchCommits().get(0);
		commitMessageElement.setCommit(commit);
		assertEquals(GIT_URI + "/commit/" + commitMessageElement.getCommitName(), commitMessageElement.getUrl());
	}

	@Test
	public void testImage() {
		assertTrue(commitMessageElement.getImage().contains("issue.png"));
	}
}
