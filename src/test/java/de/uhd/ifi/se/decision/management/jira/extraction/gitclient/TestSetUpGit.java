package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.mock.issue.MockIssue;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;

public class TestSetUpGit extends TestSetUpWithIssues {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestSetUpGit.class);
	protected static GitClient gitClient;
	protected MockIssue mockJiraIssueForGitTests;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		File directory = getExampleDirectory();
		String uri = getExampleUri();
		gitClient = new GitClientImpl(uri, directory.getAbsolutePath(), "TEST");
		makeExampleCommit("readMe.txt", "TODO Write ReadMe", "Init Commit");
		makeExampleCommit("readMe.txt", "Self-explanatory, ReadMe not necessary.",
				"TEST-12: Explain how the great software works");
		makeExampleCommit("GodClass.java", "public class GodClass {}", "TEST-12: Develop great software");

		setupBranchWithDecKnowledge();
	}

	@Before
	public void setUp() {
		initialization();
		mockJiraIssueForGitTests = new MockIssue();
		mockJiraIssueForGitTests.setKey("TEST-12");
	}

	private static File getExampleDirectory() {
		File directory = null;
		try {
			directory = File.createTempFile("clone", "");
			directory.delete();
			directory.mkdirs();
		} catch (IOException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return uri;
	}

	protected static void makeExampleCommit(String filename, String content, String commitMessage) {
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
			LOGGER.error("Mock commit failed. Message: " + e.getMessage());
		}
	}

	private static void setupBranchWithDecKnowledge() {
		String featureBranch = "featureBranch";
		String firstCommitMessage = "First message";
		String currentBranch = null;
		Git git = gitClient.getGit();
		try {
			currentBranch = git.getRepository().getBranch();
			git.branchCreate().setName(featureBranch).call();
			git.checkout().setName(featureBranch).call();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		makeExampleCommit("readMe.featureBranch.txt"
				, "First content"
				, firstCommitMessage);

		makeExampleCommit("readMe.featureBranch.txt"
				, "Second content"
				, "Second message");

		makeExampleCommit("GodClass.java", "public class GodClass {}"
				, "TEST-12: Develop great software" +
						"//[issue]Huston we have a small problem..[/issue]" +
						"\r\n"+
						"//[alternative]ignore it![/alternative]" +
						"\r\n"+
						"//[pro]ignorance is bliss[/pro]" +
						"\r\n"+
						"//[decision]solve it ASAP![/decision]" +
						"\r\n"+
						"//[pro]life is valuable, prevent even smallest risks[/pro]"
						);

		returnToPreviousBranch(currentBranch, git);
	}

	private static void returnToPreviousBranch(String branch, Git git) {
		if (branch==null) {
			return;
		}
		else {
			try {
				git.checkout().setName(branch).call();
				git.pull();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@AfterClass
	public static void tidyUp() {
		gitClient.deleteRepository();
	}

	// helpers
	protected String getRepoUri() {
		List<RemoteConfig> remoteList = null;
		try {
			remoteList = gitClient.getGit().remoteList().call();

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		if (remoteList==null) {
			return "";
		}
		else {
			RemoteConfig remoteHead = remoteList.get(0);
			URIish uriHead = remoteHead.getURIs().get(0);

			return uriHead.toString();
		}

	}

	protected String getRepoBaseDirectory() {
		Repository repo = gitClient.getGit().getRepository();
		File dir = repo.getDirectory();
		String projectUriSomeBranchPath = dir.getAbsolutePath();
		String regExSplit = File.separator;
		if (regExSplit.equals("\\")) {
			regExSplit="\\\\";
		}
		String[] projectUriSomeBranchPathComponents = projectUriSomeBranchPath.split(regExSplit);
		String[] projectUriPathComponents = new String[projectUriSomeBranchPathComponents.length-4];
		for (int i = 0; i<projectUriPathComponents.length;i++) {
			projectUriPathComponents[i] = projectUriSomeBranchPathComponents[i];
		}
		return String.join(File.separator, projectUriPathComponents);
	}
}
