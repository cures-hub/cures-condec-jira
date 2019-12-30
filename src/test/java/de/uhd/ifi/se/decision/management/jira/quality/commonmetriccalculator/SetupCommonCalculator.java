package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import org.junit.Before;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.quality.CommonMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public abstract class SetupCommonCalculator extends TestSetUpGit {
	protected CommonMetricCalculator calculator;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		calculator = new CommonMetricCalculator(1, user, "16");
	}

}
