package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.AsSubgraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
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

	public FilteringManager(FilterSettings filterSettings) {
		this(null, filterSettings);
	}

	public FilteringManager(ApplicationUser user, FilterSettings filterSettings) {
		this.user = user;
		this.filterSettings = filterSettings;
		if (filterSettings != null) {
			this.graph = KnowledgeGraph.getOrCreate(filterSettings.getProjectKey());
		}
	}

	public FilteringManager(String projectKey, ApplicationUser user, String query) {
		this(user, new FilterSettings(projectKey, query, user));
	}

	/**
	 * @return all knowledge elements that match the {@link FilterSettings}.
	 */
	public Set<KnowledgeElement> getElementsMatchingFilterSettings() {
		if (filterSettings == null || filterSettings.getProjectKey() == null || graph == null) {
			LOGGER.error("FilteringManager misses important attributes.");
			return new HashSet<>();
		}
		Set<KnowledgeElement> elements = getElementsInLinkDistanceFromSelectedElementOrEntireVertexSet();
		elements = filterElements(elements);
		if (filterSettings.getSelectedElement() != null) {
			elements.add(filterSettings.getSelectedElement());
		}
		return elements;
	}

	/**
	 * @return subgraph of the {@link KnowledgeGraph} that matches the
	 *         {@link FilterSettings}.
	 */
	public KnowledgeGraph getSubgraphMatchingFilterSettings() {
		if (filterSettings == null || filterSettings.getProjectKey() == null || graph == null) {
			LOGGER.error("FilteringManager misses important attributes.");
			return null;
		}

		Set<KnowledgeElement> elements = getElementsMatchingFilterSettings();
		KnowledgeGraph subgraph = KnowledgeGraph.copy(new AsSubgraph<>(graph, elements));
		removeLinksWithTypesNotInFilterSettings(subgraph);

		if (filterSettings.createTransitiveLinks()) {
			addTransitiveLinksToSubgraph(subgraph);
		}
		return subgraph;
	}

	private KnowledgeGraph addTransitiveLinksToSubgraph(KnowledgeGraph subgraph) {
		KnowledgeGraph temporaryGraph = new KnowledgeGraph();
		int id = -65536;
		Set<KnowledgeElement> elementsNotMatchingFilterSettings = getElementsNotMatchingFilterSettings();
		for (KnowledgeElement element : elementsNotMatchingFilterSettings) {
			Set<KnowledgeElement> linkedElements = graph.getLinkedElements(element);
			if (temporaryGraph.isElementInGraph(element)) {
				linkedElements.addAll(temporaryGraph.getLinkedElements(element));
			}
			for (KnowledgeElement sourceElement : linkedElements) {
				for (KnowledgeElement targetElement : linkedElements) {
					if (sourceElement.equals(targetElement) || temporaryGraph.containsEdge(sourceElement, targetElement)
							|| temporaryGraph.containsEdge(targetElement, sourceElement) || graph.containsEdge(sourceElement, 
							targetElement) || graph.containsEdge(targetElement, sourceElement)) {
						continue;
					}
					if (sourceElement.getType() != KnowledgeType.OTHER && targetElement.getType() != KnowledgeType.OTHER) {
						if (!graph.getLinkedSourceElements(element).contains(sourceElement)) {
							continue;
						}
						if (!graph.getLinkedTargetElements(element).contains(targetElement)) {
							continue;
						}
					}
					if (sourceElement.getType() == KnowledgeType.OTHER && targetElement.getType() != KnowledgeType.OTHER) {
						continue;
					}
					Link transitiveLink = new Link(sourceElement, targetElement, LinkType.TRANSITIVE);
					transitiveLink.setId(id++);
					subgraph.addEdge(transitiveLink);
				}
			}
		}
		return subgraph;
	}

	/**
	 * @return all knowledge elements that do not match the {@link FilterSettings}.
	 */
	public Set<KnowledgeElement> getElementsNotMatchingFilterSettings() {
		Set<KnowledgeElement> elementsInLinkDistanceOrEntireVertexSet = getElementsInLinkDistanceFromSelectedElementOrEntireVertexSet();
		Set<KnowledgeElement> elementsMatchingFilterSettings = getElementsMatchingFilterSettings();
		elementsInLinkDistanceOrEntireVertexSet.removeAll(elementsMatchingFilterSettings);
		return elementsInLinkDistanceOrEntireVertexSet;
	}

	private Set<KnowledgeElement> getElementsInLinkDistanceFromSelectedElementOrEntireVertexSet() {
		if (filterSettings.getSelectedElement() != null) {
			graph.addVertex(filterSettings.getSelectedElement());
			return getElementsInLinkDistance(filterSettings.getSelectedElement());
		}
		return new HashSet<>(graph.vertexSet());
	}

	private Set<KnowledgeElement> getElementsInLinkDistance(KnowledgeElement element) {
		int linkDistance = filterSettings.getLinkDistance();
		return new HashSet<>(element.getLinkedElements(linkDistance));
	}

	/**
	 * Removes those edges from the graph that have types that are not in the
	 * {@link FilterSettings#getLinkTypes()}.
	 * 
	 * @param subgraph
	 *            {@link KnowledgeGraph} object.
	 * @return subgraph of the {@link KnowledgeGraph} which might have fewer edges
	 *         than before.
	 */
	private KnowledgeGraph removeLinksWithTypesNotInFilterSettings(KnowledgeGraph subgraph) {
		if (filterSettings.getLinkTypes().size() < DecisionKnowledgeProject.getNamesOfLinkTypes().size()) {
			Set<Link> linksNotMatchingFilterSettings = getLinksNotMatchingFilterSettings(subgraph.edgeSet());
			subgraph.removeAllEdges(linksNotMatchingFilterSettings);
		}
		return subgraph;
	}

	private Set<Link> getLinksNotMatchingFilterSettings(Set<Link> links) {
		Set<Link> linksNotMatchingFilterSettings = new HashSet<>();
		for (Link link : links) {
			if (filterSettings.getLinkTypes().parallelStream()
					.noneMatch(selectedType -> selectedType.toLowerCase().startsWith(link.getTypeAsString()))) {
				linksNotMatchingFilterSettings.add(link);
			}
		}
		return linksNotMatchingFilterSettings;
	}

	private Set<KnowledgeElement> filterElements(Set<KnowledgeElement> elements) {
		Set<KnowledgeElement> filteredElements = new HashSet<>();
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
	 *         {@link FilterSettings}.
	 */
	public boolean isElementMatchingFilterSettings(KnowledgeElement element) {
		if (!isElementMatchingKnowledgeTypeFilter(element)) {
			return false;
		}
		if (!isElementMatchingTimeFilter(element)) {
			return false;
		}
		if (!isElementMatchingStatusFilter(element) && !isElementMatchingDocumentationIncompletenessFilter(element)) {
			return false;
		}
		if (!isElementMatchingDocumentationLocationFilter(element)) {
			return false;
		}
		if (!isElementMatchingDecisionGroupFilter(element)) {
			return false;
		}
		if (!isElementMatchingIsTestCodeFilter(element)) {
			return false;
		}
		if (!isElementMatchingDegreeFilter(element)) {
			return false;
		}
		return isElementMatchingSubStringFilter(element);
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element is documented in one of the given
	 *         {@link DocumentationLocation}s in the {@link FilterSettings}.
	 */
	public boolean isElementMatchingDocumentationLocationFilter(KnowledgeElement element) {
		return filterSettings.getDocumentationLocations().contains(element.getDocumentationLocation());
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element's status equals one of the given
	 *         {@link KnowledgeStatus} in the {@link FilterSettings}.
	 */
	public boolean isElementMatchingStatusFilter(KnowledgeElement element) {
		return filterSettings.getStatus().contains(element.getStatus());
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element is created or updated in the given time frame in
	 *         the {@link FilterSettings}. See
	 *         {@link KnowledgeElement#getCreationDate()} and
	 *         {@link KnowledgeElement#getUpdatingDate()}.
	 */
	public boolean isElementMatchingTimeFilter(KnowledgeElement element) {
		boolean isMatchingTimeFilter = true;
		if (filterSettings.getStartDate() > 0) {
			isMatchingTimeFilter = element.getUpdatingDate().getTime() >= filterSettings.getStartDate();
		}
		if (filterSettings.getEndDate() > 0) {
			isMatchingTimeFilter = isMatchingTimeFilter
					&& element.getCreationDate().getTime() <= filterSettings.getEndDate() + 86400000;
		}
		return isMatchingTimeFilter;
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element's description, summary, or key contains the given
	 *         substring in the {@link FilterSettings}.
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
	 *         {@link KnowledgeType}s in the {@link FilterSettings}.
	 */
	public boolean isElementMatchingKnowledgeTypeFilter(KnowledgeElement element) {
		String type = element.getType().replaceProAndConWithArgument().toString();
		if (element.getType() == KnowledgeType.OTHER) {
			if (filterSettings.isOnlyDecisionKnowledgeShown()) {
				return false;
			}
			type = element.getTypeAsString();
		}
		return filterSettings.getKnowledgeTypes().contains(type);
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element's groups are equal to the given groups in the
	 *         {@link FilterSettings}.
	 */
	public boolean isElementMatchingDecisionGroupFilter(KnowledgeElement element) {
		List<String> selectedGroups = filterSettings.getDecisionGroups();
		if (selectedGroups.isEmpty()) {
			return true;
		}

		List<String> groups = element.getDecisionGroups();

		int matches = 0;
		for (String group : selectedGroups) {
			if (groups.contains(group)) {
				matches++;
			}
		}
		return matches == selectedGroups.size();
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element's degree (i.e. number of links) is in between
	 *         minDegree and maxDegree in the {@link FilterSettings}.
	 */
	public boolean isElementMatchingDegreeFilter(KnowledgeElement element) {
		if (filterSettings.getMinDegree() > 0) {
			int degree = element.getLinks().size();
			return degree >= filterSettings.getMinDegree() && degree <= filterSettings.getMaxDegree();
		}
		return true;
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return true if the element is a test class.
	 */
	public boolean isElementMatchingIsTestCodeFilter(KnowledgeElement element) {
		// TODO Make code class recognition more explicit
		if (element.getDocumentationLocation() != DocumentationLocation.CODE) {
			return true;
		}
		if (!element.getSummary().contains(".java")) {
			return true;
		}
		return filterSettings.isTestCodeShown() || !element.getSummary().startsWith("Test");
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object.
	 * @return True if the element is incompletely documented according to the
	 *         {@see CompletenessCheck} and incomplete knowledge elements should be
	 *         shown. False otherwise.
	 */
	public boolean isElementMatchingDocumentationIncompletenessFilter(KnowledgeElement element) {
		return filterSettings.isIncompleteKnowledgeShown() && element.isIncomplete();
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
