package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;

public class RationaleCoverageCalculator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RationaleCompletenessCalculator.class);

	private String projectKey;
	private ApplicationUser user;

	public RationaleCoverageCalculator(String projectKey) {
		this.projectKey = projectKey;
	}

	public RationaleCoverageCalculator(ApplicationUser user, FilterSettings filterSettings) {
		this(filterSettings.getProjectKey());
		this.user = user;
	}

	// TODO Currently only link distance 1 is assessed.
	public Map<String, String> getLinksToIssueTypeMap(IssueType jiraIssueType, KnowledgeType knowledgeType) {
		LOGGER.info("RationaleCoverageCalculator getLinksToIssueTypeMap 1 3");

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

}
