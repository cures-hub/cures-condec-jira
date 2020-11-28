package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.LinkedHashMap;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.quality.MetricCalculator;

public class GeneralMetricsDashboardItem extends ConDecDashboardItem {

	@Override
	public Map<String, Object> getMetrics() {
		Map<String, Object> metrics = new LinkedHashMap<>();

		ChartCreator chartCreator = new ChartCreator();

		MetricCalculator metricCalculator = new MetricCalculator(user, filterSettings);
		chartCreator.addChart("#Comments per Jira Issue", "boxplot-CommentsPerJiraIssue",
				metricCalculator.numberOfCommentsPerIssue());
		chartCreator.addChart("Distribution of Knowledge Types", "piechartInteger-KnowledgeTypeDistribution",
				metricCalculator.getDistributionOfKnowledgeTypes());
		chartCreator.addChart("#Requirements and Code Classes", "piechartInteger-ReqCodeSummary",
				metricCalculator.getReqAndClassSummary());
		chartCreator.addChart("#Elements from Documentation Locations", "piechartInteger-DecSources",
				metricCalculator.getKnowledgeSourceCount());
		chartCreator.addChart("Comments in Jira Issues relevant to Decision Knowledge",
				"piechartInteger-RelevantSentences", metricCalculator.getNumberOfRelevantComments());

		metrics.putAll(chartCreator.getVelocityParameters());

		/*
		 * chartCreator.addChart("#Commits per Jira Issue",
		 * "boxplot-CommitsPerJiraIssue", metricCalculator.numberOfCommitsPerIssue());
		 */

		return metrics;
	}
}