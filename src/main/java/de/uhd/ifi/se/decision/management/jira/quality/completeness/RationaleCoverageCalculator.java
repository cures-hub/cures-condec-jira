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
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;

public class RationaleCoverageCalculator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RationaleCompletenessCalculator.class);

	private String projectKey;
	private ApplicationUser user;
	private FilterSettings filterSettings;

	public RationaleCoverageCalculator(ApplicationUser user, FilterSettings filterSettings) {
		this.projectKey = filterSettings.getProjectKey();
		this.user = user;
		this.filterSettings = filterSettings;
	}

	// TODO Currently only link distance 1 is assessed. Enable higher link
	// distances.
	public Map<String, String> getJiraIssuesWithNeighborsOfOtherType(IssueType jiraIssueType,
			KnowledgeType knowledgeType) {
		LOGGER.info("RationaleCoverageCalculator getJiraIssuesWithNeighborsOfOtherType");

		if (knowledgeType == null) {
			return null;
		}

		List<Issue> jiraIssues = JiraIssuePersistenceManager.getAllJiraIssuesForProjectAndType(user,
				projectKey, jiraIssueType);

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

}
