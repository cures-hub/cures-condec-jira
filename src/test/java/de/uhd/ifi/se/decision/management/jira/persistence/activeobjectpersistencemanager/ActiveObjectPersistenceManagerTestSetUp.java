package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectpersistencemanager;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.persistence.ActiveObjectPersistenceManager;

public abstract class ActiveObjectPersistenceManagerTestSetUp extends TestSetUpWithIssues {

	protected ApplicationUser user;
	protected ActiveObjectPersistenceManager aoStrategy;

	public void initialisation() {
		initialization();
		aoStrategy = new ActiveObjectPersistenceManager("TEST");
		user = new MockApplicationUser("NoFails");
	}
}
