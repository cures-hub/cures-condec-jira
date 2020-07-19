package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import java.io.File;

import org.junit.After;
import org.junit.Ignore;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;

@Ignore
public class TestRemoveFeatureBranchNotPresentInRemote extends TestSetUpGit {

	private GitClient testGitClient;

	private String featureBranch = "featureBranch";
	private String expectedFirstCommitMessage = "First message";

	@After
	public void after() {
		restoreFeatureBranchOnRemote();
	}

	// @Test
	// @Ignore
	// public void testGetFeatureBranchNotOnRemoteWithLocalPull() {
	// // fetches the 'default' branch commits. Do not use TestSetUpGit' gitClient.
	// List<String> uris = new ArrayList<String>();
	// uris.add(GIT_URI);
	// testGitClient = new GitClient(uris, null, "TEST");
	//
	// // "delete" branch on remote
	// assertTrue(moveFeatureBranchOnRemote());
	//
	// // do not prevent pull
	// assertTrue(resetPullControl(testGitClient.getDirectory(GIT_URI)));
	//
	// // getting branch with performing pull
	// List<RevCommit> commits =
	// testGitClient.getFeatureBranchCommits(featureBranch, GIT_URI);
	// // branch should not exist at local repo anymore
	// assertTrue(commits.size() == 0);
	// }
	//
	// @Test
	// public void testGetFeatureBranchNotOnRemoteLocalPullCache() {
	// // fetches the 'default' branch commits. Do not use TestSetUpGit' gitClient
	// List<String> uris = new ArrayList<String>();
	// uris.add(GIT_URI);
	// testGitClient = new GitClient(uris, null, "TEST");
	// File developDir = testGitClient.getDirectory(GIT_URI);
	//
	// // fetch the branch, it will be cached for a while and not automatically
	// // released to TEMP.. folder
	// List<RevCommit> commits =
	// testGitClient.getFeatureBranchCommits(featureBranch, GIT_URI);
	//
	// // "delete" branch on remote
	// assertTrue(moveFeatureBranchOnRemote());
	//
	// // Fetch the branch again, pull should not be performed due to cache
	// List<RevCommit> commitsAgain =
	// testGitClient.getFeatureBranchCommits(featureBranch, GIT_URI);
	// // branch expected to be still at local repo
	// assertEquals(4, commitsAgain.size());
	// assertEquals(commits, commitsAgain);
	// assertEquals(expectedFirstCommitMessage, commits.get(0).getFullMessage());
	//
	// // Once more fetch the branch, but with pullig from remote
	//
	// // do not prevent pull
	// assertTrue(resetPullControl(developDir));
	//
	// // branch should not exist at local repo anymore
	// assertTrue(testGitClient.getFeatureBranchCommits(featureBranch,
	// GIT_URI).size() == 0);
	// }

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
