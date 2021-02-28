package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRationaleCompletenessDashboardItem extends TestSetUp {

	private ConDecDashboardItem dashboardItem;

	@Before
	public void setUp() {
		init();
		dashboardItem = new RationaleCompletenessDashboardItem();
		dashboardItem.user = JiraUsers.SYS_ADMIN.getApplicationUser();
		dashboardItem.filterSettings = new FilterSettings("TEST", "");
	}

	@Test
	@NonTransactional
	public void testGetMetrics() {
		Map<String, Object> metricsMap = dashboardItem.getMetrics();
		assertEquals(0, metricsMap.size());
	}

	@Test
	@NonTransactional
	public void testGetAdditionalParameters() {
		Map<String, Object> additionalParameters = dashboardItem.getAdditionalParameters();
		assertEquals(2, additionalParameters.size());
	}

}
