package de.uhd.ifi.se.decision.management.jira.filtering;


import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Extracats the Element search Items from the JQL and SearchString
 * TODO get Types and Date Early and Latest in spezific datatypes after Constructor
 */
public class FilterExtractor {
	private String projectKey;
	private ApplicationUser user;
	private String filterString;

	private List<DecisionKnowledgeElement> decisionKnowledgeElements;

	public FilterExtractor(String projectKey , ApplicationUser user, String filterString) {
		if(filterString == null){
			this.filterString = "";
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
			this.decisionKnowledgeElements =  filter.getAllElementsMatchingQuery();
		}
		if(this.decisionKnowledgeElements == null){
			this.decisionKnowledgeElements = new ArrayList<>();
		}
	}

	public FilterExtractor(String projectKey, ApplicationUser user,String searchTerm, String issueTypes, long createdEarliest, long createdLatest){
		this.projectKey = projectKey;
		this.user = user;
		GraphFiltering filter;
		filter = new GraphFiltering(projectKey, searchTerm, user, false);
		filter.produceResultsWithAdditionalFilters(issueTypes, createdEarliest, createdLatest);
		this.decisionKnowledgeElements = filter.getAllElementsMatchingQuery();
	}

	/**
	 * if no filter
	 * @return  all DecisionKnowledgeElements of the Project
	 *
	 * if filtered
	 * @return  filtered DecisionKnowledgeElements
	 */
	public List<DecisionKnowledgeElement> getFilteredDecisions(){
		return this.decisionKnowledgeElements;
	}
}
