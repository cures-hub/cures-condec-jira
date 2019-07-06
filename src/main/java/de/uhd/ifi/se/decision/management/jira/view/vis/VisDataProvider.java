package de.uhd.ifi.se.decision.management.jira.view.vis;

import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterExtractor;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.FilterSettings;

import java.util.List;

public class VisDataProvider {

	private String projectKey;
	private VisGraph graph;
	private VisTimeLine timeLine;
	private FilterExtractor filterExtractor;
	private List<DecisionKnowledgeElement> decisionKnowledgeElements;


	public VisDataProvider(String projectKey){
		this.projectKey = projectKey;
		this.timeLine = new VisTimeLine(projectKey);
	}

	public VisDataProvider(String projectKey, String elementKey, boolean isHyperlinked, String query, ApplicationUser user) {
		this.projectKey = projectKey;
		this.filterExtractor = new FilterExtractor(projectKey, user, query);
		decisionKnowledgeElements = filterExtractor.getFilteredDecisions();
		graph = new VisGraph(projectKey, elementKey, decisionKnowledgeElements, isHyperlinked);

	}

	public VisDataProvider(String elementKey, boolean isHyperlinked, ApplicationUser user, FilterSettings filterData) {
		this.projectKey = filterData.getProjectKey();
		this.filterExtractor = new FilterExtractor(user,filterData);
		decisionKnowledgeElements = filterExtractor.getFilteredDecisions();
		graph = new VisGraph(projectKey,elementKey, decisionKnowledgeElements, isHyperlinked);

	}

	public VisGraph getVisGraph(){
		return  this.graph;
	}

	public VisTimeLine getTimeLine(){
		if(timeLine == null){
			this.timeLine = new VisTimeLine(projectKey);
		}
		return this.timeLine;
	}
}
