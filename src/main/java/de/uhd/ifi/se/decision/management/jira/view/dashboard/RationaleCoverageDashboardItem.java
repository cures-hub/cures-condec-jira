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

		RationaleCoverageCalculator rationaleCoverageCalculator = new RationaleCoverageCalculator(user, filterSettings);

		metrics.put("decisionsPerJiraIssue",
				rationaleCoverageCalculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.DECISION));
		metrics.put("issuesPerJiraIssue",
				rationaleCoverageCalculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.ISSUE));
		metrics.put("decisionDocumentedForSelectedJiraIssue",
				rationaleCoverageCalculator.getJiraIssuesWithNeighborsOfOtherType(jiraIssueType, KnowledgeType.ISSUE));
		metrics.put("issueDocumentedForSelectedJiraIssue", rationaleCoverageCalculator
				.getJiraIssuesWithNeighborsOfOtherType(jiraIssueType, KnowledgeType.DECISION));

		return metrics;
	}
}