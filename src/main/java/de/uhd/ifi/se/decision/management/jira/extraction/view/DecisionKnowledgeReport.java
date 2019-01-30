package de.uhd.ifi.se.decision.management.jira.extraction.view;

import java.util.HashMap;
import java.util.Map;
import com.atlassian.jira.bc.issue.search.SearchService;

import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class DecisionKnowledgeReport extends AbstractReport {

	@JiraImport
	private final ProjectManager projectManager;

	private Long projectId;

	private SearchService searchService;

	private String jiraIssueTypeToLinkTo;

	public static org.json.JSONObject restResponse;

	// Need these constructurs, instead bean exception
	public DecisionKnowledgeReport(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	// Need these constructurs, instead bean exception
	public DecisionKnowledgeReport(ProjectManager projectManager, String rootType) {
		this.projectManager = projectManager;
	}

	@SuppressWarnings("rawtypes")
	public String generateReportHtml(ProjectActionSupport action, Map params) throws Exception {
		Map<String, Object> velocityParams = createValues(action);
		return descriptor.getHtml("view", velocityParams);
	}

	public Map<String, Object> createValues(ProjectActionSupport action) {

		CommentMetricCalculator calculator = new CommentMetricCalculator(this.projectId, action.getLoggedInUser(), this.jiraIssueTypeToLinkTo);

		Map<String, Object> velocityParams = new HashMap<>();
		velocityParams.put("projectName", action.getProjectManager().getProjectObj(this.projectId).getName());

		// get Number of comments per Issue
		velocityParams.put("numCommentsPerIssueMap", calculator.getNumberOfCommentsPerIssueMap());

		// get Number of Decisions per Issue
		velocityParams.put("numDecisionsPerIssueMap",
				calculator.getNumberOfSentencePerIssueMap(KnowledgeType.DECISION));

		// get Number of Issues per Issue
		velocityParams.put("numIssuesPerIssueMap",
				calculator.getNumberOfSentencePerIssueMap(KnowledgeType.ISSUE));

		// get Number of relevant Sentences per Issue
		velocityParams.put("numRelevantSentences", calculator.getNumberOfRelevantSentences());
		velocityParams.put("map", Map.class); // TODO: what was this for? It is not used in vm.

		// get Number of commits per Issue
		velocityParams.put("numCommitsPerIssueMap", calculator.getNumberOfCommitsPerIssueMap());

		// Get associated Knowledge Types in Sentences per Issue
		velocityParams.put("numKnowledgeTypesPerIssue", calculator.getDecKnowElementsPerIssue());

		// Get types of decisions and alternatives linkes to Issue (e.g. has decision
		// but no alternative)
		velocityParams.put("numLinksToIssue", calculator.getLinkToOtherElement(KnowledgeType.ISSUE, KnowledgeType.DECISION));
		velocityParams.put("issuesWithoutDecisionLinks", calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.ISSUE));
		velocityParams.put("numLinksToDecision", calculator.getLinkToOtherElement(KnowledgeType.DECISION, KnowledgeType.ISSUE));
		velocityParams.put("decisionsWithoutIssueLinks", calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.DECISION));

		// Get Number of Alternatives With Arguments
		velocityParams.put("numAlternativeWoArgument", calculator.getAlternativeArguments());
		velocityParams.put("issuesWithAltWoArg", calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.ALTERNATIVE));

		// Get Link Distance
		velocityParams.put("numLinkDistanceAlternative", calculator.getLinkDistance(KnowledgeType.ALTERNATIVE));
		velocityParams.put("numLinkDistanceIssue", calculator.getLinkDistance(KnowledgeType.ISSUE));
		velocityParams.put("numLinkDistanceDecision", calculator.getLinkDistance(KnowledgeType.DECISION));

		velocityParams.put("numLinksToIssueTypeIssue", calculator.getLinksToIssueTypeMap(KnowledgeType.ISSUE));
		velocityParams.put("jiraIssuesWithoutLinksToIssue", calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.ISSUE));

		velocityParams.put("numLinksToIssueTypeDecision",calculator.getLinksToIssueTypeMap(KnowledgeType.DECISION));
		velocityParams.put("jiraIssuesWithoutLinksToDecision",calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.DECISION));

		velocityParams.put("issueType", calculator.getPropperStringForBugAndTasksFromIssueType());

		return velocityParams;
	}

	/**
	 * Seems to be uncalled, but is called by atlassian during execution to transfer
	 * velocity variables.
	 */
	@SuppressWarnings("rawtypes")
	public void validate(ProjectActionSupport action, Map params) {
		this.projectId = ParameterUtils.getLongParam(params, "selectedProjectId");
		this.jiraIssueTypeToLinkTo = ParameterUtils.getStringParam(params, "rootType");
	}


}