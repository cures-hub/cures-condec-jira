package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.model.git.FileType;

public class TestGitConfiguration {
	private GitConfiguration gitConfig;

	@Before
	public void setUp() {
		gitConfig = new GitConfiguration();
	}

	@Test
	public void testActivated() {
		gitConfig.setActivated(true);
		assertTrue(gitConfig.isActivated());
	}

	@Test
	public void testGitRepositoryConfigurations() {
		GitRepositoryConfiguration gitRepositoryConfiguration = new GitRepositoryConfiguration(TestSetUpGit.GIT_URI,
				"master", "HTTP", "heinz.guenther", "P@ssw0rd!");
		gitConfig.addGitRepoConfiguration(gitRepositoryConfiguration);
		List<GitRepositoryConfiguration> gitRepositoryConfigurations = gitConfig.getGitRepoConfigurations();
		assertEquals(1, gitRepositoryConfigurations.size());
		assertEquals("master", gitRepositoryConfigurations.get(0).getDefaultBranch());
		assertEquals("HTTP", gitRepositoryConfigurations.get(0).getAuthMethod());
		assertEquals("heinz.guenther", gitRepositoryConfigurations.get(0).getUsername());
		assertEquals("P@ssw0rd!", gitRepositoryConfigurations.get(0).getToken());
	}

	@Test
	public void testCodeFileEndings() {
		Map<String, String> codeFileEndingMap = new HashMap<String, String>();
		codeFileEndingMap.put("JAVA_C", "java, c++, C");
		codeFileEndingMap.put("PYTHON", "py");
		codeFileEndingMap.put("HTML", "js, ts");
		gitConfig.setCodeFileEndings(codeFileEndingMap);
		List<String> codeFileEndingsJavaC = Arrays.asList(gitConfig.getCodeFileEndings("JAVA_C").split(", "));
		List<String> codeFileEndingsPython = Arrays.asList(gitConfig.getCodeFileEndings("PYTHON").split(", "));
		List<String> codeFileEndingsHTML = Arrays.asList(gitConfig.getCodeFileEndings("HTML").split(", "));
		assertTrue(codeFileEndingsJavaC.contains("java"));
		assertTrue(codeFileEndingsJavaC.contains("c++"));
		assertTrue(codeFileEndingsJavaC.contains("c"));
		assertTrue(codeFileEndingsPython.contains("py"));
		assertTrue(codeFileEndingsHTML.contains("js"));
		assertTrue(codeFileEndingsHTML.contains("ts"));
		assertTrue(gitConfig.getCodeFileEndings("TEX").equals(""));
		assertEquals(6, gitConfig.getFileTypesToExtract().size());
	}

	@Test
	public void testShouldFileTypeBeExtracted() {
		assertTrue(gitConfig.shouldFileTypeBeExtracted(FileType.java()));
		assertFalse(gitConfig.shouldFileTypeBeExtracted(FileType.javascript()));
	}

	@Test
	public void testPostDefaultBranchCommits() {
		gitConfig.setPostDefaultBranchCommitsActivated(true);
		assertTrue(gitConfig.isPostDefaultBranchCommitsActivated());
	}

	@Test
	public void testPostFeatureBranchCommits() {
		gitConfig.setPostFeatureBranchCommitsActivated(true);
		assertTrue(gitConfig.isPostFeatureBranchCommitsActivated());
	}
}
