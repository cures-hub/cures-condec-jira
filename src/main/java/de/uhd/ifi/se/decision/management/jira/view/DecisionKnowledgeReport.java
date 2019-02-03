package de.uhd.ifi.se.decision.management.jira.view;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import de.uhd.ifi.se.decision.management.jira.extraction.view.CommentMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Renders the report page.
 */
public class DecisionKnowledgeReport extends AbstractReport {

	@JiraImport
	private final ProjectManager projectManager;
	private long projectId;
	private String jiraIssueType;

	// Constructur is needed to prevent bean exception
	public DecisionKnowledgeReport(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	// Constructur is needed to prevent bean exception
	public DecisionKnowledgeReport(ProjectManager projectManager, String rootType) {
		this.projectManager = projectManager;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public String generateReportHtml(ProjectActionSupport action, Map params) throws Exception {
		Map<String, Object> velocityParams = createValues(action);
		return descriptor.getHtml("view", velocityParams);
	}

	public Map<String, Object> createValues(ProjectActionSupport action) {
		CommentMetricCalculator calculator = new CommentMetricCalculator(this.projectId, action.getLoggedInUser(),
				this.jiraIssueType);

		Map<String, Object> velocityParams = new HashMap<String, Object>();
		velocityParams.put("projectName", action.getProjectManager().getProjectObj(this.projectId).getName());

		// Number of comments per JIRA issue
		velocityParams.put("numCommentsPerIssueMap", calculator.getNumberOfCommentsPerIssueMap());

		// Number of decisions per JIRA issue
		velocityParams.put("numDecisionsPerIssueMap",
				calculator.getNumberOfSentencePerIssueMap(KnowledgeType.DECISION));

		// Number of issues (decision problems) per JIRA issue
		velocityParams.put("numIssuesPerIssueMap", calculator.getNumberOfSentencePerIssueMap(KnowledgeType.ISSUE));

		// Number of relevant sentences per JIRA issue
		velocityParams.put("numRelevantSentences", calculator.getNumberOfRelevantSentences());

		// Number of commits per JIRA issue
		velocityParams.put("numCommitsPerIssueMap", calculator.getNumberOfCommitsPerIssueMap());

		// Get associated Knowledge Types in Sentences per Issue
		velocityParams.put("numKnowledgeTypesPerIssue", calculator.getDecKnowElementsPerIssue());

		// Get types of decisions and alternatives linkes to Issue (e.g. has decision
		// but no alternative)
		velocityParams.put("numLinksToIssue",
				calculator.getLinkToOtherElement(KnowledgeType.ISSUE, KnowledgeType.DECISION));
		velocityParams.put("issuesWithoutDecisionLinks",
				calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.ISSUE));
		velocityParams.put("numLinksToDecision",
				calculator.getLinkToOtherElement(KnowledgeType.DECISION, KnowledgeType.ISSUE));
		velocityParams.put("decisionsWithoutIssueLinks",
				calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.DECISION));

		// Get Number of Alternatives With Arguments
		velocityParams.put("numAlternativeWoArgument", calculator.getAlternativeArguments());
		velocityParams.put("issuesWithAltWoArg",
				calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.ALTERNATIVE));

		// Get Link Distance
		velocityParams.put("numLinkDistanceAlternative", calculator.getLinkDistance(KnowledgeType.ALTERNATIVE));
		velocityParams.put("numLinkDistanceIssue", calculator.getLinkDistance(KnowledgeType.ISSUE));
		velocityParams.put("numLinkDistanceDecision", calculator.getLinkDistance(KnowledgeType.DECISION));

		velocityParams.put("numLinksToIssueTypeIssue", calculator.getLinksToIssueTypeMap(KnowledgeType.ISSUE));
		velocityParams.put("jiraIssuesWithoutLinksToIssue",
				calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.ISSUE));

		velocityParams.put("numLinksToIssueTypeDecision", calculator.getLinksToIssueTypeMap(KnowledgeType.DECISION));
		velocityParams.put("jiraIssuesWithoutLinksToDecision",
				calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.DECISION));

		velocityParams.put("issueType", calculator.getPropperStringForBugAndTasksFromIssueType());

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
		this.jiraIssueType = ParameterUtils.getStringParam(params, "issueType");
	}
}