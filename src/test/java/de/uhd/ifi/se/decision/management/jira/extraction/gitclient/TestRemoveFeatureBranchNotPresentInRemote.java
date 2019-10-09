package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class TestRemoveFeatureBranchNotPresentInRemote extends TestSetUpGit {

	private final String repoBaseDirectory;
	private GitClient testGitClient;

	private String featureBranch = "featureBranch";
	private String expectedFirstCommitMessage = "First message";

	public TestRemoveFeatureBranchNotPresentInRemote() {
		repoBaseDirectory = super.getRepoBaseDirectory();
	}

	@After
	public void after() {
		System.out.println("After of "+this.getClass().getName());
		System.out.println(getBranchRefPathOnRemote());
		System.out.println(getTempBranchRefPathOnRemote());
		restoreFeatureBranchOnRemote();
	}

	@Test
	public void testGetFeatureBranchNotOnRemoteWithLocalPull() {
		System.out.println("testGetFeatureBranchNotOnRemoteWithLocalPull");

		// Use clean, distinct local repo dir for test
		String cloneAt = repoBaseDirectory+"t1";

		// fetches the 'default' branch commits. Do not use TestSetUpGit' gitClient.
		testGitClient = new GitClientImpl(GIT_URI, cloneAt, "TEST");

		// "delete" branch on remote
		assertTrue(moveFeatureBranchOnRemote());

		// do not prevent pull
		assertTrue(resetPullControl(testGitClient.getDirectory()));

		// getting branch with performing pull
		List<RevCommit> commits = testGitClient.getFeatureBranchCommits(featureBranch);
		// branch should not exist at local repo anymore
		assertNull(commits);
	}

	@Ignore
	@Test
	public void testGetFeatureBranchNotOnRemoteLocalPullCache() {
		System.out.println("testGetFeatureBranchNotOnRemoteLocalPullCache");

		// Use clean, distinct local repo dir for test
		String cloneAt = repoBaseDirectory+"t2";

		// fetches the 'default' branch commits. Do not use TestSetUpGit' gitClient
		testGitClient = new GitClientImpl(GIT_URI, cloneAt, "TEST");
		File developDir = testGitClient.getDirectory();

		// fetch the branch, it will be cached for a while and not automatically released to TEMP.. folder
		List<RevCommit> commits = testGitClient.getFeatureBranchCommits(featureBranch);

		//"delete" branch on remote
		assertTrue(moveFeatureBranchOnRemote());

		// Fetch the branch again, pull should not be performed due to cache
		List<RevCommit> commitsAgain = testGitClient.getFeatureBranchCommits(featureBranch);
		// branch expected to be still at local repo
		assertEquals(4, commitsAgain.size());
		assertEquals(commits, commitsAgain);
		assertEquals(expectedFirstCommitMessage, commits.get(0).getFullMessage());


		// Once more fetch the branch, but with pullig from remote

		// do not prevent pull
		assertTrue(resetPullControl(developDir));

		// branch should not exist at local repo anymore
		assertNull(testGitClient.getFeatureBranchCommits(featureBranch));
	}

	private boolean moveFeatureBranchOnRemote() {
		File branchRefPointer = new File(getBranchRefPathOnRemote());
		File newFileName = new File(getTempBranchRefPathOnRemote());
		branchRefPointer.renameTo(newFileName);
		branchRefPointer.delete();
		boolean isNewFile = newFileName.isFile();
		boolean isOldDeleted = !branchRefPointer.isFile();
		return isNewFile && isOldDeleted;
	}

	private boolean restoreFeatureBranchOnRemote() {
		File branchRefPointer = new File(getTempBranchRefPathOnRemote());
		File originalFileName = new File(getBranchRefPathOnRemote());
		branchRefPointer.renameTo(originalFileName);
		boolean isNewFile = originalFileName.isFile();
		boolean isOldDeleted = !branchRefPointer.isFile();
		return isNewFile && isOldDeleted;
	}

	private boolean resetPullControl(File localDir) {
		String controlFileInBranchDir = ".git"+File.separator+"condec.pullstamp.";
		File branchFoldersDir = new File(localDir,".."+File.separator+"..");
		String[] branchDirectoryNames = branchFoldersDir.list((current, name) ->
				(new File(current, name).isDirectory()));

		for (String dirName : branchDirectoryNames) {
			File branchFolder = new File(branchFoldersDir, dirName);
			File controlFileHandle = new File(branchFolder, controlFileInBranchDir);
			if (!controlFileHandle.delete())
				return false;
		}
		return true;
	}

	private String getTempBranchRefPathOnRemote() {
		return GIT_URI
				+File.separator+"refs"
				+File.separator+"heads"
				+File.separator+featureBranch+".someOtherName";
	}

	private String getBranchRefPathOnRemote() {
		return GIT_URI
				+File.separator+"refs"
				+File.separator+"heads"
				+File.separator+featureBranch;
	}
}
