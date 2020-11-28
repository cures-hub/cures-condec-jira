package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.LinkedHashMap;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.RationaleCompletenessCalculator;

public class RationaleCompletenessDashboardItem extends ConDecDashboardItem {

	@Override
	public Map<String, Object> getMetrics() {
		Map<String, Object> metrics = new LinkedHashMap<>();

		ChartCreator chartCreator = new ChartCreator();
		RationaleCompletenessCalculator rationaleCompletenessCalculator = new RationaleCompletenessCalculator(
				filterSettings);
		chartCreator.addChartWithIssueContent("How many issues (=decision problems) are solved by a decision?",
				"piechartRich-IssuesSolvedByDecision", rationaleCompletenessCalculator
						.getElementsWithNeighborsOfOtherType(KnowledgeType.ISSUE, KnowledgeType.DECISION));
		chartCreator.addChartWithIssueContent("For how many decisions is the issue (=decision problem) documented?",
				"piechartRich-DecisionsSolvingIssues", rationaleCompletenessCalculator
						.getElementsWithNeighborsOfOtherType(KnowledgeType.DECISION, KnowledgeType.ISSUE));
		chartCreator.addChartWithIssueContent("How many alternatives have at least one pro argument documented?",
				"piechartRich-ProArgumentDocumentedForAlternative", rationaleCompletenessCalculator
						.getElementsWithNeighborsOfOtherType(KnowledgeType.ALTERNATIVE, KnowledgeType.PRO));
		chartCreator.addChartWithIssueContent("How many alternatives have at least one con argument documented?",
				"piechartRich-ConArgumentDocumentedForAlternative", rationaleCompletenessCalculator
						.getElementsWithNeighborsOfOtherType(KnowledgeType.ALTERNATIVE, KnowledgeType.CON));
		chartCreator.addChartWithIssueContent("How many alternatives have at least one con argument documented?",
				"piechartRich-ConArgumentDocumentedForAlternative", rationaleCompletenessCalculator
						.getElementsWithNeighborsOfOtherType(KnowledgeType.ALTERNATIVE, KnowledgeType.CON));
		chartCreator.addChartWithIssueContent("How many decisions have at least one pro argument documented?",
				"piechartRich-ProArgumentDocumentedForDecision", rationaleCompletenessCalculator
						.getElementsWithNeighborsOfOtherType(KnowledgeType.DECISION, KnowledgeType.PRO));
		chartCreator.addChartWithIssueContent("How many decisions have at least one con argument documented?",
				"piechartRich-ConArgumentDocumentedForDecision", rationaleCompletenessCalculator
						.getElementsWithNeighborsOfOtherType(KnowledgeType.DECISION, KnowledgeType.CON));

		metrics.putAll(chartCreator.getVelocityParameters());
		return metrics;
	}
}