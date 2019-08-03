package de.uhd.ifi.se.decision.management.jira.view;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.config.properties.APKeys;

import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.CommentMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.quality.CommonMetricCalculator;

/**
 * Renders the report page.
 */
public class DecisionKnowledgeReport extends AbstractReport {

	private long projectId;
	private String jiraIssueTypeId;

	@Override
	@SuppressWarnings("rawtypes")
	public String generateReportHtml(ProjectActionSupport action, Map params) throws Exception {
		Map<String, Object> velocityParams = createValues(action);
		return descriptor.getHtml("view", velocityParams);
	}

	public Map<String, Object> createValues(ProjectActionSupport action) {
		CommentMetricCalculator calculatorForSentences = new CommentMetricCalculator(projectId, action.getLoggedInUser());
		CommonMetricCalculator calculator = new CommonMetricCalculator(projectId, action.getLoggedInUser(),
				jiraIssueTypeId);

		Map<String, Object> velocityParams = new HashMap<String, Object>();

		// Push some basic parameters
		String issueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(jiraIssueTypeId);
		String jiraBaseUrl = action.getApplicationProperties().getString(APKeys.JIRA_BASEURL);

		velocityParams.put("projectName", action.getProjectManager().getProjectObj(projectId).getName());
		velocityParams.put("jiraBaseUrl", jiraBaseUrl);

		// prepare calculations
		Map<String, String> chartNamesAndPurpose = new HashMap<String, String>();
		Map<String, Object> chartNamesAndData = new HashMap<String, Object>();
		String chartId = "";

		/* general statistics */
		chartId = "piechartInteger-RelevantSentences";
		chartNamesAndPurpose.put(chartId, "Relevance of Sentences in JIRA Issue Comments");
		chartNamesAndData.put(chartId, calculatorForSentences.getNumberOfRelevantSentences());

		chartId = "piechartInteger-KnowledgeTypeDistribution";
		chartNamesAndPurpose.put(chartId, "Distribution of Knowledge Types");
		chartNamesAndData.put(chartId, calculator.getDistributionOfKnowledgeTypes());

		/* selected issue type stats */
		chartId = "piechartRich-DecisionDocumentedForSelectedJiraIssue";
		chartNamesAndPurpose.put(chartId, "For how many JIRA issues of type "+issueTypeName+" is the issue documented?");
		chartNamesAndData.put(chartId, calculator.getLinksToIssueTypeMap(KnowledgeType.ISSUE));

		chartId = "piechartRich-IssueDocumentedForSelectedJiraIssue";
		chartNamesAndPurpose.put(chartId, "For how many JIRA issues of type "+issueTypeName+" is the decision documented?");
		chartNamesAndData.put(chartId, calculator.getLinksToIssueTypeMap(KnowledgeType.DECISION));

		/* towards rationale completeness and inconsistencies */
		chartId = "piechartRich-IssuesSolvedByDecision";
		chartNamesAndPurpose.put(chartId, "How many issues (=decision problems) are solved by a decision?");
		chartNamesAndData.put(chartId,
				calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.ISSUE
						, KnowledgeType.DECISION)
		);
		chartId = "piechartRich-DecisionsSolvingIssues";
		chartNamesAndPurpose.put(chartId, "For how many decisions is the issue (=decision problem) documented?");
		chartNamesAndData.put(chartId,
				calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.DECISION
						, KnowledgeType.ISSUE)
		);

		chartId = "piechartRich-ProArgumentDocumentedForAlternative";
		chartNamesAndPurpose.put(chartId, "How many alternatives have at least one pro argument documented?");
		chartNamesAndData.put(chartId
				, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.ALTERNATIVE
						, KnowledgeType.PRO));

		chartId = "piechartRich-ConArgumentDocumentedForAlternative";
		chartNamesAndPurpose.put(chartId, "How many alternatives have at least one con argument documented?");
		chartNamesAndData.put(chartId
				, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.ALTERNATIVE
						, KnowledgeType.CON));

		chartId = "piechartRich-ProArgumentDocumentedForDecision";
		chartNamesAndPurpose.put(chartId, "How many decisions have at least one pro argument documented?");
		chartNamesAndData.put(chartId
				, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.DECISION
						, KnowledgeType.PRO));

		chartId = "piechartRich-ConArgumentDocumentedForDecision";
		chartNamesAndPurpose.put(chartId, "How many decisions have at least one con argument documented?");
		chartNamesAndData.put(chartId
				, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.DECISION
						, KnowledgeType.CON));

		/* rationale inconsistencies */
		chartId = "piechartRich-IssuesSolvedByManyDecisions";
		chartNamesAndPurpose.put(chartId, "Issues (=decision problems) solved by more than one decision");
		chartNamesAndData.put(chartId,
				calculator.getDecKnowlElementsOfATypeGroupedByHavingMoreThanOneElementsOfOtherType(KnowledgeType.ISSUE
						, KnowledgeType.DECISION)
		);

		/* distances */
		chartId = "boxplot-CommentsPerJiraIssue";
		chartNamesAndPurpose.put(chartId, "\\#Comments per JIRA Issue");
		chartNamesAndData.put(chartId, calculatorForSentences.getNumberOfCommentsForJiraIssues());
		chartId = "boxplot-CommitsPerJiraIssue";
		chartNamesAndPurpose.put(chartId, "\\#Commits per JIRA Issue");
		chartNamesAndData.put(chartId, calculator.getNumberOfCommitsForJiraIssues());

		chartId = "boxplot-DecisionsPerJiraIssue";
		chartNamesAndPurpose.put(chartId, "\\#Decisions per JIRA Issue");
		chartNamesAndData.put(chartId,
				calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.DECISION));

		chartId = "boxplot-IssuesPerJiraIssue";
		chartNamesAndPurpose.put(chartId, "\\#Issues per JIRA Issue");
		chartNamesAndData.put(chartId,
				calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.ISSUE));

		chartId = "boxplot-LinkDistanceFromIssue";
		chartNamesAndPurpose.put(chartId, "Link Distance from Issue");
		chartNamesAndData.put(chartId,
				calculator.getLinkDistance(KnowledgeType.ISSUE));

		chartId = "boxplot-LinkDistanceFromAlternative";
		chartNamesAndPurpose.put(chartId, "Link Distance from Alternative");
		chartNamesAndData.put(chartId,
				calculator.getLinkDistance(KnowledgeType.ALTERNATIVE));

		chartId = "boxplot-LinkDistanceFromDecision";
		chartNamesAndPurpose.put(chartId, "Link Distance from Decision");
		chartNamesAndData.put(chartId,
				calculator.getLinkDistance(KnowledgeType.DECISION));


		// push gathered data to velocity template
		velocityParams.put("chartNamesAndPurpose", chartNamesAndPurpose);
		velocityParams.put("chartNamesAndData", chartNamesAndData);

		return velocityParams;
	}

	/**
	 * Seems to be uncalled, but is automatically called to transfer velocity
	 * variables.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void validate(ProjectActionSupport action, Map params) {
		this.projectId = ParameterUtils.getLongParam(params, "selectedProjectId");
		this.jiraIssueTypeId = ParameterUtils.getStringParam(params, "issueType");
	}
}