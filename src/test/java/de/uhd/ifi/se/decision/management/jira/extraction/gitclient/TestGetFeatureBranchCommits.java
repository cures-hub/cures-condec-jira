package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestGetFeatureBranchCommits extends TestSetUpGit {

	final String repoBaseDirectory;
	final String uri;
	GitClientImpl testGitClient;

	String featureBranch = "featureBranch";
	String expectedFirstCommitMessage = "First message";

	public TestGetFeatureBranchCommits() {
		repoBaseDirectory = getRepoBaseDirectory();
		uri = getRepoUri();
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGetFeatureBranchCommitsByString() {
		// fetches the 'default' branch commits. Do not use TestSetUpGit' gitClient
		testGitClient = new GitClientImpl(uri, repoBaseDirectory, "TEST");

		List<RevCommit> commits = testGitClient.getFeatureBranchCommits(featureBranch);
		assertEquals(3, commits.size());
		assertEquals(expectedFirstCommitMessage, commits.get(0).getFullMessage());
	}

	@Test
	public void testGetFeatureBranchCommitsByRef() {
		// fetches the 'default' branch commits. Do not use TestSetUpGit' gitClient
		testGitClient = new GitClientImpl(uri, repoBaseDirectory, "TEST");

		// get the Ref
		List<Ref> remoteBranches = testGitClient.getRemoteBranches();
		List<Ref> branchCandidates = remoteBranches.stream()
				.filter(ref -> ref.getName().endsWith(featureBranch))
				.collect(Collectors.toList());

		assertEquals(1, branchCandidates.size());

		List<RevCommit> commits = testGitClient.getFeatureBranchCommits(branchCandidates.get(0));
		assertEquals(3, commits.size());
		assertEquals(expectedFirstCommitMessage, commits.get(0).getFullMessage());
	}

	// helpers
	private String getRepoUri() {
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

	private String getRepoBaseDirectory() {
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
