package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

public class TestGitDiffedCodeExtractionManager extends TestSetUpGit {

	@Test
	public void testDiffEmpty() {
		Diff diff = new Diff();
		GitDiffedCodeExtractionManager manager = new GitDiffedCodeExtractionManager(diff);
		assertTrue(manager.getOldDecisionKnowledgeElements().isEmpty());
		assertTrue(manager.getNewDecisionKnowledgeElements().isEmpty());
	}

	@Test
	public void testDiffNull() {
		Diff diff = null;
		GitDiffedCodeExtractionManager manager = new GitDiffedCodeExtractionManager(diff);
		assertTrue(manager.getOldDecisionKnowledgeElements().isEmpty());
		assertTrue(manager.getNewDecisionKnowledgeElements().isEmpty());
	}

	@Test
	public void testRevCommitExisting() {
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTests);
		Diff diff = gitClient.getDiff(commits);
		GitDiffedCodeExtractionManager manager = new GitDiffedCodeExtractionManager(diff);
		assertTrue(manager.getOldDecisionKnowledgeElements().isEmpty());
		assertEquals(10, manager.getNewDecisionKnowledgeElements().size());
	}

	@Test
	public void testJiraIssueKeyExisting() {
		Diff diff = gitClient.getDiff(mockJiraIssueForGitTests);
		GitDiffedCodeExtractionManager manager = new GitDiffedCodeExtractionManager(diff);
		assertTrue(manager.getOldDecisionKnowledgeElements().isEmpty());
		assertEquals(10, manager.getNewDecisionKnowledgeElements().size());
	}
}