package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.mock.issue.MockIssue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * @issue Should we have one or more git repositories for testing?
 * @decision We have one mock git repository for testing!
 * @pro The mock git repository can be easily accessed using the Plugin Settings
 *      (see ConfigPersistenceManager class).
 * @pro This is more efficient than recreating the test git repository all the
 *      time.
 * @con Changes to the git repository (e.g. new commits) during testing has an
 *      effect on the test cases that follow. The order of test cases is
 *      arbitrary.
 * @alternative We could have more than one mock git repositories for testing!
 * @con we do not have time for it at the moment..
 */
public abstract class TestSetUpGitHTTP extends TestSetUp {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestSetUpGit.class);
	public static String GIT_URI = getExampleUri();
	protected static GitClient gitClient;
	protected MockIssue mockJiraIssueForGitTests;
	protected MockIssue mockJiraIssueForGitTestsTangled;
	protected MockIssue mockJiraIssueForGitTestsTangledSingleCommit;

	@BeforeClass
	public static void setUpBeforeClass() {
		init();
		if (gitClient != null && gitClient.getGitClientsForSingleRepo(GIT_URI) != null
				&& gitClient.getGitClientsForSingleRepo(GIT_URI).getDirectory().exists()) {
			// git client already exists
			return;
		}
		ClassLoader classLoader = TestSetUpGit.class.getClassLoader();
		String pathToExtractionVCSTestFilesDir = "extraction/versioncontrol/";
		String pathToExtractionVCSTestFile = pathToExtractionVCSTestFilesDir
				+ "GitDiffedCodeExtractionManager.REPLACE-PROBLEM.FileA.java";
		String extractionVCSTestFileTargetName = "GitDiffedCodeExtractionManager.REPLACE-PROBLEM.java";
		File fileA = new File(classLoader.getResource(pathToExtractionVCSTestFile).getFile());
		List<String> uris = new ArrayList<String>();
		uris.add(GIT_URI);
		ConfigPersistenceManager.setGitUris("TEST", GIT_URI);
		ConfigPersistenceManager.setDefaultBranches("TEST", "master");
		ConfigPersistenceManager.setAuthMethods("TEST", "HTTP");
		ConfigPersistenceManager.setUsernames("TEST", "user");
		ConfigPersistenceManager.setTokens("TEST", "secret👀");
		gitClient = GitClient.getOrCreate("TEST");
		// above line will log errors for pulling from still empty remote repositry.
		makeExampleCommit("readMe.txt", "TODO Write ReadMe", "Init Commit");
		makeExampleCommit(fileA, extractionVCSTestFileTargetName, "TEST-12: File with decision knowledge");
		makeExampleCommit("GodClass.java",
				"public class GodClass {"
						+ "//@issue Small code issue in GodClass, it does nothing. \t \n \t \n \t \n}",
				"TEST-12: Develop great software");
		makeExampleCommit("Untangled.java",
				"package de.uhd.ifi.se.decision.management.jira.extraction.impl;\n" + "\n" + "public class Main {\n"
						+ "    public static void main(String[] args) {\n" + "        LOGGER.info((\"Hello World!\");\n"
						+ "    }\n" + "}\n",
				"TEST-26 add main");
		makeExampleCommit("Untangled2.java",
				"package de.uhd.ifi.se.decision.management.jira.extraction.impl;\n" + "\n" + "public class D {\n" + "\n"
						+ "    public int a;\n" + "    public int b ;\n" + "    public String c;\n" + "\n"
						+ "    public d(){\n" + "        this.a = 18;\n" + "        this.b = 64;\n"
						+ "        this.c = \"world\";\n" + "    };\n" + "    public void printSomeThing(){\n"
						+ "        for(int i =0; i < b; i ++){\n" + "            for(int j =0; j < a; j++){\n"
						+ "                LOGGER.info((c);\n" + "            }\n" + "        }\n" + "    };\n" + "\n"
						+ "\n" + "}\n",
				"TEST-26 add class d");

		makeExampleCommit("Tangled1.java",
				"package de.uhd.ifi.se.decision.management.jira;\n" + "public class E {\n" + "\n"
						+ "    public int a;\n" + "    public int b ;\n" + "    public String c;\n" + "\n"
						+ "    public c(){\n" + "        this.a = 22;\n" + "        this.b = 33;\n"
						+ "        this.c = \"mouse\";\n" + "    };\n" + "    public int sum(){\n"
						+ "        return a + b +c.length();\n" + "    };\n" + "\n"
						+ "    public void printSomeThing(){\n" + "        for(int i =0; i < b; i ++){\n"
						+ "            for(int j =0; j < a; j++){\n" + "                LOGGER.info((c);\n"
						+ "            }\n" + "        }\n" + "    };\n" + "\n" + "\n" + "}\n",
				"TEST-26 add class e");

		makeExampleCommit("Tangled2.java",
				"package de.uhd.ifi.se.decision.management.jira.view.treeviewer;\n" + "public class A {\n" + "\n"
						+ "    public int x;\n" + "    public int y ;\n" + "    public String z;\n" + "\n"
						+ "    public A(int x, int y, String z){\n" + "        this.x = x;\n" + "        this.y = y;\n"
						+ "        this.z = z;\n" + "    };\n" + "    public void doSomething(){\n"
						+ "        for(int i =0; i < 10; i ++){\n" + "            for(int j =0; j < 20; j++){\n"
						+ "                LOGGER.info((i+j);\n" + "            }\n" + "        }\n" + "    };\n"
						+ "    public void doOtherthing(){\n" + "        for(int i =0; i < 10; i ++){\n"
						+ "            for(int j =0; j < 20; j++){\n" + "                LOGGER.info((i+j);\n"
						+ "            }\n" + "        }\n" + "    };\n" + "\n" + "}\n",
				"TEST-62 add class A");
		setupBranchWithDecKnowledge();
		// TODO Remove this method and only use one branch
		setupBranchForTranscriber();
	}

	@Before
	public void setUp() {
		init();
		mockJiraIssueForGitTests = new MockIssue();
		mockJiraIssueForGitTestsTangled = new MockIssue();
		mockJiraIssueForGitTestsTangledSingleCommit = new MockIssue();
		mockJiraIssueForGitTests.setKey("TEST-12");
		mockJiraIssueForGitTestsTangled.setKey("TEST-26");
		mockJiraIssueForGitTestsTangledSingleCommit.setKey("TEST-62");
	}

	private static String getExampleUri() {
		if (GIT_URI != null) {
			return GIT_URI;
		}
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
			LOGGER.error(e.getMessage());
		}
		GIT_URI = uri;
		return uri;
	}

	protected static void makeExampleCommit(File inputFile, String targetName, String commitMessage) {
		Git git = gitClient.getGitClientsForSingleRepo(GIT_URI).getGit();
		File gitFile = new File(gitClient.getGitClientsForSingleRepo(GIT_URI).getDirectory().getParent(), targetName);
		try {
			FileUtils.copyFile(inputFile, gitFile);
			git.add().addFilepattern(gitFile.getName()).call();
			git.commit().setMessage(commitMessage).setAuthor("gitTest", "gitTest@test.de").call();
			git.push().setRemote("origin").call();
		} catch (Exception e) {
			LOGGER.error("Mock commit failed. Message: " + e.getMessage());
		}
	}

	protected static void makeExampleCommit(String filename, String content, String commitMessage) {
		Git git = gitClient.getGitClientsForSingleRepo(GIT_URI).getGit();
		try {
			File inputFile = new File(gitClient.getGitClientsForSingleRepo(GIT_URI).getDirectory().getParent(),
					filename);
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
		Git git = gitClient.getGitClientsForSingleRepo(GIT_URI).getGit();
		String currentBranch = null;

		ClassLoader classLoader = TestSetUpGit.class.getClassLoader();
		String pathToTestFilesDir = "extraction/versioncontrol/";
		String pathToTestFile = pathToTestFilesDir + "GitDiffedCodeExtractionManager.REPLACE-PROBLEM.FileB.java";
		String testFileTargetName = "GitDiffedCodeExtractionManager.REPLACE-PROBLEM.java";
		File fileB = new File(classLoader.getResource(pathToTestFile).getFile());

		// TODO: Check how can we create a mock feature branch?
		try {
			currentBranch = git.getRepository().getBranch();
			git.branchCreate().setName(featureBranch).call();
			git.checkout().setName(featureBranch).call();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		makeExampleCommit("readMe.featureBranch.txt", "First content", firstCommitMessage);

		makeExampleCommit("GodClass.java", "public class GodClass {" + "//@issue:code issue in GodClass" + "\r\n}",
				"Second message");

		makeExampleCommit("HermesGodClass.java",
				"public class HermesGodClass {" + "//@issue:1st code issue in one-line comment in HermesGodClass"
						+ "\r\n/*\r\n@issue:2nd issue in comment block*/" + "\r\n/**\r\n* @issue:3rd issue in javadoc"
						+ "\r\n*\r\n* @alternative:1st alt in javadoc*/" + "\r\n}",
				"TEST-12: Develop great software" + "//[issue]Huston we have a small problem..[/issue]" + "\r\n"
						+ "//[alternative]ignore it![/alternative]" + "\r\n" + "//[pro]ignorance is bliss[/pro]"
						+ "\r\n" + "//[decision]solve it ASAP![/decision]" + "\r\n"
						+ "//[pro]life is valuable, prevent even smallest risks[/pro]");
		makeExampleCommit(fileB, testFileTargetName,
				"modified rationale text, reproducing replace problem observed " + "with CONDEC-505 feature branch");
		returnToPreviousBranch(currentBranch, git);
	}

	private static void setupBranchForTranscriber() {
		String featureBranch = "TEST-4.transcriberBranch";
		Git git = gitClient.getGitClientsForSingleRepo(GIT_URI).getGit();
		String currentBranch = null;

		try {
			currentBranch = git.getRepository().getBranch();
			git.branchCreate().setName(featureBranch).call();
			git.checkout().setName(featureBranch).call();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		makeExampleCommit("readMe.featureBranch.txt", "", "");
		makeExampleCommit("readMe.featureBranch.txt", "", "[issue]This is an issue![/issue]");
		makeExampleCommit("readMe.featureBranch.txt", "", "[Issue]This is an issue![/Issue]");
		makeExampleCommit("readMe.featureBranch.txt", "", "[issue]This is an issue![/Issue]");
		makeExampleCommit("readMe.featureBranch.txt", "", "[issue]This is an issue![/Issue] But I love pizza!");

		returnToPreviousBranch(currentBranch, git);
	}

	private static void returnToPreviousBranch(String branch, Git git) {
		if (branch == null) {
			return;
		} else {
			try {
				git.checkout().setName(branch).call();
				git.pull();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@AfterClass
	public static void tidyUp() {
		// gitClient.closeAll();
		// gitClient.deleteRepositories();
	}
}
