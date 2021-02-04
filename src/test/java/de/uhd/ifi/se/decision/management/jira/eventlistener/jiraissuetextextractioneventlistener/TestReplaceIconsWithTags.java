package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.JiraIssueTextExtractionEventListener;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestReplaceIconsWithTags {

	@Test
	public void testIssueIcon() {
		String textWithIcon = "(!) This is a very severe issue.";
		assertEquals("{issue} This is a very severe issue.{issue}",
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
		assertEquals("{issue} This is a very severe issue.\r\n {issue}{decision} We can solve it!{decision}",
				JiraIssueTextExtractionEventListener.replaceIconsWithTags(textWithIcon));
	}

	@Test
	public void testIrrelevantTextBefore() {
		String textWithIcon = "{code}public class GodClass{code}(!) This is a very severe issue.\r\n(/) We can solve it!";
		assertEquals(
				"{code}public class GodClass{code}{issue} This is a very severe issue.\r\n{issue}{decision} We can solve it!{decision}",
				JiraIssueTextExtractionEventListener.replaceIconsWithTags(textWithIcon));
	}

	@Test
	public void testRelevantMacroTextBefore() {
		String textWithIcon = "{issue} This is a very severe issue.{issue}\r\n(/) We can solve it!";
		assertEquals("{issue} This is a very severe issue.{issue}\r\n{decision} We can solve it!{decision}",
				JiraIssueTextExtractionEventListener.replaceIconsWithTags(textWithIcon));
	}

	@Test
	public void testRelevantMacroTextAfter() {
		String textWithIcon = "(!) This is a very severe issue.{decision} We can solve it!{decision}";
		assertEquals("{issue} This is a very severe issue.{issue}{decision} We can solve it!{decision}",
				JiraIssueTextExtractionEventListener.replaceIconsWithTags(textWithIcon));
	}

	@Test
	public void testLineBreak() {
		String textWithIcon = "(!) This is a very severe issue.\n";
		assertEquals("{issue} This is a very severe issue.{issue}\n",
				JiraIssueTextExtractionEventListener.replaceIconsWithTags(textWithIcon));
	}

	@Test
	public void testWithoutIconsStaysTheSame() {
		String textWithoutIcon = "{issue} This is a very severe issue.{issue}{decision} We can solve it!{decision}";
		assertEquals("{issue} This is a very severe issue.{issue}{decision} We can solve it!{decision}",
				JiraIssueTextExtractionEventListener.replaceIconsWithTags(textWithoutIcon));
	}

	@Test
	public void testProAndCon() {
		String textWithoutIcon = "(+) Good idea.\n(-) No, this is bad.";
		assertEquals("{pro} Good idea.{pro}\n{con} No, this is bad.{con}",
				JiraIssueTextExtractionEventListener.replaceIconsWithTags(textWithoutIcon));
	}
}
