package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterExtractor;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterExtractorImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;

public class VisDataProvider {

	private String projectKey;
	private ApplicationUser user;
	private VisGraph graph;
	private VisTimeLine timeLine;
	private FilterExtractor filterExtractor;
	private List<DecisionKnowledgeElement> decisionKnowledgeElements;

	// Evolution Views
	public VisDataProvider(ApplicationUser user, FilterSettings filterSettings) {
		if (user == null || filterSettings == null) {
			return;
		}
		this.projectKey = filterSettings.getProjectKey();
		this.user = user;
		FilterExtractor filterExtractor = new FilterExtractorImpl(this.user, filterSettings);
		List<DecisionKnowledgeElement> decisionKnowledgeElements = filterExtractor
				.getAllElementsMatchingCompareFilter();
		graph = new VisGraph(decisionKnowledgeElements, projectKey);
		this.timeLine = new VisTimeLine(decisionKnowledgeElements);
	}

	public VisDataProvider(ApplicationUser user, FilterSettings filterSettings, List<DecisionKnowledgeElement> allDecisions) {
		if (user == null || filterSettings == null) {
			return;
		}
		this.projectKey = filterSettings.getProjectKey();
		this.user = user;
		FilterExtractor filterExtractor = new FilterExtractor(this.user, filterSettings);
		List<DecisionKnowledgeElement> decisionKnowledgeElements = filterExtractor.getElementsLinkTypeFilterMatches(allDecisions);
		graph = new VisGraph(decisionKnowledgeElements, projectKey);
	}

	// JQL Filter and JIRA Filter
	public VisDataProvider(String projectKey, String elementKey, String query, ApplicationUser user) {
		this.projectKey = projectKey;
		this.user = user;
		this.filterExtractor = new FilterExtractorImpl(projectKey, user, query);
		decisionKnowledgeElements = filterExtractor.getAllElementsMatchingQuery();
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getOrCreate(projectKey).getDefaultPersistenceManager();
		DecisionKnowledgeElement rootElement = persistenceManager.getDecisionKnowledgeElement(elementKey);
		graph = new VisGraph(rootElement, decisionKnowledgeElements);
	}

	// Filter Issue Module
	public VisDataProvider(String elementKey, ApplicationUser user, FilterSettings filterSettings) {
		this.projectKey = filterSettings.getProjectKey();
		this.user = user;
		this.filterExtractor = new FilterExtractorImpl(user, filterSettings);
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getOrCreate(projectKey).getDefaultPersistenceManager();
		DecisionKnowledgeElement rootElement = persistenceManager.getDecisionKnowledgeElement(elementKey);
		decisionKnowledgeElements = filterExtractor.getAllElementsMatchingCompareFilter();
		graph = new VisGraph(rootElement, decisionKnowledgeElements);
	}

	public VisGraph getVisGraph() {
		return this.graph;
	}

	public VisTimeLine getTimeLine() {
		if (timeLine == null) {
			this.timeLine = new VisTimeLine(projectKey);
		}
		return this.timeLine;
	}
}
