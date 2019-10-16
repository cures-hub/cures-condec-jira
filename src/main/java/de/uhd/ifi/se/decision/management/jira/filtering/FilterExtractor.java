package de.uhd.ifi.se.decision.management.jira.filtering;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.traverse.BreadthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.JiraQueryHandlerImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.Node;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeGraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionStatusManager;
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
				AbstractPersistenceManager persistenceManager = AbstractPersistenceManager.getPersistenceManager(
						this.filterSettings.getProjectKey(), current.getDocumentationLocation().getIdentifier());
				DecisionKnowledgeElement element = persistenceManager.getDecisionKnowledgeElement(currentElementKey);
				List<DecisionKnowledgeElement> filteredElements = getElementsInGraph(element);
				// add each element to the list
				addedElements.addAll(filteredElements);
				// add list to the big list
				elementsQueryLinked.add(filteredElements);
			}
		}
		return elementsQueryLinked;
	}

	private List<DecisionKnowledgeElement> getElementsInGraph(DecisionKnowledgeElement element) {
		KnowledgeGraph graph = new KnowledgeGraphImpl(filterSettings.getProjectKey());
		List<DecisionKnowledgeElement> elements = new ArrayList<>();
		if(!graph.vertexSet().contains(element)){
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
						&& checkIfElementMatchesTimeFilter(currentElement)) {
					results.add(currentElement);
				}
			}
		}
		return results;
	}

	public List<DecisionKnowledgeElement> getAllElementsMatchingCompareFilter() {
		if (filterSettings == null || filterSettings.getProjectKey() == null) {
			return new ArrayList<>();
		}
		List<DecisionKnowledgeElement> elements = getElementsInProject();
		return filterElements(elements);
	}

	// Get decision knowledge elements from the selected strategy and the sentences
	private List getElementsInProject() {
		AbstractPersistenceManager strategy = AbstractPersistenceManager
				.getDefaultPersistenceStrategy(filterSettings.getProjectKey());
		List<DecisionKnowledgeElement> elements = strategy.getDecisionKnowledgeElements();
		AbstractPersistenceManager jiraIssueCommentPersistenceManager = new JiraIssueTextPersistenceManager(
				filterSettings.getProjectKey());
		elements.addAll(jiraIssueCommentPersistenceManager.getDecisionKnowledgeElements());
		return elements;
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
