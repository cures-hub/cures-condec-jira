package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class TestGetRationaleFromCommitMessages extends TestSetUpGit {

	@Test
	public void nullOrEmptyFeatureBranchCommits() {
		int numberExpectedElements = 0;
		List<KnowledgeElement> gotElements = gitClient.getRationaleElements(null);
		assertEquals(numberExpectedElements, gotElements.size());
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

		List<KnowledgeElement> gotElements = gitClient.getRationaleElements(featureBranch);
		assertEquals(numberExpectedElements, gotElements.size());
	}

	@Test
	public void fromFeatureBranchCommitsNullInput() {
		List<KnowledgeElement> gotElements = gitClient.getRationaleElements(null);
		assertNotNull(gotElements);
		assertEquals(0, gotElements.size());

		gotElements = gitClient.getRationaleElements((Ref) null);
		assertNotNull(gotElements);
		assertEquals(0, gotElements.size());
	}
}