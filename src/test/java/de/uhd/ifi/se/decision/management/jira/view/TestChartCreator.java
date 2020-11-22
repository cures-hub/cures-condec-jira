package de.uhd.ifi.se.decision.management.jira.view;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.view.dashboard.ChartCreator;

public class TestChartCreator {

	private ChartCreator creator;

	@Before
	public void setUp() {
		creator = new ChartCreator("TestProject");
	}

	@Test
	public void testAddChart() {
		Map<String, Integer> metricData = new HashMap<String, Integer>();
		metricData.put("Test", 1);
		creator.addChart("TestChart", "ChartID", metricData);
		assertEquals(metricData, creator.getChartNamesAndData().get("ChartID"));
		assertEquals(creator.getChartNamesAndPurpose().get("ChartID"), "\\" + "TestChart");
	}

	@Test
	public void testAddChartWithIssueContent() {
		Map<String, String> metricData = new HashMap<String, String>();
		metricData.put("Test", "String");
		creator.addChartWithIssueContent("TestChart", "ChartID", metricData);
		assertEquals(metricData, creator.getChartNamesAndData().get("ChartID"));
		assertEquals(creator.getChartNamesAndPurpose().get("ChartID"), "\\" + "TestChart");
	}

	@Test
	public void testGetVelocityParameters() {
		assertEquals(4, creator.getVelocityParameters().size());
	}
}
