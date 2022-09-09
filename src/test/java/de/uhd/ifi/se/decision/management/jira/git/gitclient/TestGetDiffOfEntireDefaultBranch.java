package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.GitClient;
import de.uhd.ifi.se.decision.management.jira.git.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.config.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.git.model.FileType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class TestGetDiffOfEntireDefaultBranch extends TestSetUpGit {

	@Test
	public void testAllDefaultBranchCommits() {
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();

		List<RevCommit> allCommits = diff.getCommits();
		assertEquals(6, allCommits.size());

		assertEquals(5, diff.getChangedFiles().size());
		ChangedFile extractedClass = diff.getChangedFiles().get(2);
		assertEquals("Tangled2.java", extractedClass.getName());
		assertEquals(1, extractedClass.getCommits().size());
		assertEquals("TEST-30", extractedClass.getJiraIssueKeys().iterator().next());

		assertEquals(12, diff.getCodeElements().size());
		assertEquals(0, diff.getCommitElements().size());
	}

	@Test
	public void testNoFileTypesConfigured() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("CONDEC");
		gitConfig.setFileTypesToExtract(new ArrayList<>());
		gitConfig.setActivated(true);
		gitConfig.addGitRepoConfiguration(
				new GitRepositoryConfiguration(SECURE_GIT_URIS.get(0), "master", "NONE", "", ""));
		ConfigPersistenceManager.saveGitConfiguration("CONDEC", gitConfig);

		assertTrue(ConfigPersistenceManager.getGitConfiguration("CONDEC").getFileTypesToExtract().isEmpty());
		Diff diff = GitClient.getInstance("CONDEC").getDiffOfEntireDefaultBranch();

		List<RevCommit> allCommits = diff.getCommits();
		assertEquals(6, allCommits.size());

		assertEquals(0, diff.getChangedFiles().size());
	}

	@Test
	public void testTwoFileTypesConfigured() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("CONDEC");
		gitConfig.setFileTypesToExtract(List.of(FileType.java(), FileType.javascript()));
		gitConfig.setActivated(true);
		gitConfig.addGitRepoConfiguration(
				new GitRepositoryConfiguration(SECURE_GIT_URIS.get(0), "master", "NONE", "", ""));
		ConfigPersistenceManager.saveGitConfiguration("CONDEC", gitConfig);

		assertEquals(2, ConfigPersistenceManager.getGitConfiguration("CONDEC").getFileTypesToExtract().size());
		Diff diff = GitClient.getInstance("CONDEC").getDiffOfEntireDefaultBranch();

		List<RevCommit> allCommits = diff.getCommits();
		assertEquals(6, allCommits.size());

		assertEquals(5, diff.getChangedFiles().size());
	}
}