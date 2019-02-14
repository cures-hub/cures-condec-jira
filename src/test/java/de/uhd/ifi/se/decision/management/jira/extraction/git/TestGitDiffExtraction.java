package de.uhd.ifi.se.decision.management.jira.extraction.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.util.FS;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;

public class TestGitDiffExtraction {

	private static String projectKey;

	private static String directory;

	private static Git git;

	private static File cloneDir;

	private static RefSpec refSpec;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@BeforeClass
	public static void setUp() throws IllegalStateException, GitAPIException, IOException, JSONException,
			InterruptedException, URISyntaxException {
		ProjectManager a = ComponentAccessor.getProjectManager();
		Project diffProject = new MockProject(5, "GETDIFF");
		((MockProject) diffProject).setKey("GETDIFF");
		((MockProjectManager) a).addProject(diffProject);

		projectKey = a.getProjectByCurrentKey("GETDIFF").getKey();

		File file = new File(
				System.getProperty("user.home") + File.separator + "repository" + File.separator + projectKey);
		if (file.exists()) {
			FileUtils.deleteDirectory(file);
		}

		// Create a folder in the temp folder that will act as the remote repository
		File remoteDir = File.createTempFile("remote", "");
		remoteDir.delete();
		remoteDir.mkdirs();

		// Create a bare repository
		FileKey fileKey = FileKey.exact(remoteDir, FS.DETECTED);
		Repository remoteRepo = fileKey.open(false);
		remoteRepo.create(true);

		// Clone the bare repository
		cloneDir = File.createTempFile("clone", "");
		cloneDir.delete();
		cloneDir.mkdirs();
		git = Git.cloneRepository().setURI(remoteRepo.getDirectory().getAbsolutePath()).setDirectory(cloneDir)
				.setBranch("master").call();
		git.getRepository();

		StoredConfig config = git.getRepository().getConfig();
		config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, "master", "remote", "origin");
		config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, "master", "merge", "refs/heads/master");
		config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, "Branch", "remote", "origin");
		config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, "Branch", "merge", "refs/heads/Branch");
		config.save();
		directory = remoteRepo.getDirectory().getAbsolutePath();

		// Create a new file
		File newFile = new File(cloneDir, "readMe.txt");
		newFile.createNewFile();
		FileUtils.writeStringToFile(newFile, "Test content file");
		// Commit the new file
		git.add().addFilepattern(newFile.getName()).call();
		git.commit().setMessage("FirstFile").setAuthor("gildas", "gildas@example.com").call();

		// Push the commit on the bare repository
		refSpec = new RefSpec("master");
		git.push().setRemote("origin").setRefSpecs(refSpec).call();

		GitClient.getGitRepo(directory, projectKey);
		git.checkout().setCreateBranch(true).setName("Branch").call();
		Thread.sleep(2000);
	}

	@AfterClass
	public static void tearDown() {
		GitClient.closeAndDeleteRepo();
	}

	@Ignore
	@Test
	public void getNoDiffsForNoCommits() throws IOException, GitAPIException, JSONException, InterruptedException {
		String commits = "{" + "\"commits\":[" + "" + "]" + "}";
		Map<DiffEntry, EditList> gitDiffs = GitDiffExtraction.getGitDiff(commits, projectKey, true);
		assertTrue(gitDiffs == null);
	}

	@Ignore
	@Test
	public void getDiffsForOneCommitInMaster()
			throws IOException, GitAPIException, JSONException, InterruptedException {
		// Create a new file
		git.checkout().setCreateBranch(false).setName("master").call();
		File newFile = new File(cloneDir.getAbsolutePath(), "myNewFile.txt");
		newFile.createNewFile();
		FileUtils.writeStringToFile(newFile, "Test content file");
		// Commit the new file
		git.add().addFilepattern(newFile.getName()).call();
		git.commit().setMessage("First commit").setAuthor("gildas", "gildas@example.com").call();

		// Push the commit on the bare repository
		refSpec = new RefSpec("master");
		git.push().setRemote("origin").setRefSpecs(refSpec).call();
		Iterable<RevCommit> revCommits = git.log().all().call();
		String commits = "{" + "\"commits\":[";
		for (RevCommit commit : revCommits) {
			if (commit.getFullMessage().equals("First commit")) {
				commits = commits + "{ commitId: \"" + commit.getName() + "\", },";
			}
		}
		commits = commits + "]" + "}";
		Map<DiffEntry, EditList> gitDiffs = GitDiffExtraction.getGitDiff(commits, projectKey, true);
		assertEquals(gitDiffs.size(), 1);
	}

	@Ignore
	@Test
	public void getDiffsForTenCommitsInMaster()
			throws IOException, GitAPIException, JSONException, InterruptedException {
		// Create a new files
		git.checkout().setCreateBranch(false).setName("master").call();
		for (int i = 1; i <= 10; i++) {
			File newFile = new File(cloneDir, "myNewFile" + i + ".txt");
			newFile.createNewFile();
			FileUtils.writeStringToFile(newFile, "Test content file");
			// Commit the new file
			git.add().addFilepattern(newFile.getName()).call();
			git.commit().setMessage("Ten commits in Master").setAuthor("gildas", "gildas@example.com").call();
		}

		// Push the commit on the bare repository
		refSpec = new RefSpec("master");
		git.push().setRemote("origin").setRefSpecs(refSpec).call();
		Iterable<RevCommit> revCommits = git.log().all().call();
		String commits = "{" + "\"commits\":[";
		for (RevCommit commit : revCommits) {
			if (commit.getFullMessage().equals("Ten commits in Master")) {
				commits = commits + "{ commitId: \"" + commit.getName() + "\", },";
			}
		}
		commits = commits + "]" + "}";
		Map<DiffEntry, EditList> gitDiffs = GitDiffExtraction.getGitDiff(commits, projectKey, true);
		assertEquals(gitDiffs.size(), 10);
	}

	@Ignore
	@Test
	public void getDiffsForOneCommitInBranch()
			throws IOException, GitAPIException, JSONException, InterruptedException {
		// Create a new file
		git.checkout().setCreateBranch(false).setName("Branch").call();
		File newFile = new File(cloneDir, "myNewFileForBranch.txt");
		newFile.createNewFile();
		FileUtils.writeStringToFile(newFile, "No content");
		// Commit the new file
		git.add().addFilepattern(newFile.getName()).call();
		git.commit().setMessage("First commit in branch").setAuthor("gildas", "gildas@example.com").call();

		// Push the commit on the bare repository
		refSpec = new RefSpec("Branch");
		git.push().setRemote("origin").setRefSpecs(refSpec).call();
		Iterable<RevCommit> revCommits = git.log().all().call();
		String commits = "{" + "\"commits\":[";
		for (RevCommit commit : revCommits) {
			if (commit.getFullMessage().equals("First commit in branch")) {
				commits = commits + "{ commitId: \"" + commit.getName() + "\", },";
			}
		}
		commits = commits + "]" + "}";
		Map<DiffEntry, EditList> gitDiffs = GitDiffExtraction.getGitDiff(commits, projectKey, true);
		assertEquals(gitDiffs.size(), 1);
	}

	@Ignore
	@Test
	public void getDiffsForTenCommitsInBranch()
			throws IOException, GitAPIException, JSONException, InterruptedException {
		// Create a new files
		git.checkout().setCreateBranch(false).setName("Branch").call();
		for (int i = 1; i <= 10; i++) {
			File newFile = new File(cloneDir, "myNewFileForBranch" + i + ".txt");
			newFile.createNewFile();
			FileUtils.writeStringToFile(newFile, "No content");
			// Commit the new file
			git.add().addFilepattern(newFile.getName()).call();
			git.commit().setMessage("Ten commits in branch").setAuthor("gildas", "gildas@example.com").call();
		}

		// Push the commit on the bare repository
		refSpec = new RefSpec("Branch");
		git.push().setRemote("origin").setRefSpecs(refSpec).call();
		Iterable<RevCommit> revCommits = git.log().all().call();
		String commits = "{" + "\"commits\":[";
		for (RevCommit commit : revCommits) {
			if (commit.getFullMessage().equals("Ten commits in branch")) {
				commits = commits + "{ commitId: \"" + commit.getName() + "\", },";
			}
		}
		commits = commits + "]" + "}";
		Map<DiffEntry, EditList> gitDiffs = GitDiffExtraction.getGitDiff(commits, projectKey, true);
		assertEquals(gitDiffs.size(), 10);
	}
}
