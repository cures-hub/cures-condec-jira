package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
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
		Diff diff = gitClient.getDiffForFeatureBranchWithName("TEST-4.feature.branch");
		assertEquals(6, diff.get(0).getRationaleElementsFromCommitMessages().size());
	}
}