package de.uhd.ifi.se.decision.management.jira.extraction.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RefSpec;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestTaskCodeSummarizer extends TestSetUpGit{

	@Test
	public void getNoSumForNoCommits() throws IOException, GitAPIException, JSONException, InterruptedException {
		String commits = "{" + "\"commits\":[" + "" + "]" + "}";
		Map<DiffEntry, EditList> gitDiffs = GitDiffExtraction.getGitDiff(commits, projectKey, true);
		String text = TaskCodeSummarizer.summarizer(gitDiffs, projectKey, false);
		assertTrue(text.isEmpty());
	}


	@Test
	public void getNoSumForNoJavaCommits() throws IOException, GitAPIException, JSONException, InterruptedException {
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
		String text = TaskCodeSummarizer.summarizer(gitDiffs, projectKey, false);
		assertTrue(text.isEmpty());
	}

	@Test
	public void getSumForTenCommitsWithNoJava()
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
		String text = TaskCodeSummarizer.summarizer(gitDiffs, projectKey, false);
		assertTrue(text.isEmpty());
	}

	@Ignore
	@Test
	public void getSumForOneCommitWithOneJava()
			throws IOException, GitAPIException, JSONException, InterruptedException {
		// Create a new file
		git.checkout().setCreateBranch(false).setName("master").call();
		File newFile = new File(cloneDir, "myNewFileForBranch.java");
		newFile.createNewFile();
		FileUtils.writeStringToFile(newFile, "public class MyNewFile { \n" + "public void newfiling() { \n"
				+ "run(); \n" + "} \n" + "\n" + "public void run() {\n" + "} \n" + "}");
		// Commit the new file
		git.add().addFilepattern(newFile.getName()).call();
		git.commit().setMessage("First commit in branch").setAuthor("gildas", "gildas@example.com").call();

		// Push the commit on the bare repository
		refSpec = new RefSpec("master");
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
		String text = TaskCodeSummarizer.summarizer(gitDiffs, projectKey, false);
		assertEquals(text, "In class *MyNewFile* the following methods has been changed: \n" + "newfiling\n" + "run\n");
	}

	@Ignore
	@Test
	public void getSumForOneCommitWithTenJava()
			throws IOException, GitAPIException, JSONException, InterruptedException {
		// Create a new files
		git.checkout().setCreateBranch(false).setName("master").call();
		for (int i = 1; i <= 10; i++) {
			File newFile = new File(cloneDir, "myNewFileForOneCommit" + i + ".java");
			newFile.createNewFile();
			FileUtils.writeStringToFile(newFile,
					"public class MyNewFileForOneCommit" + i + " { \n" + "public void newfiling() { \n" + "run(); \n"
							+ "} \n" + "\n" + "public void run() {\n" + "} \n" + "}");
			// Commit the new file
			git.add().addFilepattern(newFile.getName()).call();
		}

		// Push the commit on the bare repository
		git.commit().setMessage("One commit for ten files").setAuthor("gildas", "gildas@example.com").call();
		refSpec = new RefSpec("master");
		git.push().setRemote("origin").setRefSpecs(refSpec).call();
		Iterable<RevCommit> revCommits = git.log().all().call();
		String commits = "{" + "\"commits\":[";
		for (RevCommit commit : revCommits) {
			if (commit.getFullMessage().equals("One commit for ten files")) {
				commits = commits + "{ commitId: \"" + commit.getName() + "\", },";
			}
		}
		commits = commits + "]" + "}";
		Map<DiffEntry, EditList> gitDiffs = GitDiffExtraction.getGitDiff(commits, projectKey, true);
		String text = TaskCodeSummarizer.summarizer(gitDiffs, projectKey, false);
		String eq1 = "In class *MyNewFileForOneCommit1* the following methods has been changed: \n" + "newfiling\n"
				+ "run\n";
		String eq2 = "In class *MyNewFileForOneCommit2* the following methods has been changed: \n" + "newfiling\n"
				+ "run\n";
		String eq3 = "In class *MyNewFileForOneCommit3* the following methods has been changed: \n" + "newfiling\n"
				+ "run\n";
		String eq4 = "In class *MyNewFileForOneCommit4* the following methods has been changed: \n" + "newfiling\n"
				+ "run\n";
		String eq5 = "In class *MyNewFileForOneCommit5* the following methods has been changed: \n" + "newfiling\n"
				+ "run\n";
		String eq6 = "In class *MyNewFileForOneCommit6* the following methods has been changed: \n" + "newfiling\n"
				+ "run\n";
		String eq7 = "In class *MyNewFileForOneCommit7* the following methods has been changed: \n" + "newfiling\n"
				+ "run\n";
		String eq8 = "In class *MyNewFileForOneCommit8* the following methods has been changed: \n" + "newfiling\n"
				+ "run\n";
		String eq9 = "In class *MyNewFileForOneCommit9* the following methods has been changed: \n" + "newfiling\n"
				+ "run\n";
		String eq10 = "In class *MyNewFileForOneCommit10* the following methods has been changed: \n" + "newfiling\n"
				+ "run\n";
		assertTrue(text.contains(eq1));
		assertTrue(text.contains(eq2));
		assertTrue(text.contains(eq3));
		assertTrue(text.contains(eq4));
		assertTrue(text.contains(eq5));
		assertTrue(text.contains(eq6));
		assertTrue(text.contains(eq7));
		assertTrue(text.contains(eq8));
		assertTrue(text.contains(eq9));
		assertTrue(text.contains(eq10));
	}

	@Ignore
	@Test
	public void getSumForTenCommitsWithTenJava()
			throws IOException, GitAPIException, JSONException, InterruptedException {
		// Create a new files
		git.checkout().setCreateBranch(false).setName("master").call();
		for (int i = 1; i <= 10; i++) {
			File newFile = new File(cloneDir, "myNewFileForBranch" + i + ".java");
			newFile.createNewFile();
			FileUtils.writeStringToFile(newFile, "public class MyNewFile" + i + " { \n" + "public void newfiling() { \n"
					+ "run(); \n" + "} \n" + "\n" + "public void run() {\n" + "} \n" + "}");
			// Commit the new file
			git.add().addFilepattern(newFile.getName()).call();
			git.commit().setMessage("Ten commits in branch").setAuthor("gildas", "gildas@example.com").call();
		}

		// Push the commit on the bare repository
		refSpec = new RefSpec("master");
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
		String text = TaskCodeSummarizer.summarizer(gitDiffs, projectKey, false);
		String eq1 = "In class *MyNewFile1* the following methods has been changed: \n" + "newfiling\n" + "run\n";
		String eq2 = "In class *MyNewFile2* the following methods has been changed: \n" + "newfiling\n" + "run\n";
		String eq3 = "In class *MyNewFile3* the following methods has been changed: \n" + "newfiling\n" + "run\n";
		String eq4 = "In class *MyNewFile4* the following methods has been changed: \n" + "newfiling\n" + "run\n";
		String eq5 = "In class *MyNewFile5* the following methods has been changed: \n" + "newfiling\n" + "run\n";
		String eq6 = "In class *MyNewFile6* the following methods has been changed: \n" + "newfiling\n" + "run\n";
		String eq7 = "In class *MyNewFile7* the following methods has been changed: \n" + "newfiling\n" + "run\n";
		String eq8 = "In class *MyNewFile8* the following methods has been changed: \n" + "newfiling\n" + "run\n";
		String eq9 = "In class *MyNewFile9* the following methods has been changed: \n" + "newfiling\n" + "run\n";
		String eq10 = "In class *MyNewFile10* the following methods has been changed: \n" + "newfiling\n" + "run\n";
		assertTrue(text.contains(eq1));
		assertTrue(text.contains(eq2));
		assertTrue(text.contains(eq3));
		assertTrue(text.contains(eq4));
		assertTrue(text.contains(eq5));
		assertTrue(text.contains(eq6));
		assertTrue(text.contains(eq7));
		assertTrue(text.contains(eq8));
		assertTrue(text.contains(eq9));
		assertTrue(text.contains(eq10));
	}

	@Test
	public void getSumForTenCommitsWithOneJava()
			throws IOException, GitAPIException, JSONException, InterruptedException {
		// Create a new files
		git.checkout().setCreateBranch(false).setName("master").call();
		File newFile = new File(cloneDir, "fileForTenCommits.java");
		newFile.createNewFile();
		String stringClass = "public class FileForTenCommits { \n" + "public void newfiling() { \n" + "run(); \n"
				+ "} \n" + "\n" + "public void run() {\n" + "} \n" + "}";
		FileUtils.writeStringToFile(newFile, stringClass);
		git.add().addFilepattern(newFile.getName()).call();
		git.commit().setMessage("Ten commits for one file").setAuthor("gildas", "gildas@example.com").call();
		for (int i = 1; i <= 10; i++) {
			stringClass = stringClass.substring(0, stringClass.length() - 2);
			stringClass = stringClass + " public void newMethod" + i + "() { \n" + "run(); \n" + "} \n" + "}";
			FileUtils.write(newFile, stringClass);
			// Commit the new file
			git.add().addFilepattern(newFile.getName()).call();
			git.commit().setMessage("Ten commits for one file").setAuthor("gildas", "gildas@example.com").call();
		}

		// Push the commit on the bare repository
		refSpec = new RefSpec("master");
		git.push().setRemote("origin").setRefSpecs(refSpec).call();
		Iterable<RevCommit> revCommits = git.log().all().call();
		String commits = "{" + "\"commits\":[";
		for (RevCommit commit : revCommits) {
			if (commit.getFullMessage().equals("Ten commits for one file")) {
				commits = commits + "{ commitId: \"" + commit.getName() + "\", },";
			}
		}
		commits = commits + "]" + "}";
		Map<DiffEntry, EditList> gitDiffs = GitDiffExtraction.getGitDiff(commits, projectKey, true);
		String text = TaskCodeSummarizer.summarizer(gitDiffs, projectKey, false);
		String eq = "In class *FileForTenCommits* the following methods has been changed: \n" + "newfiling\n" + "run\n"
				+ "newMethod1\n" + "newMethod2\n" + "newMethod3\n" + "newMethod4\n" + "newMethod5\n" + "newMethod6\n"
				+ "newMethod7\n" + "newMethod8\n" + "newMethod9\n" + "newMethod10\n";
		assertEquals(text, eq);

	}

	@AfterClass
	public static void tearDown() throws InterruptedException {
		GitClient.closeAndDeleteRepo();
	}
}
