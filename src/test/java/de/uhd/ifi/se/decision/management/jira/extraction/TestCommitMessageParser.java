package de.uhd.ifi.se.decision.management.jira.extraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.parser.CommitMessageParser;

public class TestCommitMessageParser {

	@Test
	public void testConstructor() {
		assertNotNull(new CommitMessageParser());
	}

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
	public void testGetJiraIssueKeys() {
		String message = "ConDec-1: Improve almost everything... ConDec-2 even this!";
		Set<String> keys = CommitMessageParser.getJiraIssueKeys(message, "condec");
		assertEquals(2, keys.size());
		Iterator<String> iterator = keys.iterator();
		assertEquals("CONDEC-1", iterator.next());
		assertEquals("CONDEC-2", iterator.next());
	}

	@Test
	public void testGetJiraIssueKeysProjectKeyNull() {
		String message = "ConDec-1: Improve almost everything... ConDec-2 even this!";
		Set<String> keys = CommitMessageParser.getJiraIssueKeys(message, null);
		assertEquals(0, keys.size());
	}
}
