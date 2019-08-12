package de.uhd.ifi.se.decision.management.jira.mocks;

import java.sql.SQLException;

import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionKnowledgeElementInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.KnowledgeStatusInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;

public class MockDatabase implements DatabaseUpdater {

	@SuppressWarnings("unchecked")
	@Override
	public void update(EntityManager entityManager) {
		try {
			entityManager.migrate(DecisionKnowledgeElementInDatabase.class);
			entityManager.migrate(PartOfJiraIssueTextInDatabase.class);
			entityManager.migrate(LinkInDatabase.class);
			entityManager.migrate(KnowledgeStatusInDatabase.class);
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
	}
}
