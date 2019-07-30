package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.JiraQueryHandlerImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImplFiltered;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;

/**
 * Extracts the Element search Items from the JQL and SearchString
 */
public class FilterExtractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(FilterExtractor.class);
	private ApplicationUser user;
	private FilterSettings filterSettings;
	private JiraQueryHandler queryHandler;

	public FilterExtractor(String projectKey, ApplicationUser user, String filterString) {
		if (projectKey == null || projectKey.equals("") || filterString == null || user == null) {
			LOGGER.error("FilterExtractor could not be created due to an invalid input.");
			return;
		}
		this.filterSettings = new FilterSettingsImpl(projectKey, filterString);
		this.user = user;
		this.queryHandler = new JiraQueryHandlerImpl(user, projectKey, filterSettings.getSearchString());
	}

	public FilterExtractor(ApplicationUser user, FilterSettings filterSettings) {
		if (filterSettings == null || user == null) {
			LOGGER.error("FilterExtractor could not be created due to an invalid input.");
			return;
		}
		this.user = user;
		this.filterSettings = filterSettings;
		this.queryHandler = new JiraQueryHandlerImpl(user, filterSettings.getProjectKey(),
				filterSettings.getSearchString());
	}

	/**
	 * Used for the export of decision knowledge
	 */
	public List<List<DecisionKnowledgeElement>> getAllGraphs() {
		List<DecisionKnowledgeElement> tempQueryResult = getAllElementsMatchingQuery();
		List<DecisionKnowledgeElement> addedElements = new ArrayList<DecisionKnowledgeElement>();
		List<List<DecisionKnowledgeElement>> elementsQueryLinked = new ArrayList<List<DecisionKnowledgeElement>>();

		// now iti over query result
		for (DecisionKnowledgeElement current : tempQueryResult) {
			// check if in addedElements list
			if (!addedElements.contains(current)) {
				// if not get the connected tree
				String currentElementKey = current.getKey();
				List<DecisionKnowledgeElement> filteredElements = getElementsInGraph(currentElementKey);
				// add each element to the list
				addedElements.addAll(filteredElements);
				// add list to the big list
				elementsQueryLinked.add(filteredElements);
			}
		}
		return elementsQueryLinked;
	}

	private List<DecisionKnowledgeElement> getElementsInGraph(String elementKey) {
		Graph graph;
		if (queryHandler.getQueryType() != JiraQueryType.OTHER) {
			graph = new GraphImplFiltered(filterSettings.getProjectKey(), elementKey, this);
		} else {
			graph = new GraphImpl(filterSettings.getProjectKey(), elementKey);
		}
		return graph.getAllElements();
	}

	// Problem Filtered Issues from sideFilter will be filterd again
	// In the end there are only 2 Issues left that are not matching with the
	// location so everything is collapsed
	public List<DecisionKnowledgeElement> getAllElementsMatchingQuery() {
		List<DecisionKnowledgeElement> results = new ArrayList<DecisionKnowledgeElement>();
		List<Issue> jiraIssues = queryHandler.getJiraIssuesFromQuery();
		if (jiraIssues == null) {
			return results;
		}
		// Search in every Jira issue for decision knowledge elements and if
		// there are some add them
		for (Issue currentIssue : jiraIssues) {
			// Add all Matching Elements from Query as a DecisionKnowledgeElement
			results.add(new DecisionKnowledgeElementImpl(currentIssue));
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

	public List<DecisionKnowledgeElement> getAllElementsMatchingCompareFilter() {
		if (filterSettings.getProjectKey() == null) {
			return new ArrayList<>();
		}
		AbstractPersistenceManager strategy = AbstractPersistenceManager
				.getDefaultPersistenceStrategy(filterSettings.getProjectKey());
		List<DecisionKnowledgeElement> elements = strategy.getDecisionKnowledgeElements();
		AbstractPersistenceManager jiraIssueCommentPersistenceManager = new JiraIssueTextPersistenceManager(
				filterSettings.getProjectKey());
		elements.addAll(jiraIssueCommentPersistenceManager.getDecisionKnowledgeElements());

		List<DecisionKnowledgeElement> filteredElements = new ArrayList<>();

		for (DecisionKnowledgeElement element : elements) {
			// Check if the  Type of the Element is correct
			if(filterSettings.getNamesOfSelectedJiraIssueTypes().contains(element.getTypeAsString())) {
				// Check if the element is created in time
				if (checkIfJiraTextMatchesFilter(element)) {
					// Case no text filter
					if (filterSettings.getSearchString().equals("") || filterSettings.getSearchString().equals("?filter=-4")) {
						filteredElements.add(element);
					} else {
						if (element.getDescription() != null && element.getSummary() != null) {
							// Case Description or summary are containing the search sting
							if (element.getDescription().contains(filterSettings.getSearchString()) || element.getSummary().contains(filterSettings.getSearchString())) {
								filteredElements.add(element);
							}
						}
					}
				}
			}
		}
		return filteredElements;
	}

	private boolean checkIfJiraTextMatchesFilter(DecisionKnowledgeElement element) {
		return !(filterSettings.getCreatedEarliest() > 0
				&& element.getCreated().getTime() < filterSettings.getCreatedEarliest())
				|| !(filterSettings.getCreatedLatest() > 0
						&& element.getCreated().getTime() > filterSettings.getCreatedLatest());
	}

	public FilterSettings getFilterSettings() {
		return this.filterSettings;
	}

	public JiraQueryHandler getQueryHandler() {
		return queryHandler;
	}

	public ApplicationUser getUser() {
		return user;
	}
}
