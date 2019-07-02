package de.uhd.ifi.se.decision.management.jira.filtering;


import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.FilterData;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
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
	private String projectKey;
	private ApplicationUser user;
	private String filterString;

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
		this.filterString = filterString;
		this.projectKey = projectKey;
		this.user = user;
		GraphFiltering filter;
		if ((filterString.matches("\\?jql=(.)+")) || (filterString.matches("\\?filter=(.)+"))) {
			filter = new GraphFiltering(projectKey, filterString, user, false);
			filter.produceResultsFromQuery();
			this.decisionKnowledgeElements = filter.getAllElementsMatchingQuery();
		} else {
			filter = new GraphFiltering(projectKey, "asdf§filter=-4", user, false);
			filter.produceResultsFromQuery();
			this.decisionKnowledgeElements = filter.getAllElementsMatchingQuery();
		}
		if (this.decisionKnowledgeElements == null) {
			this.decisionKnowledgeElements = new ArrayList<>();
		}
	}

	public FilterExtractor(String projectKey, ApplicationUser user, FilterData filterData) {
		if (projectKey == null || projectKey.equals("")) {
			LOGGER.error("ProjectKey is null or empty");
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
		this.filterString = filterData.getSearchString();
		this.projectKey = projectKey;
		this.user = user;
		GraphFiltering filter = new GraphFiltering(projectKey, filterString, user, false);
		filter.produceResultsWithAdditionalFilters(filterData);
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

	private static List<DecisionKnowledgeElement> getElementsInGraph(ApplicationUser user, String projectKey, String query, String elementKey) {
		Graph graph;
		if ((query.matches("\\?jql=(.)+")) || (query.matches("\\?filter=(.)+"))) {
			GraphFiltering filter = new GraphFiltering(projectKey, query, user, false);
			filter.produceResultsFromQuery();
			graph = new GraphImplFiltered(projectKey, elementKey, filter);
		} else {
			graph = new GraphImpl(projectKey, elementKey);
		}
		return graph.getAllElements();
	}

	public List<List<DecisionKnowledgeElement>> getGraphsMatchingQuery(String linkedQuery) {
		// Default filter for adjecent Elements
		String linkedQueryNotEmpty = linkedQuery;
		if ("".equals(linkedQueryNotEmpty)) {
			linkedQueryNotEmpty = "?filter=allissues";
		}
		List<DecisionKnowledgeElement> tempQueryResult = new FilterExtractor(projectKey, user, filterString).getFilteredDecisions();
		List<DecisionKnowledgeElement> addedElements = new ArrayList<DecisionKnowledgeElement>();
		List<List<DecisionKnowledgeElement>> elementsQueryLinked = new ArrayList<List<DecisionKnowledgeElement>>();

		// now iti over query result
		for (DecisionKnowledgeElement current : tempQueryResult) {
			// check if in addedElements list
			if (!addedElements.contains(current)) {
				// if not get the connected tree
				String currentElementKey = current.getKey();
				List<DecisionKnowledgeElement> filteredElements = getElementsInGraph(user, projectKey, linkedQueryNotEmpty, currentElementKey);
				// add each element to the list
				addedElements.addAll(filteredElements);
				// add list to the big list
				elementsQueryLinked.add(filteredElements);
			}
		}
		return elementsQueryLinked;
	}
}
