package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.Maps;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.RationaleCompletenessCalculator;

public class RationaleCompletenessDashboardItem implements ContextProvider {

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

		HttpServletRequest request = GeneralMetricsDashboardItem.getHttpRequest();
		String projectKey = "";
		if (request != null) {
			projectKey = request.getParameter("project");
		}
		newContext.put("projectKey", projectKey);
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
		newContext.putAll(chartCreator.getVelocityParameters());
		return newContext;
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
}