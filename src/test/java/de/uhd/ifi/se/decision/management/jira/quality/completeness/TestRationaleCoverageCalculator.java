package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertNotNull;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRationaleCoverageCalculator extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testRationaleCoverageCalculator() {
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		String sourceKnowledgeTypes = "TEST-1";
		RationaleCoverageCalculator calculator = new RationaleCoverageCalculator(user, filterSettings, sourceKnowledgeTypes);
		assertNotNull(calculator);
	}

}
