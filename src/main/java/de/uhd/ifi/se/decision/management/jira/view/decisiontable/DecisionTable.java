package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.traverse.DepthFirstIterator;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryHandler;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

@XmlRootElement(name = "decisiontable")
@XmlAccessorType(XmlAccessType.FIELD)
public class DecisionTable {

	private KnowledgeGraph graph;
	private KnowledgePersistenceManager persistenceManager;

	@XmlElement
	private List<KnowledgeElement> issues;

	@XmlElement
	private Map<String, List<DecisionTableElement>> decisionTableData;
	
	public DecisionTable(String projectKey) {
		this.graph = KnowledgeGraph.getOrCreate(projectKey);
		persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);
	}

	/**
	 * 
	 * @param key
	 */
	public void setIssues(String key, FilteringManager filterManager) {
		issues = new ArrayList<>();
		KnowledgeElement rootElement = persistenceManager.getJiraIssueManager().getKnowledgeElement(key);
		
		AsSubgraph<KnowledgeElement, Link> subGraph = filterManager
				.getSubgraphMatchingFilterSettings(rootElement, filterManager.getFilterSettings().getLinkDistance());
		Iterator<KnowledgeElement> iterator = new DepthFirstIterator<>(subGraph, rootElement);
		while (iterator.hasNext()) {
			KnowledgeElement elem = iterator.next();
			if (elem.getType() == KnowledgeType.ISSUE) {
				issues.add(elem);
			}
		}
	}

	/**
	 * 
	 * @param user
	 * @param map
	 * @return
	 */
	public List<Criterion> getDecisionTableCriteria(ApplicationUser user) {
		List<Criterion> criteria = new ArrayList<>();
		String query = ConfigPersistenceManager.getDecisionTableCriteriaQuery(persistenceManager.getProjectKey());
		JiraQueryHandler queryHandler = new JiraQueryHandler(user, persistenceManager.getProjectKey(), "?jql=" + query);
		for (Issue i : queryHandler.getJiraIssuesFromQuery()) {
			criteria.add(new Criterion(new KnowledgeElement(i)));
		}
		return criteria;
	}

	/**
	 * 
	 * @param id
	 * @param location
	 * @param user
	 */
	public void setDecisionTableForIssue(long id, String location, ApplicationUser user) {
		KnowledgeElement rootElement = persistenceManager.getKnowledgeElement(id, location);
		Set<Link> outgoingLinks = this.graph.outgoingEdgesOf(rootElement);
		decisionTableData = new HashMap<>();
		decisionTableData.put("alternatives", new ArrayList<DecisionTableElement>());
		decisionTableData.put("criteria", new ArrayList<DecisionTableElement>());
		for (Link currentLink : outgoingLinks) {
			KnowledgeElement elem = currentLink.getTarget();
			if (elem.getType() == KnowledgeType.ALTERNATIVE || elem.getType() == KnowledgeType.DECISION) {
				decisionTableData.get("alternatives").add(new Alternative(elem));
				getArguments(elem.getId(), decisionTableData, location);
			}
		}
	}

	/**
	 * 
	 * @param id
	 * @param alternative
	 * @param criteria
	 * @param location
	 */
	public void getArguments(long id, Map<String, List<DecisionTableElement>> decisionTableData, String location) {
		KnowledgeElement rootElement = persistenceManager.getKnowledgeElement(id, location);
		Set<Link> outgoingLinks = this.graph.outgoingEdgesOf(rootElement);

		for (Link currentLink : outgoingLinks) {
			KnowledgeElement elem = currentLink.getTarget();
			if (KnowledgeType.replaceProAndConWithArgument(elem.getType()) == KnowledgeType.ARGUMENT) {
				Alternative alternative = (Alternative) decisionTableData.get("alternatives")
						.get(decisionTableData.get("alternatives").size() - 1);
				Argument argument = new Argument(elem);
				getArgumentCriteria(argument, decisionTableData.get("criteria"));
				alternative.addArgument(argument);
			}
		}
	}

	public void getArgumentCriteria(Argument argument, List<DecisionTableElement> criteria) {
		KnowledgeElement rootElement = persistenceManager.getKnowledgeElement(argument.getId(),
				argument.getDocumentationLocation());
		Set<Link> outgoingLinks = this.graph.outgoingEdgesOf(rootElement);
		
		for (Link currentLink : outgoingLinks) {
			KnowledgeElement elem = currentLink.getTarget();
			if (elem.getType() == KnowledgeType.OTHER) {
				argument.setCriterion(elem);
				if (!criteria.contains(new Criterion(elem))) {
					criteria.add(new Criterion(elem));
				}
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<KnowledgeElement> getIssues() {
		return this.issues;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, List<DecisionTableElement>> getDecisionTableData() {
		return this.decisionTableData;
	}
}