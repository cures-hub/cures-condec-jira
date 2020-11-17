package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class TestFetch extends TestSetUpGit {

	@Test
	public void testFetchResultContainsNewCommits() {
		ConfigPersistenceManager.setGitUris("TEST", GIT_URI);
		ConfigPersistenceManager.setDefaultBranches("TEST", "master");
		gitClient = GitClient.getOrCreate("TEST");
		assertEquals(7, gitClient.getDefaultBranchCommits().size());

		makeExampleCommit("Tangled3.java",
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
		makeExampleCommit("Tangled3.java",
				"package de.uhd.ifi.se.decision.management.jira.view.treeviewer;\n" + "public class A {\n" + "\n"
						+ "    public int x;\n" + "    public int y ;\n" + "    public String z;\n" + "\n"
						+ "    public A(int x, int y, String z){\n" + "        this.x = x;\n" + "        this.y = y;\n"
						+ "        this.z = z;\n" + "    };\n" + "    public void doSomething(){\n"
						+ "        for(int i =0; i < 10; i ++){\n" + "            for(int j =0; j < 20; j++){\n"
						+ "                LOGGER.info((i+j);\n" + "            }\n" + "        }\n" + "    };\n"
						+ "    public void doOtherthing(){\n" + "        for(int i =0; i < 10; i ++){\n"
						+ "            for(int j =0; j < 20; j++){\n" + "                LOGGER.info((i+j);\n"
						+ "            }\n" + "        }\n" + "    };\n" + "\n" + "}\n",
				"TEST-42 add class A");

		gitClient = GitClient.getOrCreate("TEST");
		assertEquals(9, gitClient.getDefaultBranchCommits().size());
	}

}
