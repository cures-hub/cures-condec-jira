package de.uhd.ifi.se.decision.management.jira.config;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockJiraHelper;

public class TestActivationCondition extends TestSetUpWithIssues {
	private ActivationCondition condition;

	@Before
	public void setUp() {
		TestSetUpWithIssues.initialization();
		condition = new ActivationCondition();
	}

	@Test
	public void testUserFilledJiraHelperFilled() {
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("SysAdmin");
		MockJiraHelper helper = new MockJiraHelper();
		assertTrue(condition.shouldDisplay(user, helper));
	}
}
