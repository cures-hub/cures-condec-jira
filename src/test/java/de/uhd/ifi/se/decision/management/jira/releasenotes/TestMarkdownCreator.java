package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.status.MockStatus;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestMarkdownCreator extends TestSetUp {

	private ReleaseNotes releaseNotes;
	private MarkdownCreator markdownCreator;

	@Before
	public void setUp() {
		init();
		MutableIssue issue = ((MutableIssue) JiraIssues.getJiraIssueByKey("TEST-2"));
		issue.setStatus(new MockStatus("2", "resolved"));

		releaseNotes = new ReleaseNotes();
		releaseNotes.setTitle("Great release");
		releaseNotes.setProjectKey("TEST");
		markdownCreator = new MarkdownCreator(releaseNotes);
	}

	@Test
	public void testEmptyOnlyTitle() {
		assertEquals("# Great release\n", markdownCreator.getMarkdownString());
	}

	@Test
	public void testJiraIssueWithDecisionKnowledgeExisting() {
		ReleaseNotesEntry entry = new ReleaseNotesEntry("TEST-1");
		assertFalse(entry.getElement().getLinks().isEmpty());
		assertTrue(entry.getElement().hasNeighborOfType(KnowledgeType.ISSUE));

		releaseNotes.setNewFeatures(List.of(entry));

		assertTrue(markdownCreator.getMarkdownString().contains("## New Features"));
	}
}
