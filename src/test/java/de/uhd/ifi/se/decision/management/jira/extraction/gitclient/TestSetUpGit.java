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

		/*
		* following mockcommits are for TangledCommitDedetection
		* 1. and 2. commit are not tangled commits
		* 3. and 4. commits are tangled commits, but the package distance of 4. commit is greater than 3
		* excepted order on codeSummarize: 4->3->2->1
		* */

		makeExampleCommit("untangled.java", "package de.uhd.ifi.se.decision.management.jira.extraction.impl;\n" +
				"\n" +
				"public class Main {\n" +
				"    public static void  main(String[] args) {\n" +
				"        System.out.println(\"Hello World!\");\n" +
				"    }\n" +
				"}\n", "Test-66 add main");
		makeExampleCommit("untangled2.java", "package de.uhd.ifi.se.decision.management.jira.extraction.impl;\n" +
				"\n" +
				"public class D {\n" +
				"\n" +
				"    public int a;\n" +
				"    public int b ;\n" +
				"    public String c;\n" +
				"\n" +
				"    public d(){\n" +
				"        this.a = 18;\n" +
				"        this.b = 64;\n" +
				"        this.c = \"world\";\n" +
				"    };\n" +
				"    public void printSomeThing(){\n" +
				"        for(int i =0; i < b; i ++){\n" +
				"            for(int j =0; j < a; j++){\n" +
				"                System.out.println(c);\n" +
				"            }\n" +
				"        }\n" +
				"    };\n" +
				"\n" +
				"\n" +
				"}\n", "Test-66 add class d");
		makeExampleCommit("tangled1.java", "package de.uhd.ifi.se.decision.management.jira.config;\n" +
				"public class E {\n" +
						"\n" +
						"    public int a;\n" +
						"    public int b ;\n" +
						"    public String c;\n" +
						"\n" +
						"    public c(){\n" +
						"        this.a = 22;\n" +
						"        this.b = 33;\n" +
						"        this.c = \"mouse\";\n" +
						"    };\n" +
						"    public int sum(){\n" +
						"        return a + b +c.length();\n" +
						"    };\n" +
						"\n" +
						"    public void printSomeThing(){\n" +
						"        for(int i =0; i < b; i ++){\n" +
						"            for(int j =0; j < a; j++){\n" +
						"                System.out.println(c);\n" +
						"            }\n" +
						"        }\n" +
						"    };\n" +
						"\n" +
						"\n" +
						"}\n"
				, "Test-66 add class e");

		makeExampleCommit("tangled2.java", "package de.uhd.ifi.se.decision.management.jira.view.treeviewer;\n" +
				"public class A {\n" +
				"\n" +
				"    public int x;\n" +
				"    public int y ;\n" +
				"    public String z;\n" +
				"\n" +
				"    public A(int x, int y, String z){\n" +
				"        this.x = x;\n" +
				"        this.y = y;\n" +
				"        this.z = z;\n" +
				"    };\n" +
				"    public void doSomething(){\n" +
				"        for(int i =0; i < 10; i ++){\n" +
				"            for(int j =0; j < 20; j++){\n" +
				"                System.out.println(i+j);\n" +
				"            }\n" +
				"        }\n" +
				"    };\n" +
				"    public void doOtherthing(){\n" +
				"        for(int i =0; i < 10; i ++){\n" +
				"            for(int j =0; j < 20; j++){\n" +
				"                System.out.println(i+j);\n" +
				"            }\n" +
				"        }\n" +
				"    };\n" +
				"\n" +
				"}\n" , "Test-66 add class A");
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
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tidyUp() {
		gitClient.deleteRepository();
	}
}
