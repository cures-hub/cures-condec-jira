package de.uhd.ifi.se.decision.management.jira.view;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.Maps;

import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.ChartCreator;
import de.uhd.ifi.se.decision.management.jira.quality.CommentMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.quality.CommonMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.quality.MetricCalculator;

public class RequirementsDashboardItem implements ContextProvider {

    @Override
    public void init(final Map<String, String> params) throws PluginParseException {
	/**
	 * No special behaviour is foreseen for now.
	 */
    }

    @Override
    public Map<String, Object> getContextMap(final Map<String, Object> context) {
	Map<String, Object> newContext = Maps.newHashMap(context);
	Map<String, Object> projectContext = attachProjectsMaps();
	newContext.putAll(projectContext);
	SecureRandom random = new SecureRandom();
	String uid = String.valueOf(random.nextInt(10000));
	String selectId = "condec-dashboard-item-project-selection" + uid;
	newContext.put("selectID", selectId);
	newContext.put("dashboardUID", uid);
	HttpServletRequest req = getHttpReq();
	if (context.containsKey("showProject") || (req != null && req.getParameterMap().isEmpty())
		|| (req != null && req.getParameter("selectPageId") != null)) {
	    String showDiv = "configproject";
	    newContext.put("showDiv", showDiv);
	    return newContext;
	} else if (context.containsKey("showIssueType")
		|| (req != null && req.getParameter("project") != null && req.getParameter("issuetype") == null)) {
	    String showDiv = "configissuetype";
	    newContext.put("showDiv", showDiv);
	    String projectKey = "";
	    if (context.get("showIssueType") != null) {
		projectKey = (String) context.get("showIssueType");
	    } else {
		projectKey = (String) req.getParameter("project");
	    }
	    newContext.put("projectKey", projectKey);
	    Map<String, Object> issueTypeContext = attachIssueTypeMaps(projectKey);
	    newContext.putAll(issueTypeContext);
	    return newContext;
	} else {
	    String showDiv = "dynamic-content";
	    newContext.put("showDiv", showDiv);
	    String projectKey = "";
	    String issueTypeId = "";
	    if (context.containsKey("showContentProjectKey")) {
		projectKey = (String) context.get("showContentProjectKey");
		issueTypeId = (String) context.get("showContentIssueTypeId");
	    } else {
		projectKey = (String) req.getParameter("project");
		issueTypeId = (String) req.getParameter("issuetype");
	    }
	    ApplicationUser loggedUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
	    Map<String, Object> values = createValues(projectKey, issueTypeId, loggedUser);
	    newContext.putAll(values);
	    return newContext;
	}
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
	    String projectKey = project.getKey();
	    String projectName = project.getName();
	    projectNameMap.put(projectName, projectKey);
	}
	newContext.put("projectNamesMap", projectNameMap);
	return newContext;
    }

    public Map<String, Object> createValues(String projectKey, String jiraIssueTypeId, ApplicationUser loggedUser) {
	Long projectId = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey).getId();

	ChartCreator chartCreator = new ChartCreator(projectId);
	MetricCalculator metricCalculator = new MetricCalculator(projectId, loggedUser);

	chartCreator.addChart("#Comments per JIRA Issue", "boxplot-CommentsPerJiraIssue",
		metricCalculator.numberOfCommentsPerIssue());
	chartCreator.addChart("#Commits per JIRA Issue", "boxplot-CommitsPerJiraIssue",
		metricCalculator.numberOfCommitsPerIssue());
	// TODO:LinkDistance filter
	chartCreator.addChart("#Decisions per JIRA Issue", "boxplot-DecisionsPerJiraIssue",
		metricCalculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.DECISION, 1));
	chartCreator.addChart("#Issues per JIRA Issue", "boxplot-IssuesPerJiraIssue",
		metricCalculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.ISSUE, 1));
	chartCreator.addChart("#Distribution of Knowledge Types", "piechartInteger-KnowledgeTypeDistribution",
		metricCalculator.getDistributionOfKnowledgeTypes());

	// calculateCompleteness(calculator, chartNamesAndPurpose, chartNamesAndData);

	// calculateBasicStatistics(calculatorForSentences, calculator, issueTypeName,
	// chartNamesAndPurpose,
	// chartNamesAndData);

	// calculateInconsistencies(calculator, chartNamesAndPurpose,
	// chartNamesAndData);

	return chartCreator.getVelocityParameters();
    }

    private void calculateCompleteness(CommonMetricCalculator calculator, Map<String, String> chartNamesAndPurpose,
	    Map<String, Object> chartNamesAndData) {
	String chartId = "";
	/* towards rationale completeness and inconsistencies */
	chartId = "piechartRich-IssuesSolvedByDecision";
	chartNamesAndPurpose.put(chartId, "How many issues (=decision problems) are solved by a decision?");
	chartNamesAndData.put(chartId, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
		KnowledgeType.ISSUE, KnowledgeType.DECISION));
	chartId = "piechartRich-DecisionsSolvingIssues";
	chartNamesAndPurpose.put(chartId, "For how many decisions is the issue (=decision problem) documented?");
	chartNamesAndData.put(chartId, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
		KnowledgeType.DECISION, KnowledgeType.ISSUE));

	chartId = "piechartRich-ProArgumentDocumentedForAlternative";
	chartNamesAndPurpose.put(chartId, "How many alternatives have at least one pro argument documented?");
	chartNamesAndData.put(chartId, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
		KnowledgeType.ALTERNATIVE, KnowledgeType.PRO));

	chartId = "piechartRich-ConArgumentDocumentedForAlternative";
	chartNamesAndPurpose.put(chartId, "How many alternatives have at least one con argument documented?");
	chartNamesAndData.put(chartId, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
		KnowledgeType.ALTERNATIVE, KnowledgeType.CON));

	chartId = "piechartRich-ProArgumentDocumentedForDecision";
	chartNamesAndPurpose.put(chartId, "How many decisions have at least one pro argument documented?");
	chartNamesAndData.put(chartId, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
		KnowledgeType.DECISION, KnowledgeType.PRO));

	chartId = "piechartRich-ConArgumentDocumentedForDecision";
	chartNamesAndPurpose.put(chartId, "How many decisions have at least one con argument documented?");
	chartNamesAndData.put(chartId, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
		KnowledgeType.DECISION, KnowledgeType.CON));
    }

    private void calculateInconsistencies(CommonMetricCalculator calculator, Map<String, String> chartNamesAndPurpose,
	    Map<String, Object> chartNamesAndData) {
	String chartId = "";
	/* rationale inconsistencies */
	chartId = "piechartRich-IssuesSolvedByManyDecisions";
	chartNamesAndPurpose.put(chartId, "Issues (=decision problems) solved by more than one decision");
	chartNamesAndData.put(chartId,
		calculator.getDecKnowlElementsOfATypeGroupedByHavingMoreThanOneElementsOfOtherType(KnowledgeType.ISSUE,
			KnowledgeType.DECISION));
    }

    private void calculateBasicStatistics(CommentMetricCalculator calculatorForSentences,
	    CommonMetricCalculator calculator, String issueTypeName, Map<String, String> chartNamesAndPurpose,
	    Map<String, Object> chartNamesAndData) {
	String chartId = "";
	/* general statistics */
	chartId = "piechartInteger-RelevantSentences";
	chartNamesAndPurpose.put(chartId, "Relevance of Sentences in JIRA Issue Comments");
	chartNamesAndData.put(chartId, calculatorForSentences.getNumberOfRelevantSentences());

	/* selected issue type stats */
	chartId = "piechartRich-DecisionDocumentedForSelectedJiraIssue";
	chartNamesAndPurpose.put(chartId, "For how many " + issueTypeName + " types is the issue documented?");
	chartNamesAndData.put(chartId, calculator.getLinksToIssueTypeMap(KnowledgeType.ISSUE));

	chartId = "piechartRich-IssueDocumentedForSelectedJiraIssue";
	chartNamesAndPurpose.put(chartId, "For how many " + issueTypeName + " types is the decision documented?");
	chartNamesAndData.put(chartId, calculator.getLinksToIssueTypeMap(KnowledgeType.DECISION));
    }

}