package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.LinkedHashMap;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.RationaleCoverageCalculator;

public class RationaleCoverageDashboardItem extends ConDecDashboardItem {

	@Override
	public Map<String, Object> getMetrics() {
		Map<String, Object> metrics = new LinkedHashMap<>();
		if (jiraIssueType == null) {
			return metrics;
		}
		ChartCreator chartCreator = new ChartCreator();

		RationaleCoverageCalculator rationaleCoverageCalculator = new RationaleCoverageCalculator(user, filterSettings);
		chartCreator.addChart("#Decisions per Jira Issue", "boxplot-DecisionsPerJiraIssue",
				rationaleCoverageCalculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.DECISION));
		chartCreator.addChart("#Issues per Jira Issue", "boxplot-IssuesPerJiraIssue",
				rationaleCoverageCalculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.ISSUE));
		chartCreator.addChartWithIssueContent(
				"For how many " + jiraIssueType.getName() + " types is an issue documented?",
				"piechartRich-DecisionDocumentedForSelectedJiraIssue",
				rationaleCoverageCalculator.getJiraIssuesWithNeighborsOfOtherType(jiraIssueType, KnowledgeType.ISSUE));
		chartCreator.addChartWithIssueContent(
				"For how many " + jiraIssueType.getName() + " types is a decision documented?",
				"piechartRich-IssueDocumentedForSelectedJiraIssue", rationaleCoverageCalculator
						.getJiraIssuesWithNeighborsOfOtherType(jiraIssueType, KnowledgeType.DECISION));

		metrics.putAll(chartCreator.getVelocityParameters());
		return metrics;
	}
}