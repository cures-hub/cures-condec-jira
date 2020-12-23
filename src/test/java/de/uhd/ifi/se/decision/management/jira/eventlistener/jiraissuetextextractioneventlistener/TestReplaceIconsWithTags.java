package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.JiraIssueTextExtractionEventListener;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestReplaceIconsWithTags {

	@Test
	public void testIssueIcon() {
		String textWithIcon = "(!) This is a very severe issue.";
		assertEquals("{issue}This is a very severe issue.{issue}",
				JiraIssueTextExtractionEventListener.replaceIconsWithTags(textWithIcon));
	}

	@Test
	public void testIconNotExisting() {
		String textWithIcon = "(abc)This is a very severe issue.";
		assertEquals("(abc)This is a very severe issue.",
				JiraIssueTextExtractionEventListener.replaceIconsWithTags(textWithIcon));
	}

	@Test
	public void testKnowledgeTypeHasNoIconRepresentation() {
		String textWithIcon = "(!)This is a very severe issue.";
		assertEquals("(!)This is a very severe issue.",
				JiraIssueTextExtractionEventListener.replaceIconsWithTags(textWithIcon, KnowledgeType.GOAL));
	}

	@Test
	public void testTwoIcons() {
		String textWithIcon = "(!) This is a very severe issue.\r\n (/) We can solve it!";
		assertEquals("{issue}This is a very severe issue.{issue}{decision}We can solve it!{decision}",
				JiraIssueTextExtractionEventListener.replaceIconsWithTags(textWithIcon));
	}

	@Test
	public void testIrrelevantTextBefore() {
		String textWithIcon = "{code}public class GodClass{code}(!) This is a very severe issue.\r\n (/) We can solve it!";
		// currently all preceding text in front of the icon will be classified as well
		assertEquals(
				"{issue}{code}public class GodClass{code} This is a very severe issue.{issue}{decision}We can solve it!{decision}",
				JiraIssueTextExtractionEventListener.replaceIconsWithTags(textWithIcon));
	}
}
