package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.LinkedHashMap;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.quality.MetricCalculator;

public class GeneralMetricsDashboardItem extends ConDecDashboardItem {

	@Override
	public Map<String, Object> getMetrics() {
		Map<String, Object> metrics = new LinkedHashMap<>();
		MetricCalculator metricCalculator = new MetricCalculator(user, filterSettings);

		metrics.put("numberOfCommentsPerJiraIssue", metricCalculator.numberOfCommentsPerIssue());
		metrics.put("distributionOfKnowledgeTypes", metricCalculator.getDistributionOfKnowledgeTypes());
		metrics.put("requirementsAndCodeFiles", metricCalculator.getReqAndClassSummary());
		metrics.put("numberOfElementsPerDocumentationLocation", metricCalculator.getKnowledgeSourceCount());
		metrics.put("numberOfRelevantComments", metricCalculator.getNumberOfRelevantComments());
		/*
		 * chartCreator.addChart("#Commits per Jira Issue",
		 * "boxplot-CommitsPerJiraIssue", metricCalculator.numberOfCommitsPerIssue());
		 */

		return metrics;
	}
}