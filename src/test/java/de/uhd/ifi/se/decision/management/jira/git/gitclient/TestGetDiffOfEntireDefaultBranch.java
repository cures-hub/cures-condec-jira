package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;

public class TestGetDiffOfEntireDefaultBranch extends TestSetUpGit {

	@Test
	public void testAllDefaultBranchCommits() {
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();

		List<RevCommit> allCommits = diff.getCommits();
		assertEquals(6, allCommits.size());

		assertEquals(5, diff.getChangedFiles().size());
		ChangedFile extractedClass = diff.getChangedFiles().get(2);
		assertEquals("Tangled2.java", extractedClass.getName());
		assertEquals(1, extractedClass.getCommits().size());
		assertEquals("TEST-30", extractedClass.getJiraIssueKeys().iterator().next());
	}
}