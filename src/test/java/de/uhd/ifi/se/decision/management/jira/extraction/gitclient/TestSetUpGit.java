package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;

public class TestSetUpGit extends TestSetUpWithIssues {

	protected static GitClient gitClient;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		File directory = getExampleDirectory();
		String uri = getExampleUri();
		gitClient = new GitClientImpl(uri, directory);
		makeExampleCommit("readMe.txt", "TODO Write ReadMe", "Init Commit");
		makeExampleCommit("readMe.txt", "Self-explanatory, ReadMe not necessary.", "TEST-12: Explain how the great software works");
		makeExampleCommit("GodClass.java", "public class GodClass {}", "TEST-12: Develop great software");
	}

	@Before
	public void setUp() {
		initialization();
	}

	private static File getExampleDirectory() {
		File directory = null;
		try {
			directory = File.createTempFile("clone", "");
			directory.delete();
			directory.mkdirs();
		} catch (IOException e) {

		}
		return directory;
	}

	private static String getExampleUri() {
		String uri = "";
		try {
			File remoteDir = File.createTempFile("remote", "");
			remoteDir.delete();
			remoteDir.mkdirs();
			RepositoryCache.FileKey fileKey = RepositoryCache.FileKey.exact(remoteDir, FS.DETECTED);
			Repository remoteRepo = fileKey.open(false);
			remoteRepo.create(true);
			uri = remoteRepo.getDirectory().getAbsolutePath();
		} catch (IOException e) {

		}
		return uri;
	}

	private static void makeExampleCommit(String filename, String content, String commitMessage) {
		Git git = gitClient.getGit();
		try {
			File inputFile = new File(gitClient.getDirectory().getParent(), filename);
			PrintWriter writer = new PrintWriter(inputFile, "UTF-8");
			writer.println(content);
			writer.close();
			git.add().addFilepattern(inputFile.getName()).call();
			git.commit().setMessage(commitMessage).setAuthor("gitTest", "gitTest@test.de").call();
			git.push().setRemote("origin").call();
		} catch (GitAPIException | FileNotFoundException | UnsupportedEncodingException e) {

		}
	}

	@AfterClass
	public static void tidyUp() {
		gitClient.deleteRepository();
	}
}
