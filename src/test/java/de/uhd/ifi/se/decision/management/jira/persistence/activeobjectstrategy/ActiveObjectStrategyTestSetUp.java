package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectstrategy;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.DecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ActiveObjectStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionKnowledgeElementInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.LinkInDatabase;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;

public class ActiveObjectStrategyTestSetUp {

	protected EntityManager entityManager;
	protected ApplicationUser user;
	protected ActiveObjectStrategy aoStrategy;

	public void initialisation() {
		ActiveObjects activeObjects = new TestActiveObjects(entityManager);
		TestComponentGetter.init(activeObjects, new MockTransactionTemplate(), new MockUserManager());
		aoStrategy = new ActiveObjectStrategy("TEST");
		user = new MockApplicationUser("NoFails");
	}

	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater {
		@SuppressWarnings("unchecked")
		@Override
		public void update(EntityManager entityManager) throws Exception {
			entityManager.migrate(DecisionKnowledgeElementInDatabase.class);
			entityManager.migrate(DecisionKnowledgeInCommentEntity.class);
			entityManager.migrate(LinkInDatabase.class);
		}
	}
}
