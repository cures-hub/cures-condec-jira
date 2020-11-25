package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChartCreator {

	private Map<String, String> chartNamesAndPurpose;
	private Map<String, Object> chartNamesAndData;
	private Map<String, Object> velocityParams;

	public ChartCreator() {
		this.chartNamesAndPurpose = new LinkedHashMap<String, String>();
		this.chartNamesAndData = new LinkedHashMap<String, Object>();
		this.velocityParams = new LinkedHashMap<String, Object>();
	}

	public void addChart(String chartName, String chartId, Map<String, Integer> metricData) {
		this.chartNamesAndPurpose.put(chartId, "\\" + chartName);
		this.chartNamesAndData.put(chartId, metricData);
		System.out.println(chartId);
		System.out.println(metricData);
	}

	public void addChartWithIssueContent(String chartName, String chartId, Map<String, String> metricData) {
		this.chartNamesAndPurpose.put(chartId, "\\" + chartName);
		this.chartNamesAndData.put(chartId, metricData);
	}

	public Map<String, Object> getVelocityParameters() {
		velocityParams.put("chartNamesAndPurpose", chartNamesAndPurpose);
		velocityParams.put("chartNamesAndData", chartNamesAndData);
		return velocityParams;
	}

	public Map<String, String> getChartNamesAndPurpose() {
		return chartNamesAndPurpose;
	}

	public Map<String, Object> getChartNamesAndData() {
		return chartNamesAndData;
	}

}
