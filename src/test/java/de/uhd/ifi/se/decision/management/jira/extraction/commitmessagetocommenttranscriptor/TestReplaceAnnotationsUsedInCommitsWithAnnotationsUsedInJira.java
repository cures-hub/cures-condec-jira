package de.uhd.ifi.se.decision.management.jira.extraction.commitmessagetocommenttranscriptor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CommitMessageToCommentTranscriber;

public class TestReplaceAnnotationsUsedInCommitsWithAnnotationsUsedInJira {

	@Test
	public void testLowercaseIssueMessage() {
		String commitMessage = "[issue]This is an issue![/issue]";
		assertEquals("{issue}This is an issue!{issue}", CommitMessageToCommentTranscriber
				.replaceAnnotationsUsedInCommitsWithAnnotationsUsedInJira(commitMessage));
	}

	@Test
	public void testUppercaseIssueMessage() {
		String commitMessage = "[Issue]This is an issue![/Issue]";
		assertEquals("{issue}This is an issue!{issue}", CommitMessageToCommentTranscriber
				.replaceAnnotationsUsedInCommitsWithAnnotationsUsedInJira(commitMessage));
	}

	@Test
	public void testMixedcaseIssueMessage() {
		String commitMessage = "[issue]This is an issue![/Issue]";
		assertEquals("{issue}This is an issue!{issue}", CommitMessageToCommentTranscriber
				.replaceAnnotationsUsedInCommitsWithAnnotationsUsedInJira(commitMessage));
	}
}
