package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;

public class ChartCreator {

    private Map<String, String> chartNamesAndPurpose;
    private Map<String, Object> chartNamesAndData;
    private Map<String, Object> velocityParams;

    public ChartCreator(long projectId) {
	this.chartNamesAndPurpose = new HashMap<String, String>();
	this.chartNamesAndData = new HashMap<String, Object>();
	this.velocityParams = new HashMap<String, Object>();
	// Push some basic parameters
	String jiraBaseUrl = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
	velocityParams.put("projectName", ComponentAccessor.getProjectManager().getProjectObj(projectId).getName());
	velocityParams.put("jiraBaseUrl", jiraBaseUrl);
    }

    public void addChart(String chartName, String chartId, Map<String, Integer> metricData) {
	this.chartNamesAndPurpose.put(chartId, "\\" + chartName);
	this.chartNamesAndData.put(chartId, metricData);
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
    // TODO: Number of Elements in Decision Knowledge Graph

}
