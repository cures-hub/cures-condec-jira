package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestMigrateArgumentTypesInLinks extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testProjectKeyInvalid() {
		assertFalse(JiraIssueTextPersistenceManager.migrateArgumentTypesInLinks(null));
		assertFalse(JiraIssueTextPersistenceManager.migrateArgumentTypesInLinks(""));
	}

	@Test
	@NonTransactional
	public void testProjectKeyFilled() {
		assertTrue(JiraIssueTextPersistenceManager.migrateArgumentTypesInLinks("TEST"));
	}
}
