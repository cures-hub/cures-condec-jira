package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;

/**
 * Calculates the rationale coverage of requirements, code, and other software
 * artifacts (=knowledge elements). For example, calculates how many decisions
 * are linked to a requirement or how many decisions are linked to a code file.
 */
public class RationaleCoverageCalculator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RationaleCompletenessCalculator.class);

	private String projectKey;
	private ApplicationUser user;
	private FilterSettings filterSettings;
	private FilteringManager filteringManager;
	private Map<KnowledgeElement, Map<KnowledgeType, Integer>> linkedElementMap = new HashMap<KnowledgeElement, Map<KnowledgeType, Integer>>();

	public RationaleCoverageCalculator(ApplicationUser user, FilterSettings filterSettings) {
		this.projectKey = filterSettings.getProjectKey();
		this.user = user;
		this.filterSettings = filterSettings;
		this.filteringManager = new FilteringManager(user, filterSettings);
	}

	// TODO Currently only link distance 1 is assessed. Enable higher link
	// distances.
	public Map<String, String> getJiraIssuesWithNeighborsOfOtherType(IssueType jiraIssueType,
			KnowledgeType knowledgeType) {
		LOGGER.info("RationaleCoverageCalculator getJiraIssuesWithNeighborsOfOtherType");

		if (knowledgeType == null) {
			return null;
		}

		List<Issue> jiraIssues = JiraIssuePersistenceManager.getAllJiraIssuesForProjectAndType(user, projectKey,
				jiraIssueType);

		String withLink = "";
		String withoutLink = "";

		for (Issue jiraIssue : jiraIssues) {
			KnowledgeElement sourceElement = new KnowledgeElement(jiraIssue);
			if (sourceElement.hasNeighborOfType(knowledgeType)) {
				withLink += jiraIssue.getKey() + " ";
			} else {
				withoutLink += jiraIssue.getKey() + " ";
			}
		}

		Map<String, String> result = new LinkedHashMap<String, String>();
		result.put("Links from " + jiraIssueType.getName() + " to " + knowledgeType.toString(), withLink);
		result.put("No links from " + jiraIssueType.getName() + " to " + knowledgeType.toString(), withoutLink);
		return result;
	}

	public Map<String, Integer> getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType type) {
		LOGGER.info("RequirementsDashboard getNumberOfDecisionKnowledgeElementsForJiraIssues 3 2");

		List<Issue> jiraIssues = JiraIssuePersistenceManager.getAllJiraIssuesForProject(user, projectKey);

		Set<String> types = new HashSet<>();
		types.add(type.toString());
		filterSettings.setKnowledgeTypes(types);

		Map<String, Integer> numberOfElementsReachable = new HashMap<String, Integer>();
		for (Issue jiraIssue : jiraIssues) {
			KnowledgeElement sourceElement = new KnowledgeElement(jiraIssue);
			filterSettings.setSelectedElement(sourceElement);
			FilteringManager filteringManager = new FilteringManager(user, filterSettings);
			Set<KnowledgeElement> elementsOfTargetTypeReachable = filteringManager.getElementsMatchingFilterSettings();
			numberOfElementsReachable.put(jiraIssue.getKey(), elementsOfTargetTypeReachable.size() - 1);
		}
		return numberOfElementsReachable;
	}

	private void fillLinkedElementMap(KnowledgeElement codeFile) {
		Map<KnowledgeType, Integer> knowledgeTypeMap = new HashMap<KnowledgeType, Integer>();
		Set<KnowledgeElement> linkedElements = codeFile.getLinkedElements(filterSettings.getLinkDistance());
		for (KnowledgeElement linkedElement : linkedElements) {
			if (!knowledgeTypeMap.containsKey(linkedElement.getType())) {
				knowledgeTypeMap.put(linkedElement.getType(), 0);
			}
			knowledgeTypeMap.put(linkedElement.getType(), knowledgeTypeMap.get(linkedElement.getType()) + 1);
		}
		linkedElementMap.put(codeFile, knowledgeTypeMap);
	}

	public Map<String, String> getCodeFilesWithNeighborsOfOtherType(KnowledgeType knowledgeType) {
		LOGGER.info("CodeCoverageCalculator getCodeFilesWithNeighborsOfOtherType");

		if (knowledgeType == null) {
			return null;
		}

		KnowledgeGraph graph = filteringManager.getSubgraphMatchingFilterSettings();
		List<KnowledgeElement> codeFiles = graph.getElements(KnowledgeType.CODE);

		String withLink = "";
		String withoutLink = "";

		for (KnowledgeElement codeFile : codeFiles) {
			if (!linkedElementMap.containsKey(codeFile)) {
				fillLinkedElementMap(codeFile);
			}
			if (!linkedElementMap.get(codeFile).containsKey(knowledgeType)) {
				withoutLink += projectKey + '-' + codeFile.getDescription() + " ";
			} else {
				withLink += projectKey + '-' + codeFile.getDescription() + " ";
			}
		}

		Map<String, String> result = new LinkedHashMap<String, String>();
		result.put("Links from Code File to " + knowledgeType.toString(), withLink);
		result.put("No links from Code File to " + knowledgeType.toString(), withoutLink);
		return result;
	}

	public Map<String, Integer> getNumberOfDecisionKnowledgeElementsForCodeFiles(KnowledgeType knowledgeType) {
		LOGGER.info("CodeCoverageCalculator getNumberOfDecisionKnowledgeElementsForCodeFiles");

		if (knowledgeType == null) {
			return null;
		}

		KnowledgeGraph graph = filteringManager.getSubgraphMatchingFilterSettings();
		List<KnowledgeElement> codeFiles = graph.getElements(KnowledgeType.CODE);

		Map<String, Integer> numberOfElementsReachable = new HashMap<String, Integer>();
		for (KnowledgeElement codeFile : codeFiles) {
			if (!linkedElementMap.containsKey(codeFile)) {
				fillLinkedElementMap(codeFile);
			}
			if (!linkedElementMap.get(codeFile).containsKey(knowledgeType)) {
				numberOfElementsReachable.put(projectKey + '-' + codeFile.getDescription(), 0);
			} else {
				numberOfElementsReachable.put(projectKey + '-' + codeFile.getDescription(),
						linkedElementMap.get(codeFile).get(knowledgeType));
			}
		}
		return numberOfElementsReachable;
	}

}
