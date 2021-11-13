package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetRationaleFromCommitMessages extends TestSetUpGit {

	@Test
	@NonTransactional
	public void nullOrEmptyFeatureBranchCommits() {
		int numberExpectedElements = 0;
		List<KnowledgeElement> gotElements = gitClient.getRationaleElements(null);
		assertEquals(numberExpectedElements, gotElements.size());
	}

	@Test
	@NonTransactional
	public void fromFeatureBranchCommits() {
		int numberExpectedElements = 11;

		Ref featureBranch = gitClient.getRefs("TEST-4.feature.branch").get(0);
		List<KnowledgeElement> gotElements = gitClient.getRationaleElements(featureBranch);
		assertEquals(numberExpectedElements, gotElements.size());
	}

	@Test
	@NonTransactional
	public void fromFeatureBranchCommitsNullInput() {
		List<KnowledgeElement> gotElements = gitClient.getRationaleElements(null);
		assertEquals(0, gotElements.size());

		gotElements = gitClient.getRationaleElements((Ref) null);
		assertNotNull(gotElements);
		assertEquals(0, gotElements.size());
	}
}