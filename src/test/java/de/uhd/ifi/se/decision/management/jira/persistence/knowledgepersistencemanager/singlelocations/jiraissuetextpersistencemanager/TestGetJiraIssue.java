package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetJiraIssue extends TestSetUp {

	protected static JiraIssueTextPersistenceManager manager;

	@Before
	public void setUp() {
		init();
		manager = new JiraIssueTextPersistenceManager("TEST");
	}

	@Test
	@NonTransactional
	public void testGetJiraIssueValid() {
		JiraIssues.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		String jiraIssueKey = manager.getJiraIssue(1).getKey();
		assertEquals("TEST-30", jiraIssueKey);
	}

	@Test
	@NonTransactional
	public void testGetJiraIssueUnknownId() {
		assertNull(manager.getJiraIssue(1));
	}
}
