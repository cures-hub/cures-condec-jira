package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
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
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettings;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;
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
public abstract class TestSetUpGit extends TestSetUp {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestSetUpGit.class);
	public static List<String> GIT_URIS = getExampleUris();
	protected static GitClient gitClient;
	protected MockIssue mockJiraIssueForGitTests;
	protected MockIssue mockJiraIssueForGitTestsTangled;
	protected MockIssue mockJiraIssueForGitTestsTangledSingleCommit;
	private static int commitTime = 0;

	@BeforeClass
	public static void setUpBeforeClass() {
		init();
		int iter = 0;
		String git_uri_strings = "";
		boolean pleaseContinue = false;
		for (String GIT_URI : GIT_URIS) {
			if (gitClient != null && gitClient.getGitClientsForSingleRepo(GIT_URI) != null
					&& gitClient.getGitClientsForSingleRepo(GIT_URI).getDirectory().exists()) {
				// git client already exists
				return;
			}
			ClassLoader classLoader = TestSetUpGit.class.getClassLoader();
			switch (iter) {
				case 0:
					ConfigPersistenceManager.setGitUris("TEST", GIT_URI);
					git_uri_strings = GIT_URI;
					ConfigPersistenceManager.setDefaultBranches("TEST", "master");
					ConfigPersistenceManager.setAuthMethods("TEST", "NONE");
					ConfigPersistenceManager.setUsernames("TEST", "");
					ConfigPersistenceManager.setTokens("TEST", "");	
					break;
					
				case 1:
					ConfigPersistenceManager.setGitUris("TEST", git_uri_strings + ";;" + GIT_URI);
					git_uri_strings += ";;" + GIT_URI;
					ConfigPersistenceManager.setDefaultBranches("TEST", "master;;master");
					ConfigPersistenceManager.setAuthMethods("TEST", "NONE;;HTTP");
					ConfigPersistenceManager.setUsernames("TEST", ";;httpuser");
					ConfigPersistenceManager.setTokens("TEST", ";;httppassword");	
					break;
				
				case 2:
					ConfigPersistenceManager.setGitUris("TEST", git_uri_strings + ";;" + GIT_URI);
					git_uri_strings += ";;" + GIT_URI;
					ConfigPersistenceManager.setDefaultBranches("TEST", "master;;master;;master");
					ConfigPersistenceManager.setAuthMethods("TEST", "NONE;;HTTP;;GITHUB");
					ConfigPersistenceManager.setUsernames("TEST", ";;httpuser;;githubuser");
					ConfigPersistenceManager.setTokens("TEST", ";;httppassword;;githubtoken");	
					break;
					
				case 3:
					ConfigPersistenceManager.setGitUris("TEST", git_uri_strings + ";;" + GIT_URI);
					git_uri_strings += ";;" + GIT_URI;
					ConfigPersistenceManager.setDefaultBranches("TEST", "master;;master;;master;;master");
					ConfigPersistenceManager.setAuthMethods("TEST", "NONE;;HTTP;;GITHUB;;GITLAB");
					ConfigPersistenceManager.setUsernames("TEST", ";;httpuser;;githubuser;;gitlabuser");
					ConfigPersistenceManager.setTokens("TEST", ";;httppassword;;githubtoken;;gitlabtoken");	
					break;

				default:
					return;
			}
			String pathToExtractionVCSTestFilesDir = "extraction/versioncontrol/";
			String pathToExtractionVCSTestFile = pathToExtractionVCSTestFilesDir
					+ "GitDiffedCodeExtractionManager.REPLACE-PROBLEM.FileA.java";
			String extractionVCSTestFileTargetName = "GitDiffedCodeExtractionManager.REPLACE-PROBLEM.java";
			File fileA = new File(classLoader.getResource(pathToExtractionVCSTestFile).getFile());
			gitClient = GitClient.getOrCreate("TEST");
			if (!gitClient.getDefaultBranchCommits().isEmpty()) {
				for (Ref featureBranch : gitClient.getBranches()) {
					if (pleaseContinue || gitClient.getRepoUriFromBranch(featureBranch) == GIT_URI) {
						pleaseContinue = true;
						break;
					}
				}
				if (pleaseContinue) {
					continue;
				}
			}
			System.out.println();
			// createBranch("master");
			// above line will log errors for pulling from still empty remote repositry.
			makeExampleCommit(GIT_URI, "readMe.txt", "TODO Write ReadMe", "Init Commit");
			System.out.println("FILE WITH DECISION KNOWLEDGE");
			makeExampleCommit(GIT_URI, fileA, extractionVCSTestFileTargetName, "TEST-12: File with decision knowledge");
			System.out.println("DEVELOP GREAT SOFTWARE");
			String issueKeyPrefix = "";
			if (iter > 0) {
				issueKeyPrefix = Integer.toString(iter);
			}
			System.out.println("Issue Key Prefix: " + issueKeyPrefix);
			makeExampleCommit(GIT_URI, "GodClass.java",
					"public class GodClass {"
							+ "//@issue Small code issue in GodClass, it does nothing. \t \n \t \n \t \n}",
					"TEST-" + issueKeyPrefix + "12: Develop great software");
			makeExampleCommit(GIT_URI, "Untangled.java",
					"package de.uhd.ifi.se.decision.management.jira.extraction.impl;\n" + "\n" + "public class Main {\n"
							+ "    public static void main(String[] args) {\n" + "        LOGGER.info((\"Hello World!\");\n"
							+ "    }\n" + "}\n",
					"TEST-" + issueKeyPrefix + "26 add main");
			makeExampleCommit(GIT_URI, "Untangled2.java",
					"package de.uhd.ifi.se.decision.management.jira.extraction.impl;\n" + "\n" + "public class D {\n" + "\n"
							+ "    public int a;\n" + "    public int b ;\n" + "    public String c;\n" + "\n"
							+ "    public d(){\n" + "        this.a = 18;\n" + "        this.b = 64;\n"
							+ "        this.c = \"world\";\n" + "    };\n" + "    public void printSomeThing(){\n"
							+ "        for(int i =0; i < b; i ++){\n" + "            for(int j =0; j < a; j++){\n"
							+ "                LOGGER.info((c);\n" + "            }\n" + "        }\n" + "    };\n" + "\n"
							+ "\n" + "}\n",
					"TEST-" + issueKeyPrefix + "26 add class d");

			makeExampleCommit(GIT_URI, "Tangled1.java",
					"package de.uhd.ifi.se.decision.management.jira;\n" + "public class E {\n" + "\n"
							+ "    public int a;\n" + "    public int b ;\n" + "    public String c;\n" + "\n"
							+ "    public c(){\n" + "        this.a = 22;\n" + "        this.b = 33;\n"
							+ "        this.c = \"mouse\";\n" + "    };\n" + "    public int sum(){\n"
							+ "        return a + b +c.length();\n" + "    };\n" + "\n"
							+ "    public void printSomeThing(){\n" + "        for(int i =0; i < b; i ++){\n"
							+ "            for(int j =0; j < a; j++){\n" + "                LOGGER.info((c);\n"
							+ "            }\n" + "        }\n" + "    };\n" + "\n" + "\n" + "}\n",
					"TEST-" + issueKeyPrefix + "26 add class e");

			makeExampleCommit(GIT_URI, "Tangled2.java",
					"package de.uhd.ifi.se.decision.management.jira.view.treeviewer;\n" + "public class A {\n" + "\n"
							+ "    public int x;\n" + "    public int y ;\n" + "    public String z;\n" + "\n"
							+ "    public A(int x, int y, String z){\n" + "        this.x = x;\n" + "        this.y = y;\n"
							+ "        this.z = z;\n" + "    };\n" + "    public void doSomething(){\n"
							+ "        for(int i =0; i < 10; i ++){\n" + "            for(int j =0; j < 20; j++){\n"
							+ "                LOGGER.info((i+j);\n" + "            }\n" + "        }\n" + "    };\n"
							+ "    public void doOtherthing(){\n" + "        for(int i =0; i < 10; i ++){\n"
							+ "            for(int j =0; j < 20; j++){\n" + "                LOGGER.info((i+j);\n"
							+ "            }\n" + "        }\n" + "    };\n" + "\n" + "}\n",
					"TEST-" + issueKeyPrefix + "62 add class A");
			setupBranchWithDecKnowledge(GIT_URI);
			// TODO Remove this method and only use one branch
			setupBranchForTranscriber(GIT_URI);
			iter++;
		}
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

	private static List<String> getExampleUris() {
		if (GIT_URIS != null) {
			return GIT_URIS;
		}
		List<String> uris = new ArrayList<String>();
		for (int i = 0; i < 4; i++) {
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
			uris.add(uri);
		}
		GIT_URIS = uris;
		return uris;
	}

	protected static void makeExampleCommit(String GIT_URI, File inputFile, String targetName, String commitMessage) {
		Git git = gitClient.getGitClientsForSingleRepo(GIT_URI).getGit();
		File gitFile = new File(gitClient.getGitClientsForSingleRepo(GIT_URI).getDirectory().getParent(), targetName);
		try {
			FileUtils.copyFile(inputFile, gitFile);
			git.add().addFilepattern(gitFile.getName()).call();
			PersonIdent defaultCommitter = new PersonIdent("gitTest", "gitTest@test.de");
			PersonIdent committer = new PersonIdent(defaultCommitter, new Date(commitTime));
			commitTime = commitTime + 86400;
			git.commit().setMessage(commitMessage).setAuthor(committer).setCommitter(committer).call();
			git.push().setRemote("origin").call();
			System.out.println("Created commit: " + commitMessage);
		} catch (Exception e) {
			LOGGER.error("Mock commit failed. Message: " + e.getMessage());
		}
	}

	protected static void makeExampleCommit(String GIT_URI, String filename, String content, String commitMessage) {
		Git git = gitClient.getGitClientsForSingleRepo(GIT_URI).getGit();
		try {
			File inputFile = new File(gitClient.getGitClientsForSingleRepo(GIT_URI).getDirectory().getParent(),
					filename);
			PrintWriter writer = new PrintWriter(inputFile, "UTF-8");
			writer.println(content);
			writer.close();
			git.add().addFilepattern(inputFile.getName()).call();
			PersonIdent defaultCommitter = new PersonIdent("gitTest", "gitTest@test.de");
			PersonIdent committer = new PersonIdent(defaultCommitter, new Date(commitTime));
			commitTime = commitTime + 86400;
			git.commit().setMessage(commitMessage).setAuthor(committer).setCommitter(committer).call();
			git.push().setRemote("origin").call();
			System.out.println("Created commit: " + commitMessage);
		} catch (GitAPIException | FileNotFoundException | UnsupportedEncodingException e) {
			LOGGER.error("Mock commit failed. Message: " + e.getMessage());
		}
	}

	private static void setupBranchWithDecKnowledge(String GIT_URI) {
		String firstCommitMessage = "First message";
		Git git = gitClient.getGitClientsForSingleRepo(GIT_URI).getGit();
		String currentBranch = getCurrentBranch(GIT_URI);

		ClassLoader classLoader = TestSetUpGit.class.getClassLoader();
		String pathToTestFilesDir = "extraction/versioncontrol/";
		String pathToTestFile = pathToTestFilesDir + "GitDiffedCodeExtractionManager.REPLACE-PROBLEM.FileB.java";
		String testFileTargetName = "GitDiffedCodeExtractionManager.REPLACE-PROBLEM.java";
		File fileB = new File(classLoader.getResource(pathToTestFile).getFile());

		createBranch(GIT_URI, "featureBranch");
		makeExampleCommit(GIT_URI, "readMe.featureBranch.txt", "First content", firstCommitMessage);

		makeExampleCommit(GIT_URI, "GodClass.java", "public class GodClass {" + "//@issue:code issue in GodClass" + "\r\n}",
				"Second message");

		System.out.println("HOUSTON WE HAVE A PROBLEM");
		makeExampleCommit(GIT_URI, "HermesGodClass.java",
				"public class HermesGodClass {" + "//@issue:1st code issue in one-line comment in HermesGodClass"
						+ "\r\n/*\r\n@issue:2nd issue in comment block*/" + "\r\n/**\r\n* @issue:3rd issue in javadoc"
						+ "\r\n*\r\n* @alternative:1st alt in javadoc*/" + "\r\n}",
				"TEST-12: Develop great software" + "\r\n" + "// [issue]Houston, we have a small problem...[/issue]" + "\r\n"
						+ "// [alternative]ignore it![/alternative]" + "\r\n" + "// [pro]ignorance is bliss[/pro]"
						+ "\r\n" + "// [decision]solve it ASAP![/decision]" + "\r\n"
						+ "// [pro]life is valuable, prevent even smallest risks[/pro]");
		makeExampleCommit(GIT_URI, fileB, testFileTargetName,
				"modified rationale text, reproducing replace problem observed " + "with CONDEC-505 feature branch");
		returnToPreviousBranch(currentBranch, git);
	}

	private static void setupBranchForTranscriber(String GIT_URI) {
		Git git = gitClient.getGitClientsForSingleRepo(GIT_URI).getGit();
		String currentBranch = getCurrentBranch(GIT_URI);
		createBranch(GIT_URI, "TEST-4.transcriberBranch");

		makeExampleCommit(GIT_URI, "readMe.featureBranch.txt", "", "");
		makeExampleCommit(GIT_URI, "readMe.featureBranch.txt", "", "[issue]This is an issue![/Issue] But I love pizza!");

		returnToPreviousBranch(currentBranch, git);
	}

	private static void createBranch(String GIT_URI, String branchName) {
		Git git = gitClient.getGitClientsForSingleRepo(GIT_URI).getGit();
		try {
			git.branchCreate().setName(branchName).call();
			git.checkout().setName(branchName).call();
			// git.push().setRemote("origin").setRefSpecs(new RefSpec(branchName)).call();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static String getCurrentBranch(String GIT_URI) {
		Git git = gitClient.getGitClientsForSingleRepo(GIT_URI).getGit();
		String currentBranch = null;
		try {
			currentBranch = git.getRepository().getBranch();
		} catch (Exception e) {
		}
		return currentBranch;
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
		MockPluginSettingsFactory.pluginSettings = new MockPluginSettings();
	}
}
