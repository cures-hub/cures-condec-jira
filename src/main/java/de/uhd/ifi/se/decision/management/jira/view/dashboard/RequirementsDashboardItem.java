package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.Maps;

import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.quality.MetricCalculator;

public class RequirementsDashboardItem implements ContextProvider {

	public ApplicationUser loggedUser;

	@Override
	public void init(final Map<String, String> params) throws PluginParseException {
		/**
		 * No special behaviour is foreseen for now.
		 */
	}

	@Override
	public Map<String, Object> getContextMap(final Map<String, Object> context) {
		if (ComponentAccessor.getJiraAuthenticationContext() != null) {
			loggedUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		}
		Map<String, Object> newContext = Maps.newHashMap(context);
		newContext.put("projects",
				DecisionKnowledgeProject.getProjectsWithConDecActivatedAndAccessableForUser(loggedUser));
		SecureRandom random = new SecureRandom();
		String uid = String.valueOf(random.nextInt(10000));
		String selectId = "condec-dashboard-item-project-selection" + uid;
		newContext.put("selectID", selectId);
		newContext.put("dashboardUID", uid);
		HttpServletRequest request = getHttpRequest();
		if (context.containsKey("showIssueType") || (request != null && request.getParameter("project") != null
				&& request.getParameter("issuetype") == null)) {
			String showDiv = "configissuetype";
			newContext.put("showDiv", showDiv);
			String projectKey = "";
			if (context.get("showIssueType") != null) {
				projectKey = (String) context.get("showIssueType");
			} else {
				projectKey = request.getParameter("project");
			}
			newContext.put("projectKey", projectKey);
			newContext.put("jiraIssueTypes", JiraIssueTypeGenerator.getJiraIssueTypes(projectKey));
			return newContext;
		} else if (context.containsKey("showContentProjectKey") || (request != null
				&& request.getParameter("project") != null && request.getParameter("filter") == null)) {
			String showDiv = "dynamic-content";
			newContext.put("showDiv", showDiv);
			String projectKey = "";
			String issueTypeId = "";
			if (context.containsKey("showContentProjectKey")) {
				projectKey = (String) context.get("showContentProjectKey");
				issueTypeId = (String) context.get("showContentIssueTypeId");
			} else {
				projectKey = request.getParameter("project");
				issueTypeId = request.getParameter("issuetype");
			}
			Map<String, Object> values = createValues(projectKey, issueTypeId, 2, false, KnowledgeType.toStringList(),
					KnowledgeStatus.toStringList(), null);
			newContext.putAll(values);
			String issueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(issueTypeId);
			newContext.put("issueType", issueTypeName);
			newContext.put("issueTypeId", issueTypeId);
			newContext.put("project", projectKey);
			newContext.put("linkDistance", 2);
			newContext.put("knowledgeTypes", KnowledgeType.getDefaultTypes());
			newContext.put("status", KnowledgeStatus.getAllKnowledgeStatus());
			newContext.put("groups", DecisionGroupManager.getAllDecisionGroups(projectKey));
			return newContext;
		} else if (context.containsKey("showContentFilter")
				|| request != null && request.getParameter("filter") != null) {
			// newContext = Maps.newHashMap(context);
			String showDiv = "dynamic-content";
			newContext.put("showDiv", showDiv);
			String projectKey = "TEST";
			String issueTypeId = "16";
			int linkDistance = 2;
			newContext.put("linkDistance", linkDistance);
			boolean ignoreGit = false;
			List<String> knowledgeTypes = KnowledgeType.toStringList();
			List<String> knowledgeStatus = KnowledgeStatus.toStringList();
			List<String> decisionGroups = null;
			if (request != null) {
				projectKey = request.getParameter("project");
				issueTypeId = request.getParameter("issueType");
				if (request.getParameter("linkDistance") != null) {
					linkDistance = Integer.parseInt(request.getParameter("linkDistance"));
				}
				// Ignore Git Filter
				if (request.getParameter("ignoreGit") != null) {
					ignoreGit = true;
				}
				newContext.put("filterSettings", new FilterSettings(projectKey, ""));
				newContext.put("ignoreGit", request.getParameter("ignoreGit"));

				// Knowledge Type Filter
				newContext.put("knowledgeTypes", KnowledgeType.getDefaultTypes());
				if (request.getParameterValues("knowledgeTypes") != null
						&& request.getParameterValues("knowledgeTypes").length > 0) {
					knowledgeTypes = Arrays.asList(request.getParameterValues("knowledgeTypes"));
					newContext.put("selectedKnowledgeTypes", knowledgeTypes);
				}
				// Knowledge Status Filter
				newContext.put("status", KnowledgeStatus.getAllKnowledgeStatus());
				if (request.getParameterValues("status") != null && request.getParameterValues("status").length > 0) {
					knowledgeStatus = Arrays.asList(request.getParameterValues("status"));
					newContext.put("selectedStatus", knowledgeStatus);
				}

				// Decision Group Filter
				newContext.put("groups", DecisionGroupManager.getAllDecisionGroups(projectKey));
				if (request.getParameterValues("group") != null && request.getParameterValues("group").length > 0) {
					decisionGroups = new ArrayList<>();
					decisionGroups = Arrays.asList(request.getParameterValues("group"));
					newContext.put("selectedGroups", decisionGroups);
				}
			}
			String issueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(issueTypeId);
			newContext.put("issueType", issueTypeName);
			newContext.put("issueTypeId", issueTypeId);
			newContext.put("project", projectKey);
			// Link Distance Filter

			Map<String, Object> values = createValues(projectKey, issueTypeId, linkDistance, ignoreGit, knowledgeTypes,
					knowledgeStatus, decisionGroups);
			newContext.putAll(values);

			return newContext;
		}
		String showDiv = "configproject";
		newContext.put("showDiv", showDiv);
		return newContext;
	}

	private HttpServletRequest getHttpRequest() {
		return com.atlassian.jira.web.ExecutingHttpRequest.get();
	}

	public Map<String, Object> createValues(String projectKey, String jiraIssueTypeId, int linkDistance,
			boolean ignoreGit, List<String> knowledgeTypes, List<String> knowledgeStatus, List<String> decisionGroups) {
		Long projectId = (long) 1;
		String projectName = "";
		String issueTypeName = "";
		if (ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey) != null) {
			projectId = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey).getId();
			issueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(jiraIssueTypeId);
			projectName = ComponentAccessor.getProjectManager().getProjectObj(projectId).getName();
		}
		ChartCreator chartCreator = new ChartCreator(projectName);
		MetricCalculator metricCalculator = new MetricCalculator(projectId, loggedUser, jiraIssueTypeId, ignoreGit,
				knowledgeTypes, knowledgeStatus, decisionGroups);
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