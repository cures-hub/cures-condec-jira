package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImplFiltered;

public class FilteringManager {

	public static List<DecisionKnowledgeElement> getElementsMatchingQuery(ApplicationUser user, String projectKey,
			String query) {
		GraphFiltering filter = new GraphFiltering(projectKey, query, user);
		filter.produceResultsFromQuery();
		return filter.getAllElementsMatchingQuery();
	}

	public static List<DecisionKnowledgeElement> getElementsInGraph(ApplicationUser user, String projectKey,
			String query, String elementKey) {
		Graph graph;
		if ((query.matches("\\?jql=(.)+")) || (query.matches("\\?filter=(.)+"))) {
			GraphFiltering filter = new GraphFiltering(projectKey, query, user);
			filter.produceResultsFromQuery();
			graph = new GraphImplFiltered(projectKey, elementKey, filter);
		} else {
			graph = new GraphImpl(projectKey, elementKey);
		}
		return graph.getAllElements();
	}

	public static List<List<DecisionKnowledgeElement>> getGraphsMatchingQuery(ApplicationUser user, String projectKey,
			String query, String linkedQuery) {
		//Default filter for adjecent Elements
		String linkedQueryNotEmpty=linkedQuery;
		if("".equals(linkedQueryNotEmpty)){
			linkedQueryNotEmpty="?filter=allissues";
		}
		List<DecisionKnowledgeElement> tempQueryResult = getElementsMatchingQuery(user, projectKey, query);
		List<DecisionKnowledgeElement> addedElements = new ArrayList<DecisionKnowledgeElement>();
		List<List<DecisionKnowledgeElement>> elementsQueryLinked = new ArrayList<List<DecisionKnowledgeElement>>();

		// now iti over query result
		for (DecisionKnowledgeElement current : tempQueryResult) {
			// check if in addedElements list
			if (!addedElements.contains(current)) {
				// if not get the connected tree
				String currentElementKey = current.getKey();
				List<DecisionKnowledgeElement> filteredElements = getElementsInGraph(user, projectKey, linkedQueryNotEmpty,
						currentElementKey);
				// add each element to the list
				addedElements.addAll(filteredElements);
				// add list to the big list
				elementsQueryLinked.add(filteredElements);
			}
		}
		return elementsQueryLinked;
	}

}
