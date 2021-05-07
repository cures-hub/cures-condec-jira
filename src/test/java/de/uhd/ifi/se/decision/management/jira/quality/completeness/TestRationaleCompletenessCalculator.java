package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertNotNull;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRationaleCompletenessCalculator extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testRationaleCompletenessCalculator() {
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		String projectKey = "TEST";
		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		RationaleCompletenessCalculator calculator = new RationaleCompletenessCalculator(user, filterSettings);
		assertNotNull(calculator);
	}

}
