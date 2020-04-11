package de.uhd.ifi.se.decision.management.jira.mocks;

import java.sql.SQLException;

import de.uhd.ifi.se.decision.management.jira.persistence.tables.CodeClassElementInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionGroupInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.ReleaseNotesInDatabase;
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
			entityManager.migrate(CodeClassElementInDatabase.class);
			entityManager.migrate(ReleaseNotesInDatabase.class);
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
	}
}
