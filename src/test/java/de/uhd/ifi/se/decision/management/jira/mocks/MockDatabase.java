package de.uhd.ifi.se.decision.management.jira.mocks;

import java.sql.SQLException;

import de.uhd.ifi.se.decision.management.jira.persistence.tables.*;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;

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
			entityManager.migrate(DiscardedLinkSuggestionsInDatabase.class);
			entityManager.migrate(DiscardedDuplicatesInDatabase.class);

		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
	}
}
