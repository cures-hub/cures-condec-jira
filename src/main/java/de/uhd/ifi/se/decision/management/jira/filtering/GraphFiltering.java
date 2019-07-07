package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.impl.JiraQueryHandlerImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;

public class GraphFiltering {
	private ApplicationUser user;
	public FilterSettings filterSettings;
	private JiraQueryHandler queryHandler;

	public JiraQueryHandler getQueryHandler() {
		return queryHandler;
	}

	public GraphFiltering(FilterSettings filterSettings, ApplicationUser user) {
		this.filterSettings = filterSettings;
		this.user = user;
		this.queryHandler = new JiraQueryHandlerImpl(user, filterSettings.getProjectKey(),
				filterSettings.getSearchString());
	}

	public List<DecisionKnowledgeElement> getAllElementsMatchingQuery() {
		List<Issue> jiraIssues = queryHandler.getJiraIssuesFromQuery();
		List<DecisionKnowledgeElement> results = new ArrayList<DecisionKnowledgeElement>();
		if (jiraIssues == null) {
			return results;
		}
		for (Issue currentIssue : jiraIssues) {
			List<DecisionKnowledgeElement> elements = JiraIssueTextPersistenceManager
					.getElementsForIssue(currentIssue.getId(), filterSettings.getProjectKey());
			for (DecisionKnowledgeElement currentElement : elements) {
				if (!results.contains(currentElement) && currentElement instanceof PartOfJiraIssueText
						&& checkIfJiraTextMatchesFilter(currentElement)) {
					results.add(currentElement);
				}
			}
		}

		return results;
	}

	private boolean checkIfJiraTextMatchesFilter(DecisionKnowledgeElement element) {
		if (filterSettings.getCreatedEarliest() > 0
				&& (element).getCreated().getTime() < filterSettings.getCreatedEarliest()) {
			return false;
		}
		if (filterSettings.getCreatedLatest() > 0
				&& (element).getCreated().getTime() > filterSettings.getCreatedLatest()) {
			return false;
		}

		if (element.getType().equals(KnowledgeType.PRO) || element.getType().equals(KnowledgeType.CON)) {
			if (!filterSettings.getNamesOfSelectedJiraIssueTypes().contains(KnowledgeType.ARGUMENT.toString())) {
				return false;
			}
		} else if (!filterSettings.getNamesOfSelectedJiraIssueTypes().contains(element.getTypeAsString())) {
			return false;
		}

		return true;
	}

	public FilterSettings getFilterSettings() {
		return this.filterSettings;
	}

	public ApplicationUser getUser() {
		return user;
	}
}