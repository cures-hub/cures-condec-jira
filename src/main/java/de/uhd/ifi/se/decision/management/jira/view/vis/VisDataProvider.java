package de.uhd.ifi.se.decision.management.jira.view.vis;

import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterExtractor;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;

import java.util.List;

public class VisDataProvider {

	private VisGraph graph;
	private VisTimeLine timeLine;
	private FilterExtractor filterExtractor;
	private List<DecisionKnowledgeElement> decisionKnowledgeElements;


	public VisDataProvider(String projectKey){
		this.timeLine = new VisTimeLine(projectKey);
	}

	public VisDataProvider(String projectKey, String elementKey, boolean isHyperlinked, String query, ApplicationUser user) {
		this.filterExtractor = new FilterExtractor(projectKey, user, query);
		decisionKnowledgeElements = filterExtractor.getFilteredDecisions();
		graph = new VisGraph(projectKey, elementKey, decisionKnowledgeElements, isHyperlinked);

	}

	public VisDataProvider(String projectKey, String elementKey, boolean isHyperlinked, String searchTerm, ApplicationUser user,
	                       String issueTypes, long createdEarliest, long createdLatest, String documentationLocation) {
		this.filterExtractor = new FilterExtractor(projectKey, user, searchTerm, issueTypes, createdEarliest, createdLatest);
		decisionKnowledgeElements = filterExtractor.getFilteredDecisions();
		graph = new VisGraph(projectKey,elementKey, decisionKnowledgeElements, isHyperlinked);

	}

	public VisGraph getVisGraph(){
		return  this.graph;
	}

	public VisTimeLine getTimeLine(){
		return this.timeLine;
	}
}
