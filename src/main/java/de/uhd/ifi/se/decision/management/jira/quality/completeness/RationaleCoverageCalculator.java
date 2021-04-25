package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.*;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
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

	public int calculateNumberOfElementsOfKnowledgeTypeWithinLinkDistance(KnowledgeElement sourceElement,
																		  KnowledgeType knowledgeType,
																		  int linkDistance) {
		Set<KnowledgeElement> elements = sourceElement.getLinkedElements(linkDistance);

		int linkedElements = 0;

		for (KnowledgeElement element : elements) {
			if (element.getType() == knowledgeType) {
				linkedElements++;
			}
		}

		return linkedElements;
	}

	public Map<String, String> getJiraIssuesWithNeighborsOfOtherType(IssueType jiraIssueType,
																	 KnowledgeType knowledgeType) {
		LOGGER.info("RationaleCoverageCalculator getJiraIssuesWithNeighborsOfOtherType");

		if (knowledgeType == null) {
			return null;
		}

		List<Issue> jiraIssues = JiraIssuePersistenceManager.getAllJiraIssuesForProjectAndType(user,
				projectKey, jiraIssueType);

		DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone(projectKey);

		int linkDistance = filterSettings.getLinkDistance();
		int threshold = definitionOfDone.getThreshold();

		String withHighLinks = "";
		String withLowLinks = "";
		String withoutLinks = "";

		for (Issue jiraIssue : jiraIssues) {
			KnowledgeElement sourceElement = new KnowledgeElement(jiraIssue);
			int linkedIssues = calculateNumberOfElementsOfKnowledgeTypeWithinLinkDistance(sourceElement,
				knowledgeType, linkDistance);
			if (linkedIssues == 0) {
				withoutLinks += jiraIssue.getKey() + " ";
			}
			else if (linkedIssues < threshold) {
				withLowLinks += jiraIssue.getKey() + " ";
			}
			else {
				withHighLinks += jiraIssue.getKey() + " ";
			}
		}

		Map<String, String> result = new LinkedHashMap<String, String>();
		result.put("Many links from " + jiraIssueType.getName() + " to " + knowledgeType.toString(), withHighLinks);
		result.put("Some links from " + jiraIssueType.getName() + " to " + knowledgeType.toString(), withLowLinks);
		result.put("No links from " + jiraIssueType.getName() + " to " + knowledgeType.toString(), withoutLinks);
		return result;
	}

	public Map<String, Integer> getNumberOfDecisionKnowledgeElementsForJiraIssues(IssueType jiraIssueType,
																				  KnowledgeType knowledgeType) {
		LOGGER.info("RequirementsDashboard getNumberOfDecisionKnowledgeElementsForJiraIssues 3 2");

		if (knowledgeType == null) {
			return null;
		}

		List<Issue> jiraIssues = JiraIssuePersistenceManager.getAllJiraIssuesForProjectAndType(user,
			projectKey, jiraIssueType);

		Set<String> types = new HashSet<>();
		types.add(knowledgeType.toString());
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
