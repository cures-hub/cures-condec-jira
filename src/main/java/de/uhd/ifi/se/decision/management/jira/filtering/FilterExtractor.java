package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.JiraQueryHandlerImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImplFiltered;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
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
		if (!(filterString.matches("\\?jql=(.)+")) || (filterString.matches("\\?filter=(.)+"))) {
			filterSettings.setSearchString("?filter=-4");
		}
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
	public List<List<DecisionKnowledgeElement>> getGraphsMatchingQuery(String linkedQuery) {
		// Default filter for adjecent Elements
		String linkedQueryNotEmpty = linkedQuery;
		if ("".equals(linkedQueryNotEmpty)) {
			linkedQueryNotEmpty = "?filter=allissues";
		}
		List<DecisionKnowledgeElement> tempQueryResult = new FilterExtractor(filterSettings.getProjectKey(), user,
				filterSettings.getSearchString()).getAllElementsMatchingQuery();
		List<DecisionKnowledgeElement> addedElements = new ArrayList<DecisionKnowledgeElement>();
		List<List<DecisionKnowledgeElement>> elementsQueryLinked = new ArrayList<List<DecisionKnowledgeElement>>();

		// now iti over query result
		for (DecisionKnowledgeElement current : tempQueryResult) {
			// check if in addedElements list
			if (!addedElements.contains(current)) {
				// if not get the connected tree
				String currentElementKey = current.getKey();
				filterSettings.setSearchString(linkedQueryNotEmpty);
				List<DecisionKnowledgeElement> filteredElements = this.getElementsInGraph(user, filterSettings,
						currentElementKey);
				// add each element to the list
				addedElements.addAll(filteredElements);
				// add list to the big list
				elementsQueryLinked.add(filteredElements);
			}
		}
		return elementsQueryLinked;
	}

	/**
	 * Used for the export of decision knowledge
	 */
	private List<DecisionKnowledgeElement> getElementsInGraph(ApplicationUser user, FilterSettings filterData,
			String elementKey) {
		Graph graph;
		if ((filterData.getSearchString().matches("\\?jql=(.)+"))
				|| (filterData.getSearchString().matches("\\?filter=(.)+"))) {
			graph = new GraphImplFiltered(filterData.getProjectKey(), elementKey, this);
		} else {
			graph = new GraphImpl(filterData.getProjectKey(), elementKey);
		}
		return graph.getAllElements();
	}

	//Problem Filtered Issues from sideFilter will be filterd again
	//In the end there are only 2 Issues left that are not matching with the
	//location so everything is collapsed
	public List<DecisionKnowledgeElement> getAllElementsMatchingQuery() {
		List<Issue> jiraIssues = queryHandler.getJiraIssuesFromQuery();
		List<DecisionKnowledgeElement> results = new ArrayList<DecisionKnowledgeElement>();
		if (jiraIssues == null) {
			return results;
		}
		//Search in every Jira issue for some more Decision Knowledge Elements and if there are some add them
		for (Issue currentIssue : jiraIssues) {
			//Add all Matching Elements from Query as a DecisionKnowledgeElement
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

	private boolean checkIfJiraTextMatchesFilter(DecisionKnowledgeElement element) {
		if (filterSettings.getCreatedEarliest() > 0
				&& (element).getCreated().getTime() < filterSettings.getCreatedEarliest()) {
			return false;
		}
		if (filterSettings.getCreatedLatest() > 0
				&& (element).getCreated().getTime() > filterSettings.getCreatedLatest()) {
			return false;
		}

		if (element.getType().equals(KnowledgeType.PRO) || element.getType().equals(KnowledgeType.CON)) {
			if (!filterSettings.getNamesOfSelectedJiraIssueTypes().contains(KnowledgeType.ARGUMENT.toString())) {
				return false;
			}
		} else if (!filterSettings.getNamesOfSelectedJiraIssueTypes().contains(element.getTypeAsString())) {
			return false;
		}

		return true;
	}

	public FilterSettings getFilterSettings() {
		return this.filterSettings;
	}

	public JiraQueryHandler getQueryHandler() {
		return queryHandler;
	}
}
