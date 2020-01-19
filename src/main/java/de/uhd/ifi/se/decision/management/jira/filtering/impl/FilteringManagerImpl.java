package de.uhd.ifi.se.decision.management.jira.filtering.impl;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.traverse.BreadthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryHandler;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryType;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

/**
 * Class for accessing the filtered knowledge graph. The filter criteria are
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

	@Override
	public List<DecisionKnowledgeElement> getAllElementsMatchingFilterSettings() {
		if (filterSettings == null || filterSettings.getProjectKey() == null) {
			return new ArrayList<DecisionKnowledgeElement>();
		}
		String searchString = filterSettings.getSearchString().toLowerCase();
		if (JiraQueryType.getJiraQueryType(searchString) == JiraQueryType.OTHER) {
			List<DecisionKnowledgeElement> elements = KnowledgePersistenceManager
					.getOrCreate(filterSettings.getProjectKey()).getDecisionKnowledgeElements();
			return filterElements(elements);
		}
		return getAllElementsMatchingQuery();
	}

	private List<DecisionKnowledgeElement> getAllElementsMatchingQuery() {
		List<Issue> jiraIssues = getJiraIssuesFromQuery();
		return getElementsInJiraIssuesMatchingFilterSettings(jiraIssues);
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

	private List<DecisionKnowledgeElement> getElementsInJiraIssuesMatchingFilterSettings(List<Issue> jiraIssues) {
		List<DecisionKnowledgeElement> elements = new ArrayList<DecisionKnowledgeElement>();
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(filterSettings.getProjectKey());

		for (Issue jiraIssue : jiraIssues) {
			DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(jiraIssue);
			if (elements.contains(element)) {
				continue;
			}

			elements.add(element);

			if (!graph.vertexSet().contains(element)) {
				graph.addVertex(element);
			}

			BreadthFirstIterator<DecisionKnowledgeElement, Link> iterator = new BreadthFirstIterator<DecisionKnowledgeElement, Link>(
					graph, element);
			while (iterator.hasNext()) {
				DecisionKnowledgeElement node = iterator.next();
				if (!elements.contains(node) && isElementMatchingFilterSettings(node)) {
					elements.add(node);
				}
			}
		}
		return elements;
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

	@Override
	public boolean isElementMatchingFilterSettings(DecisionKnowledgeElement element) {
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
		if (!isElementMatchingSubStringFilter(element)) {
			return false;
		}
		return isElementMatchingLinkTypeFilter(element);
	}

	@Override
	public boolean isElementMatchingDocumentationLocationFilter(DecisionKnowledgeElement element) {
		return filterSettings.getDocumentationLocations().contains(element.getDocumentationLocation());
	}

	@Override
	public boolean isElementMatchingStatusFilter(DecisionKnowledgeElement element) {
		return filterSettings.getSelectedStatus().contains(element.getStatus());
	}

	@Override
	public boolean isElementMatchingTimeFilter(DecisionKnowledgeElement element) {
		boolean isMatchingTimeFilter = true;
		if (filterSettings.getCreatedEarliest() != -1) {
			isMatchingTimeFilter = element.getCreated().getTime() >= filterSettings.getCreatedEarliest();
		}
		if (filterSettings.getCreatedLatest() != -1) {
			isMatchingTimeFilter = isMatchingTimeFilter
					&& element.getCreated().getTime() <= filterSettings.getCreatedLatest();
		}
		return isMatchingTimeFilter;
	}

	@Override
	public boolean isElementMatchingSubStringFilter(DecisionKnowledgeElement element) {
		String searchString = filterSettings.getSearchString().toLowerCase();
		if (JiraQueryType.getJiraQueryType(searchString) != JiraQueryType.OTHER) {
			// JQL string or filter
			return true;
		}
		if (searchString.isBlank()) {
			return true;
		}
		if (element.getDescription() != null && element.getDescription().toLowerCase().contains(searchString)) {
			return true;
		}
		if (element.getSummary() != null && element.getSummary().toLowerCase().contains(searchString)) {
			return true;
		}
		return element.getKey() != null && element.getKey().toLowerCase().contains(searchString);
	}

	@Override
	public boolean isElementMatchingKnowledgeTypeFilter(DecisionKnowledgeElement element) {
		String type = element.getType().replaceProAndConWithArgument().toString();
		if (element.getType() == KnowledgeType.OTHER) {
			type = element.getTypeAsString();
		}
		if (filterSettings.getNamesOfSelectedJiraIssueTypes().contains(type)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isElementMatchingLinkTypeFilter(DecisionKnowledgeElement element) {
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
	public void setFilterSettings(FilterSettings filterSettings) {
		this.filterSettings = filterSettings;
	}

	@Override
	public ApplicationUser getUser() {
		return user;
	}

	@Override
	public void setUser(ApplicationUser user) {
		this.user = user;
	}
}
