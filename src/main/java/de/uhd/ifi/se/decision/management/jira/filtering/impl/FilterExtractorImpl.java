package de.uhd.ifi.se.decision.management.jira.filtering.impl;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.traverse.BreadthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterExtractor;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryHandler;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryType;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.Node;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionStatusManager;
import de.uhd.ifi.se.decision.management.jira.persistence.PersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;

/**
 * Class for accessing the filtered knowledge graphs. The filter criteria are
 * specified in the {@link FilterSettings} class.
 * 
 * @see FilterSettings
 * @see KnowledgeGraph
 */
public class FilterExtractorImpl implements FilterExtractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(FilterExtractorImpl.class);
	private ApplicationUser user;
	private FilterSettings filterSettings;

	public FilterExtractorImpl(String projectKey, ApplicationUser user, String query) {
		if (projectKey == null || projectKey.isBlank() || query == null || user == null) {
			LOGGER.error("FilterExtractor could not be created due to an invalid input.");
			return;
		}
		this.filterSettings = new FilterSettingsImpl(projectKey, query);
		this.user = user;
	}

	public FilterExtractorImpl(ApplicationUser user, FilterSettings filterSettings) {
		if (filterSettings == null || user == null) {
			LOGGER.error("FilterExtractor could not be created due to an invalid input.");
			return;
		}
		this.user = user;
		this.filterSettings = filterSettings;
	}

	/**
	 * Used for the export of decision knowledge
	 */
	@Override
	public List<List<DecisionKnowledgeElement>> getAllGraphs() {
		List<List<DecisionKnowledgeElement>> allGraphs = new ArrayList<List<DecisionKnowledgeElement>>();
		List<DecisionKnowledgeElement> addedElements = new ArrayList<DecisionKnowledgeElement>();

		List<Issue> jiraIssues = getJiraIssuesFromQuery();
		if (jiraIssues == null) {
			allGraphs.add(this.getAllElementsMatchingCompareFilter());
			return allGraphs;
		}

		// Retrieve linked decision knowledge elements for every Jira issue
		for (Issue currentIssue : jiraIssues) {
			DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(currentIssue);
			if (addedElements.contains(element)) {
				continue;
			}

			addedElements.add(element);
			List<DecisionKnowledgeElement> filteredElements = getElementsInGraph(element);
			// add each element to the list
			addedElements.addAll(filteredElements);
			// add list to the big list
			allGraphs.add(filteredElements);
		}
		return allGraphs;
	}

	private List<DecisionKnowledgeElement> getElementsInGraph(DecisionKnowledgeElement element) {
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(filterSettings.getProjectKey());
		List<DecisionKnowledgeElement> elements = new ArrayList<>();
		if (!graph.vertexSet().contains(element)) {
			elements.add(element);
			return elements;
		}
		BreadthFirstIterator<Node, Link> iterator = new BreadthFirstIterator<>(graph, element);
		while (iterator.hasNext()) {
			Node node = iterator.next();
			if (node instanceof DecisionKnowledgeElement) {
				elements.add((DecisionKnowledgeElement) node);
			}
		}
		return elements;
	}

	// Problem Filtered Issues from sideFilter will be filterd again
	// In the end there are only 2 Issues left that are not matching with the
	// location so everything is collapsed
	@Override
	public List<DecisionKnowledgeElement> getAllElementsMatchingQuery() {
		List<Issue> jiraIssues = getJiraIssuesFromQuery();
		if (jiraIssues == null) {
			return this.getAllElementsMatchingCompareFilter();
		}

		List<DecisionKnowledgeElement> results = new ArrayList<DecisionKnowledgeElement>();
		// Retrieve linked decision knowledge elements for every Jira issue
		for (Issue currentIssue : jiraIssues) {
			// Add all Matching Elements from Query as a DecisionKnowledgeElement
			results.add(new DecisionKnowledgeElementImpl(currentIssue));
			List<DecisionKnowledgeElement> elements = JiraIssueTextPersistenceManager
					.getElementsForIssue(currentIssue.getId(), filterSettings.getProjectKey());
			for (DecisionKnowledgeElement currentElement : elements) {
				if (results.contains(currentElement)) {
					continue;
				}
				if (checkIfElementMatchesTimeFilter(currentElement)) {
					results.add(currentElement);
				}
			}
		}
		return results;
	}

	private List<Issue> getJiraIssuesFromQuery() {
		if (filterSettings == null) {
			return null;
		}
		JiraQueryHandler queryHandler = new JiraQueryHandlerImpl(user, filterSettings.getProjectKey(),
				filterSettings.getSearchString());
		if (queryHandler == null || queryHandler.getQueryType() == JiraQueryType.OTHER) {
			return null;
		}
		return queryHandler.getJiraIssuesFromQuery();
	}

	@Override
	public List<DecisionKnowledgeElement> getAllElementsMatchingCompareFilter() {
		if (filterSettings == null || filterSettings.getProjectKey() == null) {
			return new ArrayList<DecisionKnowledgeElement>();
		}
		List<DecisionKnowledgeElement> elements = PersistenceManager.getOrCreate(filterSettings.getProjectKey())
				.getDecisionKnowledgeElements();
		return filterElements(elements);
	}

	// Check if the element is created in time
	private boolean checkIfElementMatchesTimeFilter(DecisionKnowledgeElement element) {
		if ((filterSettings.getCreatedEarliest() == -1 && filterSettings.getCreatedLatest() == -1)) {
			return true;
		}
		if (filterSettings.getCreatedEarliest() != -1 && filterSettings.getCreatedLatest() != -1) {
			return (element.getCreated().getTime() >= filterSettings.getCreatedEarliest()
					&& element.getCreated().getTime() <= filterSettings.getCreatedLatest());
		}
		if (filterSettings.getCreatedEarliest() != -1) {
			if (element.getCreated().getTime() >= filterSettings.getCreatedEarliest()) {
				return true;
			}
		}
		if (filterSettings.getCreatedLatest() != -1) {
			if (element.getCreated().getTime() <= filterSettings.getCreatedLatest()) {
				return true;
			}
		}
		return false;
	}

	// Check if Description, Summary, Key containing the search string
	private boolean checkIfElementMatchesStringFilter(DecisionKnowledgeElement element) {
		String searchString = filterSettings.getSearchString().toLowerCase();
		if (element.getDescription() != null) {
			if (element.getDescription().toLowerCase().contains(searchString)) {
				return true;
			}
		}
		if (element.getSummary() != null) {
			if (element.getSummary().toLowerCase().contains(searchString)) {
				return true;
			}
		}
		if (element.getKey() != null) {
			if (element.getKey().toLowerCase().contains(searchString)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkIfTypeMatches(DecisionKnowledgeElement element) {
		if (element.getTypeAsString() != null) {
			if (filterSettings.getNamesOfSelectedJiraIssueTypes().contains(element.getTypeAsString())) {
				return true;
			}
			if (element.getTypeAsString().equals("Con") || element.getTypeAsString().equals("Pro")) {
				return true;
			}
		}
		return false;
	}

	private List<DecisionKnowledgeElement> filterElements(List<DecisionKnowledgeElement> elements) {
		List<DecisionKnowledgeElement> filteredElements = new ArrayList<>();
		if (elements == null || elements.size() == 0) {
			return filteredElements;
		}
		for (DecisionKnowledgeElement element : elements) {
			// Check if the DocumentationLocation is correct
			if (filterSettings.getDocumentationLocations().contains(element.getDocumentationLocation())
					|| filterSettings.getDocumentationLocations().size() == 1 && filterSettings
							.getDocumentationLocations().get(0).equals(DocumentationLocation.UNKNOWN)) {
				// Check if the Status is filtered
				if (filterSettings.getSelectedIssueStatus()
						.contains(DecisionStatusManager.getStatusForElement(element))) {
					// Check if the Type of the Element is correct
					if (checkIfTypeMatches(element) && checkIfElementMatchesTimeFilter(element)) {
						// Case no text filter
						if (filterSettings.getSearchString().equals("")
								|| filterSettings.getSearchString().equals("?filter=-4")
								|| filterSettings.getSearchString().equals("?filter=allopenissues")) {
							filteredElements.add(element);
						} else {
							if (checkIfElementMatchesStringFilter(element)) {
								filteredElements.add(element);
							}
						}
					}
				}
			}
		}
		return filteredElements;
	}

	@Override
	public FilterSettings getFilterSettings() {
		return this.filterSettings;
	}

	@Override
	public ApplicationUser getUser() {
		return user;
	}
}
