package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.LinkedHashMap;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.quality.generalmetrics.GeneralMetricCalculator;

public class GeneralMetricsDashboardItem extends ConDecDashboardItem {

	@Override
	public Map<String, Object> getMetrics() {
		Map<String, Object> metrics = new LinkedHashMap<>();
		GeneralMetricCalculator metricCalculator = new GeneralMetricCalculator(user, filterSettings);

		metrics.put("numberOfCommentsPerJiraIssue", metricCalculator.numberOfCommentsPerIssue());
		metrics.put("numberOfCommitsPerJiraIssue", metricCalculator.getNumberOfCommits());

		metrics.put("distributionOfKnowledgeTypes", metricCalculator.getDistributionOfKnowledgeTypes());
		metrics.put("requirementsAndCodeFiles", metricCalculator.getReqAndClassSummary());
		metrics.put("numberOfElementsPerDocumentationLocation", metricCalculator.getKnowledgeSourceCount());
		metrics.put("numberOfRelevantComments", metricCalculator.getNumberOfRelevantComments());

		return metrics;
	}
}