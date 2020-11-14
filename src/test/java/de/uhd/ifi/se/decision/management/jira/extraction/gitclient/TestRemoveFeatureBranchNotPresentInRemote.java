package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

public class TestRemoveFeatureBranchNotPresentInRemote extends TestSetUpGit {

	private String expectedFirstCommitMessage = "First message";
	private Ref featureBranch;

	@After
	public void after() {
		restoreFeatureBranchOnRemote();
		featureBranch = gitClient.getBranches("featureBranch").get(0);
	}

	@Test
	@Ignore
	public void testGetFeatureBranchNotOnRemoteWithLocalPull() {
		// "delete" branch on remote
		assertTrue(moveFeatureBranchOnRemote());

		// do not prevent pull
		assertTrue(resetPullControl(gitClient.getGitClientsForSingleRepo(GIT_URI).getGitDirectory()));

		// getting branch with performing pull
		List<RevCommit> commits = gitClient.getFeatureBranchCommits(featureBranch);
		// branch should not exist at local repo anymore
		assertTrue(commits.size() == 0);
	}

	@Test
	@Ignore
	public void testGetFeatureBranchNotOnRemoteLocalPullCache() {
		File developDir = gitClient.getGitClientsForSingleRepo(GIT_URI).getGitDirectory();

		// fetch the branch, it will be cached for a while and not automatically
		// released to TEMP.. folder
		List<RevCommit> commits = gitClient.getFeatureBranchCommits(featureBranch);

		// "delete" branch on remote
		assertTrue(moveFeatureBranchOnRemote());

		// Fetch the branch again, pull should not be performed due to cache
		List<RevCommit> commitsAgain = gitClient.getFeatureBranchCommits(featureBranch);
		// branch expected to be still at local repo
		assertEquals(4, commitsAgain.size());
		assertEquals(commits, commitsAgain);
		assertEquals(expectedFirstCommitMessage, commits.get(0).getFullMessage());

		// Once more fetch the branch, but with pullig from remote

		// do not prevent pull
		assertTrue(resetPullControl(developDir));

		// branch should not exist at local repo anymore
		assertTrue(gitClient.getFeatureBranchCommits(featureBranch).isEmpty());
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
		String controlFileInBranchDir = ".git" + File.separator + "condec.pullstamp.";
		File branchFoldersDir = new File(localDir, ".." + File.separator + "..");
		String[] branchDirectoryNames = branchFoldersDir
				.list((current, name) -> (new File(current, name).isDirectory()));

		for (String dirName : branchDirectoryNames) {
			File branchFolder = new File(branchFoldersDir, dirName);
			File controlFileHandle = new File(branchFolder, controlFileInBranchDir);
			if (!controlFileHandle.delete())
				return false;
		}
		return true;
	}

	private String getTempBranchRefPathOnRemote() {
		return GIT_URI + File.separator + "refs" + File.separator + "heads" + File.separator + featureBranch
				+ ".someOtherName";
	}

	private String getBranchRefPathOnRemote() {
		return GIT_URI + File.separator + "refs" + File.separator + "heads" + File.separator + featureBranch;
	}
}
