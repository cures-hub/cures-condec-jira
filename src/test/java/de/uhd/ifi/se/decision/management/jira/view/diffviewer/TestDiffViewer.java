package de.uhd.ifi.se.decision.management.jira.view.diffviewer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;

public class TestDiffViewer extends TestSetUpGit {
	private DiffViewer viewer;

	@Test
	public void testDiffViewerConstructorJiraIssue() {
		viewer = new DiffViewer("TEST", "TEST-4");
		assertNotNull(viewer);
	}

	@Test
	public void testDiffViewerConstructorBranches() {
		List<Ref> branches = gitClient.getBranches();
		assertEquals(2, branches.size());

		viewer = new DiffViewer("TEST", branches);
		assertEquals(2, viewer.getBranches().size());
	}

}
