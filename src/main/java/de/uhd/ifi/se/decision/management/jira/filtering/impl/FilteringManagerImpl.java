package de.uhd.ifi.se.decision.management.jira.filtering.impl;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.traverse.BreadthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryHandler;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryType;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.Node;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;

/**
 * Class for accessing the filtered knowledge graphs. The filter criteria are
 * specified in the {@link FilterSettings} class.
 * 
 * @see FilterSettings
 * @see KnowledgeGraph
 */
public class FilteringManagerImpl implements FilteringManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(FilteringManagerImpl.class);
	private ApplicationUser user;
	private FilterSettings filterSettings;

	public FilteringManagerImpl(ApplicationUser user, FilterSettings filterSettings) {
		if (filterSettings == null || filterSettings.getProjectKey() == null || user == null) {
			LOGGER.error("FilterExtractor could not be created due to an invalid input.");
			return;
		}
		this.user = user;
		this.filterSettings = filterSettings;
	}

	public FilteringManagerImpl(String projectKey, ApplicationUser user, String query) {
		this(user, new FilterSettingsImpl(projectKey, query));
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
			allGraphs.add(this.getAllElementsMatchingFilterSettings());
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
			return this.getAllElementsMatchingFilterSettings();
		}

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager
				.getOrCreate(filterSettings.getProjectKey()).getJiraIssueTextManager();

		List<DecisionKnowledgeElement> results = new ArrayList<DecisionKnowledgeElement>();
		// Retrieve linked decision knowledge elements for every Jira issue
		for (Issue currentJiraIssue : jiraIssues) {
			// Add all Matching Elements from Query as a DecisionKnowledgeElement
			results.add(new DecisionKnowledgeElementImpl(currentJiraIssue));
			List<DecisionKnowledgeElement> elements = persistenceManager
					.getElementsInJiraIssue(currentJiraIssue.getId());
			for (DecisionKnowledgeElement currentElement : elements) {
				if (results.contains(currentElement)) {
					continue;
				}
				if (isElementMatchingTimeFilter(currentElement)) {
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
	public List<DecisionKnowledgeElement> getAllElementsMatchingFilterSettings() {
		if (filterSettings == null || filterSettings.getProjectKey() == null) {
			return new ArrayList<DecisionKnowledgeElement>();
		}
		List<DecisionKnowledgeElement> elements = KnowledgePersistenceManager
				.getOrCreate(filterSettings.getProjectKey()).getDecisionKnowledgeElements();
		return filterElements(elements);
	}

	private List<DecisionKnowledgeElement> filterElements(List<DecisionKnowledgeElement> elements) {
		List<DecisionKnowledgeElement> filteredElements = new ArrayList<DecisionKnowledgeElement>();
		if (elements == null || elements.isEmpty()) {
			return filteredElements;
		}
		for (DecisionKnowledgeElement element : elements) {
			if (isElementMatchingFilterSettings(element)) {
				filteredElements.add(element);
			}
		}
		return filteredElements;
	}

	private boolean isElementMatchingFilterSettings(DecisionKnowledgeElement element) {
		if (!isElementMatchingKnowledgeTypeFilter(element)) {
			return false;
		}
		if (!isElementMatchingTimeFilter(element)) {
			return false;
		}
		if (!isElementMatchingStatusFilter(element)) {
			return false;
		}
		if (!isElementMatchingDocumentationLocationFilter(element)) {
			return false;
		}
		if (!isElementMatchingJiraQueryFilter(element)) {
			return false;
		}
		if (!isElementMatchingSubStringFilter(element)) {
			return false;
		}
		if (!isElementMatchingLinkTypeFilter(element)) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if the element is documented in one of the given
	 * {@link DocumentationLocation}s in the {@link FilterSetting}s.
	 * 
	 * @param element
	 *            {@link DecisionKnowledgeElement} object.
	 * @return true if the element is documented in one of the given
	 *         {@link DocumentationLocation}s.
	 */
	private boolean isElementMatchingDocumentationLocationFilter(DecisionKnowledgeElement element) {
		return filterSettings.getDocumentationLocations().contains(element.getDocumentationLocation());
	}

	private boolean isElementMatchingStatusFilter(DecisionKnowledgeElement element) {
		return filterSettings.getSelectedStatus().contains(element.getStatus());
	}

	/**
	 * Checks if the element is created in the given time frame in the
	 * {@link FilterSetting}s. See {@link DecisionKnowledgeElement#getCreated()}.
	 * 
	 * @param element
	 *            {@link DecisionKnowledgeElement} object.
	 * @return true if the element is created in the given time frame.
	 */
	private boolean isElementMatchingTimeFilter(DecisionKnowledgeElement element) {
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

	/**
	 * Checks if the element's description, summary, or key contains the given
	 * substring in the {@link FilterSetting}s.
	 * 
	 * @param element
	 *            {@link DecisionKnowledgeElement} object.
	 * @return true if the element's description, summary, or key contains the given
	 *         substring.
	 */
	private boolean isElementMatchingJiraQueryFilter(DecisionKnowledgeElement element) {
		String searchString = filterSettings.getSearchString().toLowerCase();
		if (JiraQueryType.getJiraQueryType(searchString) == JiraQueryType.OTHER) {
			// no JQL string or filter
			return true;
		}

		if (filterSettings.getSearchString().equals("?filter=-4")
				|| filterSettings.getSearchString().equals("?filter=allopenissues")) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if the element's description, summary, or key contains the given
	 * substring in the {@link FilterSetting}s.
	 * 
	 * @param element
	 *            {@link DecisionKnowledgeElement} object.
	 * @return true if the element's description, summary, or key contains the given
	 *         substring.
	 */
	private boolean isElementMatchingSubStringFilter(DecisionKnowledgeElement element) {
		String searchString = filterSettings.getSearchString().toLowerCase();
		if (searchString.isBlank()) {
			return true;
		}
		if (element.getDescription() != null && element.getDescription().toLowerCase().contains(searchString)) {
			return true;
		}
		if (element.getSummary() != null && element.getSummary().toLowerCase().contains(searchString)) {
			return true;
		}
		if (element.getKey() != null && element.getKey().toLowerCase().contains(searchString)) {
			return true;
		}
		return false;
	}

	private boolean isElementMatchingKnowledgeTypeFilter(DecisionKnowledgeElement element) {
		String type = element.getType().replaceProAndConWithArgument().toString();
		if (element.getType() == KnowledgeType.OTHER) {
			type = element.getTypeAsString();
		}
		if (filterSettings.getNamesOfSelectedJiraIssueTypes().contains(type)) {
			return true;
		}
		return false;
	}

	private boolean isElementMatchingLinkTypeFilter(DecisionKnowledgeElement element) {
		if (filterSettings.getNamesOfSelectedLinkTypes().size() == filterSettings.getAllLinkTypes().size()) {
			return true;
		}
		List<Link> links = element.getLinks();
		if (links == null || links.isEmpty()) {
			return true;
		}
		for (Link link : links) {
			if (filterSettings.getNamesOfSelectedLinkTypes().contains(link.getType())) {
				return true;
			}
		}
		return false;
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
