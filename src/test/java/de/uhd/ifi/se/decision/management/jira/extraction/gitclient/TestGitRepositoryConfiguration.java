package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryConfiguration;

public class TestGitRepositoryConfiguration {

	private GitRepositoryConfiguration gitRepositoryConfiguration;

	@Before
	public void setUp() {
		gitRepositoryConfiguration = new GitRepositoryConfiguration(TestSetUpGit.GIT_URI, "master", "HTTP",
				"heinz.guenther", "P@ssw0rd!");
	}

	@Test
	public void testDefaultBranchNull() {
		gitRepositoryConfiguration = new GitRepositoryConfiguration(TestSetUpGit.GIT_URI, null, "HTTP",
				"heinz.guenther", "P@ssw0rd!");
		assertEquals("master", gitRepositoryConfiguration.getDefaultBranch());
	}

	@Test
	public void testDefaultBranchEmpty() {
		gitRepositoryConfiguration = new GitRepositoryConfiguration(TestSetUpGit.GIT_URI, "", "HTTP", "heinz.guenther",
				"P@ssw0rd!");
		assertEquals("master", gitRepositoryConfiguration.getDefaultBranch());
	}

	@Test
	public void testIsValidUri() {
		assertTrue(gitRepositoryConfiguration.isValid());
		gitRepositoryConfiguration = new GitRepositoryConfiguration(null, "master", "HTTP", "heinz.guenther",
				"P@ssw0rd!");
		assertFalse(gitRepositoryConfiguration.isValid());
		gitRepositoryConfiguration = new GitRepositoryConfiguration(" ", "master", "HTTP", "heinz.guenther",
				"P@ssw0rd!");
		assertFalse(gitRepositoryConfiguration.isValid());
	}
}