package de.uhd.ifi.se.decision.management.jira.mocks;

import java.sql.SQLException;

import de.uhd.ifi.se.decision.management.jira.persistence.tables.CodeClassInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.ConsistencyCheckLogsInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionGroupInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DiscardedSuggestionInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.ReleaseNotesInDatabase;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;

/**
 * Initializes the active objects database for unit testing. All database tables
 * need to be listed here.
 */
public class MockDatabase implements DatabaseUpdater {

	@SuppressWarnings("unchecked")
	@Override
	public void update(EntityManager entityManager) {
		try {
			entityManager.migrate(PartOfJiraIssueTextInDatabase.class);
			entityManager.migrate(LinkInDatabase.class);
			entityManager.migrate(DecisionGroupInDatabase.class);
			entityManager.migrate(CodeClassInDatabase.class);
			entityManager.migrate(ReleaseNotesInDatabase.class);
			entityManager.migrate(DiscardedSuggestionInDatabase.class);
			entityManager.migrate(ConsistencyCheckLogsInDatabase.class);

		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
	}
}
