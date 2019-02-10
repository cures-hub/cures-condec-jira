package de.uhd.ifi.se.decision.management.jira.view;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.action.ProjectActionSupport;

import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.extraction.metrics.CommentMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

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
		CommentMetricCalculator calculator = new CommentMetricCalculator(projectId, action.getLoggedInUser(),
				jiraIssueTypeId);

		Map<String, Object> velocityParams = new HashMap<String, Object>();
		velocityParams.put("projectName", action.getProjectManager().getProjectObj(projectId).getName());

		// Name of selected JIRA issue type
		velocityParams.put("issueType", JiraIssueTypeGenerator.getJiraIssueTypeName(jiraIssueTypeId));

		// Number of comments per JIRA issue (of the selected JIRA issue type)
		velocityParams.put("numberOfCommentsForJiraIssues", calculator.getNumberOfCommentsForJiraIssues());

		// Number of commits per JIRA issue
		velocityParams.put("numberOfCommitsForJiraIssues", calculator.getNumberOfCommitsForJiraIssues());

		// Number of issues (decision problems) per JIRA issue
		velocityParams.put("numberOfIssuesForJiraIssues",
				calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.ISSUE));

		// Number of decisions per JIRA issue
		velocityParams.put("numberOfDecisionsForJiraIssues",
				calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.DECISION));

		// Link distance from elements with certain knowledge type
		velocityParams.put("numLinkDistanceIssue", calculator.getLinkDistance(KnowledgeType.ISSUE));
		velocityParams.put("numLinkDistanceAlternative", calculator.getLinkDistance(KnowledgeType.ALTERNATIVE));
		velocityParams.put("numLinkDistanceDecision", calculator.getLinkDistance(KnowledgeType.DECISION));

		// Number of relevant sentences per JIRA issue
		velocityParams.put("numRelevantSentences", calculator.getNumberOfRelevantSentences());

		// Distribution of Knowledge Types in JIRA project
		velocityParams.put("distriutionOfKnowledgeTypesInProject", calculator.getDistributionOfKnowledgeTypes());

		// How many issues (=decision problems) are solved by a decision?
		velocityParams.put("numberOfLinksFromIssuesToDecisions",
				calculator.getNumberOfLinksToOtherElement(KnowledgeType.ISSUE, KnowledgeType.DECISION));
		velocityParams.put("issuesWithoutDecisionLinks",
				calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.ISSUE));

		// For how many decisions is the issue (=decision problem) documented?
		velocityParams.put("numberOfLinksFromDecisionsToIssues",
				calculator.getNumberOfLinksToOtherElement(KnowledgeType.DECISION, KnowledgeType.ISSUE));
		velocityParams.put("decisionsWithoutIssueLinks",
				calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.DECISION));

		// How many alternatives have at least one argument documented?
		velocityParams.put("numAlternativeWoArgument", calculator.getAlternativeArguments());
		velocityParams.put("issuesWithAltWoArg",
				calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.ALTERNATIVE));

		velocityParams.put("numLinksToIssueTypeIssue", calculator.getLinksToIssueTypeMap(KnowledgeType.ISSUE));
		velocityParams.put("jiraIssuesWithoutLinksToIssue",
				calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.ISSUE));

		velocityParams.put("numLinksToIssueTypeDecision", calculator.getLinksToIssueTypeMap(KnowledgeType.DECISION));
		velocityParams.put("jiraIssuesWithoutLinksToDecision",
				calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.DECISION));

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