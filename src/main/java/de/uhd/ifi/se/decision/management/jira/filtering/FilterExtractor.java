package de.uhd.ifi.se.decision.management.jira.filtering;


import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImplFiltered;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Extracats the Element search Items from the JQL and SearchString
 */
public class FilterExtractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(FilterExtractor.class);
	private ApplicationUser user;
	private FilterSettings filterData;
	private List<DecisionKnowledgeElement> decisionKnowledgeElements;

	public FilterExtractor(String projectKey, ApplicationUser user, String filterString) {
		if (projectKey == null || projectKey.equals("")) {
			LOGGER.error("ProjectKey is null or empty");
			return;
		}
		if (filterString == null) {
			LOGGER.error("FilterString is null");
			return;
		}
		if (user == null) {
			LOGGER.error("User is null");
			return;
		}
		this.filterData = new FilterSettingsImpl(projectKey, filterString);
		this.user = user;
		GraphFiltering filter;
		if (!(filterString.matches("\\?jql=(.)+")) || (filterString.matches("\\?filter=(.)+"))) {
			filterData.setSearchString("asdf§filter=-4");
		}
		filter = new GraphFiltering(filterData, user, false);
		filter.getJiraIssuesFromQuery(filterData.getSearchString());
		this.decisionKnowledgeElements = filter.getAllElementsMatchingQuery();
		if (this.decisionKnowledgeElements == null) {
			this.decisionKnowledgeElements = new ArrayList<>();
		}
	}

	public FilterExtractor(ApplicationUser user, FilterSettings filterData) {
		if (filterData == null) {
			LOGGER.error("Filter data is null");
			return;
		}
		if (filterData.getSearchString() == null) {
			LOGGER.error("FilterString is null");
			return;
		}
		if (user == null) {
			LOGGER.error("User is null");
			return;
		}
		this.user = user;
		this.filterData = filterData;
		GraphFiltering filter = new GraphFiltering(filterData, user, false);
		filter.produceResultsWithAdditionalFilters();
		this.decisionKnowledgeElements = filter.getAllElementsMatchingQuery();
	}

	/**
	 * if no filter
	 *
	 * @return filtered DecisionKnowledgeElements
	 */
	public List<DecisionKnowledgeElement> getFilteredDecisions() {
		return this.decisionKnowledgeElements;
	}


	/**
	 * Treant and Treeview filter
	 */

	private static List<DecisionKnowledgeElement> getElementsInGraph(ApplicationUser user, FilterSettings filterData, String elementKey) {
		Graph graph;
		if ((filterData.getSearchString().matches("\\?jql=(.)+")) || (filterData.getSearchString().matches("\\?filter=(.)+"))) {
			GraphFiltering filter = new GraphFiltering(filterData, user, false);
			filter.getJiraIssuesFromQuery(filterData.getSearchString());
			graph = new GraphImplFiltered(filterData.getProjectKey(), elementKey, filter);
		} else {
			graph = new GraphImpl(filterData.getProjectKey(), elementKey);
		}
		return graph.getAllElements();
	}

	public List<List<DecisionKnowledgeElement>> getGraphsMatchingQuery(String linkedQuery) {
		// Default filter for adjecent Elements
		String linkedQueryNotEmpty = linkedQuery;
		if ("".equals(linkedQueryNotEmpty)) {
			linkedQueryNotEmpty = "?filter=allissues";
		}
		List<DecisionKnowledgeElement> tempQueryResult = new FilterExtractor(filterData.getProjectKey(), user, filterData.getSearchString()).getFilteredDecisions();
		List<DecisionKnowledgeElement> addedElements = new ArrayList<DecisionKnowledgeElement>();
		List<List<DecisionKnowledgeElement>> elementsQueryLinked = new ArrayList<List<DecisionKnowledgeElement>>();

		// now iti over query result
		for (DecisionKnowledgeElement current : tempQueryResult) {
			// check if in addedElements list
			if (!addedElements.contains(current)) {
				// if not get the connected tree
				String currentElementKey = current.getKey();
				filterData.setSearchString(linkedQueryNotEmpty);
				List<DecisionKnowledgeElement> filteredElements = getElementsInGraph(user, filterData, currentElementKey);
				// add each element to the list
				addedElements.addAll(filteredElements);
				// add list to the big list
				elementsQueryLinked.add(filteredElements);
			}
		}
		return elementsQueryLinked;
	}
}
