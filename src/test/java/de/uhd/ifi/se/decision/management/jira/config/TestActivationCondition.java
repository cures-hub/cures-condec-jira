package de.uhd.ifi.se.decision.management.jira.config;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockJiraHelper;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestActivationCondition {
	private static ActivationCondition condition;

	@BeforeClass
	public static void setUp() {
		TestSetUp.init();
		BasicConfiguration basicConfiguration = ConfigPersistenceManager.getBasicConfiguration("TEST");
		basicConfiguration.setActivated(true);
		ConfigPersistenceManager.saveBasicConfiguration("TEST", basicConfiguration);
		condition = new ActivationCondition();
	}

	@Test
	public void testUserFilledJiraHelperFilled() {
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		MockJiraHelper helper = new MockJiraHelper();
		assertTrue(condition.shouldDisplay(user, helper));
	}
}
