package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestRemoveFeatureBranchNotPresentInRemote extends TestSetUpGit {

	private final String repoBaseDirectory;
	private GitClient testGitClient;

	private String featureBranch = "featureBranch";
	private String expectedFirstCommitMessage = "First message";

	public TestRemoveFeatureBranchNotPresentInRemote() {
		repoBaseDirectory = super.getRepoBaseDirectory();
	}

	@Test
	public void testGetFeatureBranchNotOnRemote() {

		// fetches the 'default' branch commits. Do not use TestSetUpGit' gitClient
		testGitClient = new GitClientImpl(GIT_URI, repoBaseDirectory, "TEST");

		//delete branch on remote
		deleteFeatureBranchOnRemote();

		List<RevCommit> commits = testGitClient.getFeatureBranchCommits(featureBranch);
		// branch should not exist at local repo anymore
		assertNull(commits);
	}

	@Test
	public void testGetFeatureBranchNotOnRemoteButCached() {

		// fetches the 'default' branch commits. Do not use TestSetUpGit' gitClient
		testGitClient = new GitClientImpl(GIT_URI, repoBaseDirectory, "TEST");
		// fetch the branch, it will be cached for a while if not yet released to TEMP.. folder
		List<RevCommit> commits = testGitClient.getFeatureBranchCommits(featureBranch);

		//delete branch on remote
		deleteFeatureBranchOnRemote();

		// fetch the branch again, pull should not be performed due to cache
		List<RevCommit> commitsAgain = testGitClient.getFeatureBranchCommits(featureBranch);
		// branch expected to be still at local repo
		assertEquals(4, commitsAgain.size());
		assertEquals(commits, commitsAgain);
		assertEquals(expectedFirstCommitMessage, commits.get(0).getFullMessage());
	}

	private boolean deleteFeatureBranchOnRemote() {
		String featureBranchPath = GIT_URI
				+File.separator+"refs"
				+File.separator+"heads"
				+File.separator+featureBranch;
		File deleteBranch = new File(featureBranchPath);
		return deleteBranch.delete();
	}
}
