package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestGetFeatureBranchCommits extends TestSetUpGit {

	private final String repoBaseDirectory;
	private GitClient testGitClient;

	private String featureBranch = "featureBranch";
	private String expectedFirstCommitMessage = "First message";

	public TestGetFeatureBranchCommits() {
		repoBaseDirectory = super.getRepoBaseDirectory();
	}

	@Test
	public void testGetFeatureBranchCommitsByString() {
		// fetches the 'default' branch commits. Do not use TestSetUpGit' gitClient
		testGitClient = new GitClientImpl(GIT_URI, repoBaseDirectory, "TEST");

		List<RevCommit> commits = testGitClient.getFeatureBranchCommits(featureBranch);
		assertEquals(4, commits.size());
		assertEquals(expectedFirstCommitMessage, commits.get(0).getFullMessage());
	}

	@Test
	public void testGetFeatureBranchCommitsByRef() {
		// fetches the 'default' branch commits. Do not use TestSetUpGit' gitClient
		testGitClient = new GitClientImpl(GIT_URI, repoBaseDirectory, "TEST");

		// get the Ref
		List<Ref> remoteBranches = testGitClient.getRemoteBranches();
		List<Ref> branchCandidates = remoteBranches.stream().filter(ref -> ref.getName().endsWith(featureBranch))
				.collect(Collectors.toList());

		assertEquals(1, branchCandidates.size());

		List<RevCommit> commits = testGitClient.getFeatureBranchCommits(branchCandidates.get(0));
		assertEquals(4, commits.size());
		assertEquals(expectedFirstCommitMessage, commits.get(0).getFullMessage());
	}
}
