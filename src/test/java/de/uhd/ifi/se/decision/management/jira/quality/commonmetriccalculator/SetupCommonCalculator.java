package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import org.junit.Before;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.quality.CommonMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public abstract class SetupCommonCalculator extends TestSetUp {
	protected CommonMetricCalculator calculator;

	@Before
	public void setUp() {
		init();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		calculator = new CommonMetricCalculator(1, user, "16");
	}

}
