package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.Maps;

import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.MetricCalculator;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.RationaleCompletenessCalculator;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.RationaleCoverageCalculator;

public class RequirementsDashboardItem implements ContextProvider {

	public ApplicationUser loggedUser;

	@Override
	public void init(Map<String, String> params) throws PluginParseException {
		/**
		 * No special behaviour is foreseen for now.
		 */
	}

	@Override
	public Map<String, Object> getContextMap(Map<String, Object> context) {
		if (ComponentAccessor.getJiraAuthenticationContext() != null) {
			loggedUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		}
		Map<String, Object> newContext = Maps.newHashMap(context);
		newContext.put("projects",
				DecisionKnowledgeProject.getProjectsWithConDecActivatedAndAccessableForUser(loggedUser));

		HttpServletRequest request = getHttpRequest();
		String projectKey = "";
		String issueTypeId = "0";
		if (request != null) {
			projectKey = request.getParameter("project");
			issueTypeId = request.getParameter("issueType");
		}
		newContext.put("projectKey", projectKey);
		IssueType issueType = JiraIssueTypeGenerator.getJiraIssueType(issueTypeId);
		newContext.put("issueType", issueType);
		newContext.put("jiraIssueTypes", JiraIssueTypeGenerator.getJiraIssueTypes(projectKey));
		newContext.put("jiraBaseUrl", ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL));
		if (projectKey == null || projectKey.isBlank()) {
			return newContext;
		}

		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		String linkDistance = request.getParameter("linkDistance");
		if (linkDistance != null) {
			filterSettings.setLinkDistance(Integer.parseInt(linkDistance));
		}
		ChartCreator chartCreator = new ChartCreator();
		addRationaleCompletenessCharts(chartCreator, filterSettings);
		addGeneralMetricCharts(chartCreator, filterSettings);
		if (issueType != null) {
			addRationaleCoverageCharts(chartCreator, issueType, filterSettings);
		}
		newContext.putAll(chartCreator.getVelocityParameters());
		return newContext;
	}

	public static HttpServletRequest getHttpRequest() {
		return com.atlassian.jira.web.ExecutingHttpRequest.get();
	}

	private void addRationaleCompletenessCharts(ChartCreator chartCreator, FilterSettings filterSettings) {
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
	}

	private void addRationaleCoverageCharts(ChartCreator chartCreator, IssueType jiraIssueType,
			FilterSettings filterSettings) {
		RationaleCoverageCalculator rationaleCoverageCalculator = new RationaleCoverageCalculator(loggedUser,
				filterSettings);
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
	}

	private void addGeneralMetricCharts(ChartCreator chartCreator, FilterSettings filterSettings) {
		MetricCalculator metricCalculator = new MetricCalculator(loggedUser, filterSettings);
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

		/*
		 * chartCreator.addChart("#Commits per Jira Issue",
		 * "boxplot-CommitsPerJiraIssue", metricCalculator.numberOfCommitsPerIssue());
		 */
	}
}