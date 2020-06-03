package de.uhd.ifi.se.decision.management.jira.view;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.Maps;
import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
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
		Map<String, Object> projectContext = attachProjectsMaps();
		newContext.putAll(projectContext);
		SecureRandom random = new SecureRandom();
		String uid = String.valueOf(random.nextInt(10000));
		String selectId = "condec-dashboard-item-project-selection" + uid;
		newContext.put("selectID", selectId);
		newContext.put("dashboardUID", uid);
		HttpServletRequest req = getHttpReq();
		if (context.containsKey("showIssueType")
				|| (req != null && req.getParameter("project") != null && req.getParameter("issuetype") == null)) {
			String showDiv = "configissuetype";
			newContext.put("showDiv", showDiv);
			String projectKey = "";
			if (context.get("showIssueType") != null) {
				projectKey = (String) context.get("showIssueType");
			} else {
				projectKey = req.getParameter("project");
			}
			newContext.put("projectKey", projectKey);
			Map<String, Object> issueTypeContext = attachIssueTypeMaps(projectKey);
			newContext.putAll(issueTypeContext);
			return newContext;
		} else if (context.containsKey("showContentProjectKey") ||
				(req != null && req.getParameter("project") != null && req.getParameter("filter") == null)) {
			String showDiv = "dynamic-content";
			newContext.put("showDiv", showDiv);
			String projectKey = "";
			String issueTypeId = "";
			if (context.containsKey("showContentProjectKey")) {
				projectKey = (String) context.get("showContentProjectKey");
				issueTypeId = (String) context.get("showContentIssueTypeId");
			} else {
				projectKey = req.getParameter("project");
				issueTypeId = req.getParameter("issuetype");
			}
			Map<String, Object> values = createValues(projectKey, issueTypeId, 2, false,
					KnowledgeType.toStringList(),
					KnowledgeStatus.toStringList(),
					null);
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
		} else if (req != null && req.getParameter("filter") != null) {
			//newContext = Maps.newHashMap(context);
			String showDiv = "dynamic-content";
			newContext.put("showDiv", showDiv);
			String projectKey = req.getParameter("project");
			String issueTypeId = req.getParameter("issueType");
			String issueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(issueTypeId);
			newContext.put("issueType", issueTypeName);
			newContext.put("issueTypeId", issueTypeId);
			newContext.put("project", projectKey);
			//Link Distance Filter
			int linkDistance = 2;
			if (req.getParameter("linkDistance") != null) {
				linkDistance = Integer.parseInt(req.getParameter("linkDistance"));
			}
			newContext.put("linkDistance", linkDistance);
			//Ignore Git Filter
			boolean ignoreGit = false;
			if (req.getParameter("ignoreGit") != null) {
				ignoreGit = true;
			}
			newContext.put("ignoreGit", req.getParameter("ignoreGit"));
			//Knowledge Type Filter
			newContext.put("knowledgeTypes", KnowledgeType.getDefaultTypes());
			List<String> knowledgeTypes = new ArrayList<String>();
			if (req.getParameterValues("knowledgeTypes") != null && req.getParameterValues("knowledgeTypes").length > 0) {
				knowledgeTypes = Arrays.asList(req.getParameterValues("knowledgeTypes"));
				newContext.put("selectedKnowledgeTypes", knowledgeTypes);
			} else {
				knowledgeTypes.addAll(KnowledgeType.toStringList());

			}
			//Knowledge Status Filter
			newContext.put("status", KnowledgeStatus.getAllKnowledgeStatus());
			List<String> knowledgeStatus = new ArrayList<String>();
			if (req.getParameterValues("status") != null && req.getParameterValues("status").length > 0) {
				knowledgeStatus = Arrays.asList(req.getParameterValues("status"));
				newContext.put("selectedStatus", knowledgeStatus);
			} else {
				knowledgeStatus.addAll(KnowledgeStatus.toStringList());
			}
			//Decision Group Filter
			newContext.put("groups", DecisionGroupManager.getAllDecisionGroups(projectKey));
			List<String> decisionGroups = new ArrayList<String>();
			if (req.getParameterValues("group") != null && req.getParameterValues("group").length > 0) {
				decisionGroups = Arrays.asList(req.getParameterValues("group"));
				newContext.put("selectedGroups", decisionGroups);
			} else {
				decisionGroups = null;
			}

			Map<String, Object> values = createValues(projectKey, issueTypeId, linkDistance, ignoreGit, knowledgeTypes, knowledgeStatus, decisionGroups);
			newContext.putAll(values);

			return newContext;
		}
		String showDiv = "configproject";
		newContext.put("showDiv", showDiv);
		return newContext;
	}

	private HttpServletRequest getHttpReq() {
		return com.atlassian.jira.web.ExecutingHttpRequest.get();
	}

	private Map<String, Object> attachIssueTypeMaps(String projectKey) {
		Map<String, Object> newContext = new HashMap<>();
		Map<String, String> IssueTypeNameMap = new TreeMap<String, String>();
		for (IssueType issuetype : JiraIssueTypeGenerator.getJiraIssueTypes(projectKey)) {
			String issueTypeId = issuetype.getId();
			String issueTypeName = issuetype.getName();
			IssueTypeNameMap.put(issueTypeName, issueTypeId);
		}
		newContext.put("issueTypeNamesMap", IssueTypeNameMap);
		return newContext;
	}

	private Map<String, Object> attachProjectsMaps() {
		Map<String, Object> newContext = new HashMap<>();
		Map<String, String> projectNameMap = new TreeMap<String, String>();
		for (Project project : ComponentAccessor.getProjectManager().getProjects()) {
			Boolean hasPermission = ComponentAccessor.getPermissionManager()
					.hasPermission(ProjectPermissions.BROWSE_PROJECTS, project, loggedUser);
			if (ConfigPersistenceManager.isActivated(project.getKey()) && hasPermission) {
				String projectKey = project.getKey();
				String projectName = project.getName();
				projectNameMap.put(projectName, projectKey);
			}
		}
		newContext.put("projectNamesMap", projectNameMap);
		return newContext;
	}

	public Map<String, Object> createValues(String projectKey, String jiraIssueTypeId, int linkDistance, boolean ignoreGit,
											List<String> knowledgeTypes,
											List<String> knowledgeStatus,
											List<String> decisionGroups) {
		Long projectId = (long) 1;
		String projectName = "";
		String issueTypeName = "";
		if (ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey) != null) {
			projectId = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey).getId();
			issueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(jiraIssueTypeId);
			projectName = ComponentAccessor.getProjectManager().getProjectObj(projectId).getName();
		}
		ChartCreator chartCreator = new ChartCreator(projectName);
		MetricCalculator metricCalculator = new MetricCalculator(projectId, loggedUser, jiraIssueTypeId, ignoreGit, knowledgeTypes, knowledgeStatus, decisionGroups);
		chartCreator.addChart("#Comments per JIRA Issue", "boxplot-CommentsPerJiraIssue",
				metricCalculator.numberOfCommentsPerIssue());
		/*
		 * chartCreator.addChart("#Commits per JIRA Issue",
		 * "boxplot-CommitsPerJiraIssue", metricCalculator.numberOfCommitsPerIssue());
		 */
		chartCreator.addChart("#Decisions per JIRA Issue", "boxplot-DecisionsPerJiraIssue",
				metricCalculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.DECISION, linkDistance));
		chartCreator.addChart("#Issues per JIRA Issue", "boxplot-IssuesPerJiraIssue",
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
		chartCreator.addChart("Comments in JIRA Issues relevant to Decision Knowledge", "piechartInteger-RelevantSentences",
				metricCalculator.getNumberOfRelevantComments());
		chartCreator.addChartWithIssueContent("For how many " + issueTypeName + " types is an issue documented?",
				"piechartRich-DecisionDocumentedForSelectedJiraIssue",
				metricCalculator.getLinksToIssueTypeMap(KnowledgeType.ISSUE, linkDistance));
		chartCreator.addChartWithIssueContent("For how many " + issueTypeName + " types is a decision documented?",
				"piechartRich-IssueDocumentedForSelectedJiraIssue",
				metricCalculator.getLinksToIssueTypeMap(KnowledgeType.DECISION, linkDistance));
		return chartCreator.getVelocityParameters();
	}
}