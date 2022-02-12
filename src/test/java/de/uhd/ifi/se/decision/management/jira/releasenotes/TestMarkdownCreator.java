package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestMarkdownCreator extends TestSetUp {

	private ReleaseNotes releaseNotes;
	private MarkdownCreator markdownCreator;

	@Before
	public void setUp() {
		init();
		releaseNotes = new ReleaseNotes();
		releaseNotes.setTitle("Great release");
		releaseNotes.setProjectKey("TEST");
		markdownCreator = new MarkdownCreator();
	}

	@Test
	public void testEmptyOnlyTitle() {
		assertEquals("# Great release\n", markdownCreator.getMarkdownString(releaseNotes));
	}

	@Test
	public void testJiraIssueWithDecisionKnowledgeExisting() {
		ReleaseNotesEntry entry = new ReleaseNotesEntry("TEST-1");
		assertFalse(entry.getElement().getLinks().isEmpty());
		assertTrue(entry.getElement().hasNeighborOfType(KnowledgeType.ISSUE));

		releaseNotes.setNewFeatures(List.of(entry));
		assertTrue(markdownCreator.getMarkdownString(releaseNotes).contains("## New Features"));
	}
}
