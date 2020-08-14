package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

/**
 * Filters the {@link KnowledgeGraph}. The filter criteria are specified in the
 * {@link FilterSettings} class.
 */
public class FilteringManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(FilteringManager.class);
	private ApplicationUser user;
	private FilterSettings filterSettings;
	private KnowledgeGraph graph;
	public Set<Link> traversedLinks;

	public FilteringManager(FilterSettings filterSettings) {
		this(null, filterSettings);
	}

	public FilteringManager(ApplicationUser user, FilterSettings filterSettings) {
		this.user = user;
		this.filterSettings = filterSettings;
		if (filterSettings != null) {
			this.graph = KnowledgeGraph.getOrCreate(filterSettings.getProjectKey());
		}
		this.traversedLinks = new HashSet<Link>();
	}

	public FilteringManager(String projectKey, ApplicationUser user, String query) {
		this(user, new FilterSettings(projectKey, query, user));
	}

	/**
	 * @return list of all knowledge elements that match the {@link FilterSetting}s.
	 */
	public List<KnowledgeElement> getElementsMatchingFilterSettings() {
		if (filterSettings == null || filterSettings.getProjectKey() == null || graph == null) {
			LOGGER.error("FilteringManager misses important attributes.");
			return new ArrayList<KnowledgeElement>();
		}
		String searchString = filterSettings.getSearchTerm().toLowerCase();
		if (JiraQueryType.getJiraQueryType(searchString) == JiraQueryType.OTHER) {
			Set<KnowledgeElement> elements = graph.vertexSet();
			return filterElements(elements);
		}
		return getAllElementsMatchingQuery();
	}

	/**
	 * @return subgraph of the {@link KnowledgeGraph} that matches the
	 *         {@link FilterSetting}s.
	 */
	public Graph<KnowledgeElement, Link> getSubgraphMatchingFilterSettings() {
		if (filterSettings == null || filterSettings.getProjectKey() == null || graph == null) {
			LOGGER.error("FilteringManager misses important attributes.");
			return null;
		}
		Set<KnowledgeElement> elements = new HashSet<KnowledgeElement>(getElementsMatchingFilterSettings());
		Graph<KnowledgeElement, Link> subgraph = new AsSubgraph<KnowledgeElement, Link>(graph, elements);
		if (filterSettings.getSelectedElement() != null) {
			subgraph = getSubgraphMatchingLinkDistance();
		}
		if (filterSettings.getLinkTypes().size() < LinkType.toStringList().size()) {
			Set<Link> linksNotMatchingFilterSettings = getLinksNotMatchingFilterSettings(subgraph.edgeSet());
			subgraph.removeAllEdges(linksNotMatchingFilterSettings);
		}
		return subgraph;
	}

	public AsSubgraph<KnowledgeElement, Link> getSubgraphMatchingLinkDistance() {
		KnowledgeElement selectedElement = filterSettings.getSelectedElement();
		int linkDistance = filterSettings.getLinkDistance();
		Set<KnowledgeElement> elements = new HashSet<KnowledgeElement>();
		elements.addAll(getLinkedElements(selectedElement, linkDistance));
		return new AsSubgraph<KnowledgeElement, Link>(graph, elements);
	}

	private Set<KnowledgeElement> getLinkedElements(KnowledgeElement currentElement, int currentDistance) {
		Set<KnowledgeElement> elements = new HashSet<KnowledgeElement>();
		elements.add(currentElement);

		if (currentDistance == 0) {
			return elements;
		}
		for (Link link : graph.edgesOf(currentElement)) {
			if (!traversedLinks.add(link)) {
				continue;
			}
			KnowledgeElement oppositeElement = link.getOppositeElement(currentElement);
			if (oppositeElement == null) {
				continue;
			}
			elements.addAll(getLinkedElements(oppositeElement, currentDistance - 1));
		}
		return elements;
	}

	private Set<Link> getLinksNotMatchingFilterSettings(Set<Link> links) {
		Set<Link> linksNotMatchingFilterSettings = new HashSet<Link>();
		for (Link link : links) {
			if (!filterSettings.getLinkTypes().contains(link.getType())) {
				linksNotMatchingFilterSettings.add(link);
			}
		}
		return linksNotMatchingFilterSettings;
	}

	private List<KnowledgeElement> getAllElementsMatchingQuery() {
		List<Issue> jiraIssues = getJiraIssuesFromQuery();
		return getElementsInJiraIssuesMatchingFilterSettings(jiraIssues);
	}

	private List<Issue> getJiraIssuesFromQuery() {
		if (filterSettings == null) {
			return null;
		}
		JiraQueryHandler queryHandler = new JiraQueryHandler(user, filterSettings.getProjectKey(),
				filterSettings.getSearchTerm());
		if (queryHandler == null || queryHandler.getQueryType() == JiraQueryType.OTHER) {
			return null;
		}
		return queryHandler.getJiraIssuesFromQuery();
	}

	private List<KnowledgeElement> getElementsInJiraIssuesMatchingFilterSettings(List<Issue> jiraIssues) {
		if (graph == null) {
			return null;
		}
		List<KnowledgeElement> elements = new ArrayList<KnowledgeElement>();
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(filterSettings.getProjectKey());

		for (Issue jiraIssue : jiraIssues) {
			KnowledgeElement element = new KnowledgeElement(jiraIssue);
			if (elements.contains(element)) {
				continue;
			}

			elements.add(element);

			if (!graph.vertexSet().contains(element)) {
				graph.addVertex(element);
			}

			BreadthFirstIterator<KnowledgeElement, Link> iterator = new BreadthFirstIterator<KnowledgeElement, Link>(
					graph, element);
			while (iterator.hasNext()) {
				KnowledgeElement node = iterator.next();
				if (!elements.contains(node) && isElementMatchingFilterSettings(node)) {
					elements.add(node);
				}
			}
		}
		return elements;
	}

	private List<KnowledgeElement> filterElements(Set<KnowledgeElement> elements) {
		List<KnowledgeElement> filteredElements = new ArrayList<KnowledgeElement>();
		if (elements == null || elements.isEmpty()) {
			return filteredElements;
		}
		for (KnowledgeElement element : elements) {
			if (isElementMatchingFilterSettings(element)) {
				filteredElements.add(element);
			}
		}
		return filteredElements;
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element matches the specified filter criteria in the
	 *         {@link FilterSetting}s.
	 */
	public boolean isElementMatchingFilterSettings(KnowledgeElement element) {
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
		if (!isElementMatchingDecisionGroupFilter(element)) {
			return false;
		}
		return isElementMatchingSubStringFilter(element);
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element is documented in one of the given
	 *         {@link DocumentationLocation}s in the {@link FilterSetting}s.
	 */
	public boolean isElementMatchingDocumentationLocationFilter(KnowledgeElement element) {
		return filterSettings.getDocumentationLocations().contains(element.getDocumentationLocation());
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element's status equals one of the given
	 *         {@link KnowledgeStatus} in the {@link FilterSetting}s.
	 */
	public boolean isElementMatchingStatusFilter(KnowledgeElement element) {
		return filterSettings.getStatus().contains(element.getStatus());
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element is created in the given time frame in the
	 *         {@link FilterSetting}s. See {@link KnowledgeElement#getCreated()}.
	 */
	public boolean isElementMatchingTimeFilter(KnowledgeElement element) {
		boolean isMatchingTimeFilter = true;
		if (filterSettings.getCreatedEarliest() != -1) {
			isMatchingTimeFilter = element.getCreated().getTime() >= filterSettings.getCreatedEarliest();
		}
		if (filterSettings.getCreatedLatest() != -1) {
			isMatchingTimeFilter = isMatchingTimeFilter
					&& element.getCreated().getTime() <= filterSettings.getCreatedLatest() + 86400000;
		}
		return isMatchingTimeFilter;
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element's description, summary, or key contains the given
	 *         substring in the {@link FilterSetting}s.
	 */
	public boolean isElementMatchingSubStringFilter(KnowledgeElement element) {
		String searchString = filterSettings.getSearchTerm().toLowerCase();
		if (searchString.isBlank()) {
			return true;
		}
		if (JiraQueryType.getJiraQueryType(searchString) != JiraQueryType.OTHER) {
			// JQL string or filter
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

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element's type equals one of the given
	 *         {@link KnowledgeType}s in the {@link FilterSetting}s.
	 */
	public boolean isElementMatchingKnowledgeTypeFilter(KnowledgeElement element) {
		String type = element.getType().replaceProAndConWithArgument().toString();
		if (element.getType() == KnowledgeType.OTHER) {
			type = element.getTypeAsString();
		}
		return filterSettings.getJiraIssueTypes().contains(type);
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element's group equals one of the given groups in the
	 *         {@link FilterSetting}s.
	 */
	public boolean isElementMatchingDecisionGroupFilter(KnowledgeElement element) {
		List<String> groups = element.getDecisionGroups();
		List<String> selectedGroups = filterSettings.getDecisionGroups();
		int matches = 0;
		for (String group : selectedGroups) {
			if (groups.contains(group)) {
				matches++;
			}
		}
		return (matches == selectedGroups.size());
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element's degree (i.e. number of links) is in between
	 *         minDegree and maxDegree in the {@link FilterSetting}s.
	 */
	public boolean isElementMatchingDegreeFilter(KnowledgeElement element) {
		int degree = element.getLinks().size();
		return degree >= filterSettings.getMinDegree() && degree <= filterSettings.getMaxDegree();
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element is a test class.
	 */
	public boolean isElementMatchingIsTestCodeFilter(KnowledgeElement element) {
		return filterSettings.isTestCodeShown() || !element.getSummary().startsWith("Test");
	}

	/**
	 * @return {@link FilterSettings} object (=filter criteria) that the filtering
	 *         manager uses.
	 */
	public FilterSettings getFilterSettings() {
		return this.filterSettings;
	}

	/**
	 * @param filterSettings
	 *            {@link FilterSettings} object (=filter criteria) that the
	 *            filtering manager uses.
	 */
	public void setFilterSettings(FilterSettings filterSettings) {
		this.filterSettings = filterSettings;
	}

	/**
	 * @return {@link ApplicationUser} who performs filtering.
	 */
	public ApplicationUser getUser() {
		return user;
	}

	/**
	 * @param user
	 *            {@link ApplicationUser} object who performs filtering. The user
	 *            needs to have the rights to query the database.
	 */
	public void setUser(ApplicationUser user) {
		this.user = user;
	}
}
