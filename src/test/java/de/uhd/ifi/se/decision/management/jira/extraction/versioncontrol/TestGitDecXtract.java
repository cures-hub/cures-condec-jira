package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestGitDecXtract extends TestSetUpGit {
	private final String uri;
	private GitDecXtract gitDecX;

	public TestGitDecXtract() {
		uri = super.getRepoUri();
	}

	@Test
	public void nullOrEmptyFeatureBranchCommits() {
		// git repository is setup already
		gitDecX = new GitDecXtract("TEST", uri);
		int numberExpectedElements = 0;
		List<DecisionKnowledgeElement> gotElements = gitDecX.getElements(null);
		Assert.assertEquals(numberExpectedElements, gotElements.size());

		gotElements = gitDecX.getElements("");
		Assert.assertEquals(numberExpectedElements, gotElements.size());

		gotElements = gitDecX.getElements("doesNotExistBranch");
		Assert.assertEquals(numberExpectedElements, gotElements.size());
	}

	@Test
	public void fromFeatureBranchCommits() {
		// git repository is setup already
		gitDecX = new GitDecXtract("TEST", uri);
		// 1 code rationale exists in main branch, will be changed in feature branch
		// feature branch: 5 in messages + 1 mod in code (x2 because in both files) + 4 inserts in code
		int numberExpectedElements = 5+1*2+4;
 		List<DecisionKnowledgeElement> gotElements = gitDecX.getElements("featureBranch");
		Assert.assertEquals(numberExpectedElements, gotElements.size());
	}
}
