package de.uhd.ifi.se.decision.management.jira.extraction;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.parser.CommitMessageParser;

public class TestCommitMessageParser {

	@Test
	public void testGetJiraIssueKeyFromEmptyMessage() {
		String jiraIssueKey = CommitMessageParser.getJiraIssueKey("");
		assertEquals("", jiraIssueKey);
	}

	@Test
	public void testGetJiraIssueKeyFromValidMessage() {
		String jiraIssueKey = CommitMessageParser.getJiraIssueKey("Test-12: This is a very advanced commit.");
		assertEquals("TEST-12", jiraIssueKey);
	}

	@Test
	public void testMultipleJiraIssueKeys() {
		Set<String> jiraIssueKeys = CommitMessageParser
				.getJiraIssueKeys("ConDec-1: Improve almost everything... CONDEC-2 -test ConDec-3 - hello- Great tool");
		Iterator<String> iterator = jiraIssueKeys.iterator();
		assertEquals("CONDEC-1", iterator.next());
		assertEquals("CONDEC-2", iterator.next());
		assertEquals("CONDEC-3", iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testParseJiraIssueKeyStartingWithDifferentWord() {
		Set<String> jiraIssueKeys = CommitMessageParser.getJiraIssueKeys("Feature/CONDEC-1 link to detail view");
		Iterator<String> iterator = jiraIssueKeys.iterator();
		assertEquals("CONDEC-1", iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testParseJiraIssueKeysSeparatedWithHypen() {
		Set<String> jiraIssueKeys = CommitMessageParser.getJiraIssueKeys("CONDEC-1-link-to-detail-view");
		Iterator<String> iterator = jiraIssueKeys.iterator();
		assertEquals("CONDEC-1", iterator.next());
		assertFalse(iterator.hasNext());
	}

}
