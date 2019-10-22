package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.activeobjectpersistencemanager;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.ActiveObjectPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public abstract class ActiveObjectPersistenceManagerTestSetUp extends TestSetUp {

	protected static ApplicationUser user;
	protected static ActiveObjectPersistenceManager aoStrategy;

	public static void initialisation() {
		init();
		aoStrategy = KnowledgePersistenceManager.getOrCreate("TEST").getActiveObjectManager();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}
}
