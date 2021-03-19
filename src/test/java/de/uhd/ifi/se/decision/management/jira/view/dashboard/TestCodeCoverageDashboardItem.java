package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCodeCoverageDashboardItem extends TestSetUp {

	private ConDecDashboardItem dashboardItem;

	@Before
	public void setUp() {
		init();
		dashboardItem = new CodeCoverageDashboardItem();
		dashboardItem.user = JiraUsers.SYS_ADMIN.getApplicationUser();
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
