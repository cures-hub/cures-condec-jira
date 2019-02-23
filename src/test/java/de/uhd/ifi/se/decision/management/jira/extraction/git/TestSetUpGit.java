package de.uhd.ifi.se.decision.management.jira.extraction.git;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;
import org.junit.Before;
import org.junit.BeforeClass;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;

public class TestSetUpGit extends TestSetUpWithIssues {

	protected static GitClient gitClient;

	public static String getExampleUri() {
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
			e.printStackTrace();
		}
		return uri;
	}

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		File directory = File.createTempFile("clone", "");
		directory.delete();
		directory.mkdirs();

		String uri = getExampleUri();
		gitClient = new GitClientImpl(uri, directory);
		Git git = gitClient.getGit();

		try {
			File inputFile = new File(gitClient.getDirectory(), "readMe.txt");
			if (inputFile.exists()) {
				PrintWriter writer = new PrintWriter(inputFile.getName(), "UTF-8");
				writer.println("New input in this File");
				writer.close();
			}
			git.add().addFilepattern(inputFile.getName()).call();
			git.commit().setMessage("TEST-12: wuofhewiuefghpwefg").setAuthor("gitTest", "gitTest@test..de").call();
			git.push().setRemote("origin").call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	@Before
	public void setUp() {
		initialization();

	}
}
