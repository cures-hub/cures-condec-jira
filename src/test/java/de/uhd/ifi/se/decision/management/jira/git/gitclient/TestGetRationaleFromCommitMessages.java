package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.model.DiffForSingleRef;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetRationaleFromCommitMessages extends TestSetUpGit {

	@Test
	@NonTransactional
	public void nullOrEmptyFeatureBranchCommits() {
		assertEquals(0, new DiffForSingleRef().getRationaleElementsFromCommitMessages().size());
	}

	@Test
	@NonTransactional
	public void fromFeatureBranchCommits() {
		Ref featureBranch = gitClient.getRefs("TEST-4.feature.branch").get(0);
		List<RevCommit> commits = gitClient.getCommits(featureBranch);
		DiffForSingleRef diff = new DiffForSingleRef();
		diff.setCommits(commits);
		assertEquals(6, diff.getRationaleElementsFromCommitMessages().size());
	}
}