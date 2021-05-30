package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

public class TestJiraIssueKeyFromCommitMessageParser {

	@Test
	public void testGetJiraIssueKeyFromEmptyMessage() {
		String jiraIssueKey = JiraIssueKeyFromCommitMessageParser.getFirstJiraIssueKey("");
		assertEquals("", jiraIssueKey);
	}

	@Test
	public void testGetJiraIssueKeyFromValidMessage() {
		String jiraIssueKey = JiraIssueKeyFromCommitMessageParser.getFirstJiraIssueKey("Test-12: This is a very advanced commit.");
		assertEquals("TEST-12", jiraIssueKey);
	}

	@Test
	public void testMultipleJiraIssueKeys() {
		Set<String> jiraIssueKeys = JiraIssueKeyFromCommitMessageParser
				.getJiraIssueKeys("ConDec-1: Improve almost everything... CONDEC-2 -test ConDec-3 - hello- Great tool");
		Iterator<String> iterator = jiraIssueKeys.iterator();
		assertEquals("CONDEC-1", iterator.next());
		assertEquals("CONDEC-2", iterator.next());
		assertEquals("CONDEC-3", iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testParseJiraIssueKeyStartingWithDifferentWord() {
		Set<String> jiraIssueKeys = JiraIssueKeyFromCommitMessageParser.getJiraIssueKeys("Feature/CONDEC-1 link to detail view");
		Iterator<String> iterator = jiraIssueKeys.iterator();
		assertEquals("CONDEC-1", iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testParseJiraIssueKeysSeparatedWithHypen() {
		Set<String> jiraIssueKeys = JiraIssueKeyFromCommitMessageParser.getJiraIssueKeys("CONDEC-1-link-to-detail-view");
		Iterator<String> iterator = jiraIssueKeys.iterator();
		assertEquals("CONDEC-1", iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testParseJiraIssueKeyWithNumbers() {
		String jiraIssueKey = JiraIssueKeyFromCommitMessageParser.getFirstJiraIssueKey("ISE2020-1: Add Solr");
		assertEquals("ISE2020-1", jiraIssueKey);
	}

}
