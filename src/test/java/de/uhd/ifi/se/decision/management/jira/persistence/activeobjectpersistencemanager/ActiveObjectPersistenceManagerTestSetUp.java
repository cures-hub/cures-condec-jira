package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectpersistencemanager;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.persistence.ActiveObjectPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUser;

public abstract class ActiveObjectPersistenceManagerTestSetUp extends TestSetUpWithIssues {

	protected static ApplicationUser user;
	protected static ActiveObjectPersistenceManager aoStrategy;

	public static void initialisation() {
		initialization();
		aoStrategy = new ActiveObjectPersistenceManager("TEST");
		user = JiraUser.SYS_ADMIN.getApplicationUser();
	}
}
