package de.uhd.ifi.se.decision.management.jira.quality.generalmetrics;

import static org.junit.Assert.assertNotNull;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestMetricCalculator extends TestSetUpGit {

	@Override
	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testGeneralMetricsCalculator() {
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		String projectKey = "TEST";
		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		GeneralMetricCalculator calculator = new GeneralMetricCalculator(user, filterSettings);
		assertNotNull(calculator);
	}

}
