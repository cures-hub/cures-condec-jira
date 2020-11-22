package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.Maps;

import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.MetricCalculator;

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
		String projectKey = request.getParameter("project");
		newContext.put("projectKey", projectKey);

		String issueTypeId = request.getParameter("issuetype");
		newContext.put("issueTypeId", issueTypeId);
		String issueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(issueTypeId);
		newContext.put("issueType", issueTypeName);
		// int linkDistance = request.getParameter("linkDistance");
		newContext.put("projectKey", projectKey);
		newContext.put("jiraIssueTypes", JiraIssueTypeGenerator.getJiraIssueTypes(projectKey));
		newContext.put("jiraBaseUrl", ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL));
		// TODO Create and work with FilterSettings object
		Map<String, Object> values = createValues(projectKey, issueTypeId, 2, KnowledgeType.toStringList(),
				KnowledgeStatus.toStringList(), null);
		newContext.putAll(values);
		return newContext;
	}

	private HttpServletRequest getHttpRequest() {
		return com.atlassian.jira.web.ExecutingHttpRequest.get();
	}

	public Map<String, Object> createValues(String projectKey, String jiraIssueTypeId, int linkDistance,
			List<String> knowledgeTypes, List<String> knowledgeStatus, List<String> decisionGroups) {
		Long projectId = (long) 1;
		String projectName = "";
		String issueTypeName = "";
		if (ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey) != null) {
			projectId = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey).getId();
			issueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(jiraIssueTypeId);
			projectName = ComponentAccessor.getProjectManager().getProjectObj(projectId).getName();
		}
		ChartCreator chartCreator = new ChartCreator(projectName);
		MetricCalculator metricCalculator = new MetricCalculator(projectId, loggedUser, jiraIssueTypeId, knowledgeTypes,
				knowledgeStatus, decisionGroups);
		chartCreator.addChart("#Comments per Jira Issue", "boxplot-CommentsPerJiraIssue",
				metricCalculator.numberOfCommentsPerIssue());
		/*
		 * chartCreator.addChart("#Commits per Jira Issue",
		 * "boxplot-CommitsPerJiraIssue", metricCalculator.numberOfCommitsPerIssue());
		 */
		chartCreator.addChart("#Decisions per Jira Issue", "boxplot-DecisionsPerJiraIssue", metricCalculator
				.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.DECISION, linkDistance));
		chartCreator.addChart("#Issues per Jira Issue", "boxplot-IssuesPerJiraIssue",
				metricCalculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.ISSUE, linkDistance));
		chartCreator.addChart("Distribution of Knowledge Types", "piechartInteger-KnowledgeTypeDistribution",
				metricCalculator.getDistributionOfKnowledgeTypes());
		chartCreator.addChart("#Requirements and Code Classes", "piechartInteger-ReqCodeSummary",
				metricCalculator.getReqAndClassSummary());
		chartCreator.addChart("#Elements from Decision Knowledge Sources", "piechartInteger-DecSources",
				metricCalculator.getKnowledgeSourceCount());
		chartCreator.addChartWithIssueContent("How many issues (=decision problems) are solved by a decision?",
				"piechartRich-IssuesSolvedByDecision",
				metricCalculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.ISSUE,
						KnowledgeType.DECISION));
		chartCreator.addChartWithIssueContent("For how many decisions is the issue (=decision problem) documented?",
				"piechartRich-DecisionsSolvingIssues",
				metricCalculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.DECISION,
						KnowledgeType.ISSUE));
		chartCreator.addChartWithIssueContent("How many alternatives have at least one pro argument documented?",
				"piechartRich-ProArgumentDocumentedForAlternative",
				metricCalculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.ALTERNATIVE,
						KnowledgeType.PRO));
		chartCreator.addChartWithIssueContent("How many alternatives have at least one con argument documented?",
				"piechartRich-ConArgumentDocumentedForAlternative",
				metricCalculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.ALTERNATIVE,
						KnowledgeType.CON));
		chartCreator.addChartWithIssueContent("How many alternatives have at least one con argument documented?",
				"piechartRich-ConArgumentDocumentedForAlternative",
				metricCalculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.ALTERNATIVE,
						KnowledgeType.CON));
		chartCreator.addChartWithIssueContent("How many decisions have at least one pro argument documented?",
				"piechartRich-ProArgumentDocumentedForDecision",
				metricCalculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.DECISION,
						KnowledgeType.PRO));
		chartCreator.addChartWithIssueContent("How many decisions have at least one con argument documented?",
				"piechartRich-ConArgumentDocumentedForDecision",
				metricCalculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.DECISION,
						KnowledgeType.CON));
		chartCreator.addChart("Comments in Jira Issues relevant to Decision Knowledge",
				"piechartInteger-RelevantSentences", metricCalculator.getNumberOfRelevantComments());
		chartCreator.addChartWithIssueContent("For how many " + issueTypeName + " types is an issue documented?",
				"piechartRich-DecisionDocumentedForSelectedJiraIssue",
				metricCalculator.getLinksToIssueTypeMap(KnowledgeType.ISSUE, linkDistance));
		chartCreator.addChartWithIssueContent("For how many " + issueTypeName + " types is a decision documented?",
				"piechartRich-IssueDocumentedForSelectedJiraIssue",
				metricCalculator.getLinksToIssueTypeMap(KnowledgeType.DECISION, linkDistance));
		return chartCreator.getVelocityParameters();
	}
}