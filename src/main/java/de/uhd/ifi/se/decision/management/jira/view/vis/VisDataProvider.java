package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterExtractor;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;

public class VisDataProvider {

	private String projectKey;
	private ApplicationUser user;
	private VisGraph graph;
	private VisTimeLine timeLine;
	private FilterExtractor filterExtractor;
	private List<DecisionKnowledgeElement> decisionKnowledgeElements;

	public VisDataProvider(String projectKey, ApplicationUser user) {
		this.projectKey = projectKey;
		this.user = user;
		this.filterExtractor = new FilterExtractor(projectKey, user, "");
		this.timeLine = new VisTimeLine(projectKey);
		decisionKnowledgeElements = filterExtractor.getAllElementsMatchingCompareFilter();
		graph = new VisGraph(decisionKnowledgeElements, false);
	}

	public VisDataProvider(String projectKey, String elementKey, boolean isHyperlinked, String query,
			ApplicationUser user) {
		this.projectKey = projectKey;
		this.user = user;
		this.filterExtractor = new FilterExtractor(projectKey, user, query);
		decisionKnowledgeElements = filterExtractor.getAllElementsMatchingQuery();
		graph = new VisGraph(projectKey, elementKey, decisionKnowledgeElements, isHyperlinked);
	}

	public VisDataProvider(String elementKey, boolean isHyperlinked, ApplicationUser user, FilterSettings filterSettings) {
		this.projectKey = filterSettings.getProjectKey();
		this.user = user;
		this.filterExtractor = new FilterExtractor(user, filterSettings);
		decisionKnowledgeElements = filterExtractor.getAllElementsMatchingQuery();
		graph = new VisGraph(projectKey, elementKey, decisionKnowledgeElements, isHyperlinked);
	}

	public VisGraph getVisGraph() {
		return this.graph;
	}

	public VisGraph getVisGraphTimeFilterd(FilterSettings filterSettings){
		FilterExtractor extractor = new FilterExtractor(this.user, filterSettings);
		List<DecisionKnowledgeElement> elements = extractor.getAllElementsMatchingCompareFilter();
		VisGraph graph = new VisGraph(elements,false);
		return graph;
	}

	public VisTimeLine getTimeLine() {
		if (timeLine == null) {
			this.timeLine = new VisTimeLine(projectKey);
		}
		return this.timeLine;
	}

	public VisTimeLine getTimeLineFilterd(FilterSettings filterSettings){
		FilterExtractor extractor = new FilterExtractor(this.user, filterSettings);
		List<DecisionKnowledgeElement> elements = extractor.getAllElementsMatchingCompareFilter();
		this.timeLine= new VisTimeLine(elements);
		return this.timeLine;
	}


}
