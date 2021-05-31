package de.uhd.ifi.se.decision.management.jira.extraction.gitclient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.junit.Assert;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class TestGitDecXtract extends TestSetUpGit {

	@Test
	public void nullOrEmptyFeatureBranchCommits() {
		int numberExpectedElements = 0;
		List<KnowledgeElement> gotElements = gitClient.getElements(null);
		Assert.assertEquals(numberExpectedElements, gotElements.size());
	}

	@Test
	public void fromFeatureBranchCommits() {
		// git repository is setup already
		List<String> uris = new ArrayList<String>();
		uris.add(GIT_URI);
		int numberExpectedElements = 14;

		// by Ref, find Ref first
		List<Ref> featureBranches = gitClient.getBranches();
		Ref featureBranch = null;
		Iterator<Ref> it = featureBranches.iterator();
		while (it.hasNext()) {
			Ref value = it.next();
			if (value.getName().endsWith("TEST-4.feature.branch")) {
				featureBranch = value;
				return;
			}
		}

		List<KnowledgeElement> gotElements = gitClient.getElements(featureBranch);
		Assert.assertEquals(numberExpectedElements, gotElements.size());
	}

	@Test
	public void fromFeatureBranchCommitsNullInput() {
		List<KnowledgeElement> gotElements = gitClient.getElements(null);
		Assert.assertNotNull(gotElements);
		Assert.assertEquals(0, gotElements.size());

		gotElements = gitClient.getElements((Ref) null);
		Assert.assertNotNull(gotElements);
		Assert.assertEquals(0, gotElements.size());

	}

}
