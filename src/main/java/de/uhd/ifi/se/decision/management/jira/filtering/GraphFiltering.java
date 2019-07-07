package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;

public class GraphFiltering {
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphFiltering.class);

	private ApplicationUser user;
	public FilterSettings filterSettings;
	private JiraQueryHandler queryHandler;

	public JiraQueryHandler getQueryHandler() {
		return queryHandler;
	}

	public GraphFiltering(FilterSettings filterSettings, ApplicationUser user) {
		this.filterSettings = filterSettings;
		this.user = user;
		this.queryHandler = new JiraQueryHandler(user, filterSettings.getProjectKey(),
				filterSettings.getSearchString());
	}

	public List<DecisionKnowledgeElement> getAllElementsMatchingQuery() {
		// List<DecisionKnowledgeElement> results = new ArrayList<>(getQueryResults());
		List<DecisionKnowledgeElement> results = new ArrayList<>();
		List<Issue> jiraIssuesForProject = JiraSearchServiceHelper.getAllJiraIssuesForProject(user,
				filterSettings.getProjectKey());
		if (jiraIssuesForProject == null) {
			return results;
		}
		for (Issue currentIssue : jiraIssuesForProject) {
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
		if (queryHandler.isQueryContainsCreationDate()) {

			if (queryHandler.getCreatedEarliest() > 0
					&& (element).getCreated().getTime() < queryHandler.getCreatedEarliest()) {
				return false;
			}
			if (queryHandler.getCreatedLatest() > 0
					&& (element).getCreated().getTime() > queryHandler.getCreatedLatest()) {
				return false;
			}
		}
		if (queryHandler.isQueryContainsIssueTypes()) {
			if (element.getType().equals(KnowledgeType.PRO) || element.getType().equals(KnowledgeType.CON)) {
				if (!queryHandler.getNamesOfJiraIssueTypesInQuery().contains(KnowledgeType.ARGUMENT.toString())) {
					return false;
				}
			} else if (!queryHandler.getNamesOfJiraIssueTypesInQuery().contains(element.getTypeAsString())) {
				return false;
			}
		}

		return true;
	}

	public FilterSettings getFilterData() {
		return this.filterSettings;
	}

	public void setFilterData(FilterSettings filterData) {
		this.filterSettings = filterData;
	}

	public ApplicationUser getUser() {
		return user;
	}

	public void setUser(ApplicationUser user) {
		this.user = user;
	}

	// TODO
	public List<DecisionKnowledgeElement> getQueryResults() {
		return new ArrayList<>();
	}
}