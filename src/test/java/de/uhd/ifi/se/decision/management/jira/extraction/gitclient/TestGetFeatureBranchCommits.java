package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;

public class TestGetFeatureBranchCommits extends TestSetUpGit {

    private final String repoBaseDirectory;
    private GitClient testGitClient;

    private String featureBranch = "featureBranch";

    public TestGetFeatureBranchCommits() {
	repoBaseDirectory = super.getRepoBaseDirectory();
    }

    @Test
    public void testGetFeatureBranchCommitsByString() {
	// fetches the 'default' branch commits. Do not use TestSetUpGit' gitClient
	List<String> uris = new ArrayList<String>();
	uris.add(GIT_URI);
	testGitClient = new GitClientImpl(uris, repoBaseDirectory, "TEST");

	List<RevCommit> commits = testGitClient.getFeatureBranchCommits(featureBranch, GIT_URI);
	assertTrue(commits != null);
    }

    @Test
    public void testGetFeatureBranchCommitsByRef() {
	// fetches the 'default' branch commits. Do not use TestSetUpGit' gitClient
	List<String> uris = new ArrayList<String>();
	uris.add(GIT_URI);
	testGitClient = new GitClientImpl(uris, repoBaseDirectory, "TEST");

	// get the Ref
	List<Ref> remoteBranches = testGitClient.getAllRemoteBranches();
	List<Ref> branchCandidates = remoteBranches.stream().filter(ref -> ref.getName().endsWith(featureBranch))
		.collect(Collectors.toList());

	assertEquals(1, branchCandidates.size());

	List<RevCommit> commits = testGitClient.getFeatureBranchCommits(branchCandidates.get(0));
	assertTrue(commits != null);
    }
}
