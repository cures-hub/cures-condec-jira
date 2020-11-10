package de.uhd.ifi.se.decision.management.jira.view.diffviewer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;

public class TestDiffViewer extends TestSetUpGit {
	private DiffViewer viewer;

	@Test
	public void testDiffViewerConstructorEntireProject() {
		viewer = new DiffViewer("TEST");
		assertEquals(1, viewer.getBranches().size());
	}

	@Test
	public void testDiffViewerConstructorJiraIssue() {
		viewer = new DiffViewer("TEST", "TEST-4");
		assertEquals(1, viewer.getBranches().size());
	}

}
