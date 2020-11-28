package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import net.java.ao.test.jdbc.NonTransactional;

public class TestFeatureBranchQualityDashboardItem extends TestSetUp {

	private ConDecDashboardItem dashboardItem;

	@Before
	public void setUp() {
		init();
		dashboardItem = new FeatureBranchQualityDashboardItem();
	}

	@Test
	@NonTransactional
	public void testGetContextMap() {
		Map<String, Object> contextMap = dashboardItem.getContextMap(new HashMap<String, Object>());
		// 5 because of additional projectsWithGit lists
		assertEquals(5, contextMap.size());
	}

	@Test
	@NonTransactional
	public void testGetMetrics() {
		Map<String, Object> metricsMap = dashboardItem.getMetrics();
		// metrics are accessed via REST API
		assertEquals(0, metricsMap.size());
	}
}