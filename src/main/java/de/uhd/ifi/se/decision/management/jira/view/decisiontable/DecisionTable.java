package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jgrapht.Graph;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
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
// TODO Improve JavaDoc (not only parameter names should be given but a short
// explanation)
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
	 * @param filterSettings
	 *            filter criteria such as selected knowledge element and maximal
	 *            link distance in the {@link KnowledgeGraph}.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 */
	public void setIssues(FilterSettings filterSettings, ApplicationUser user) {
		issues = new ArrayList<>();

		FilteringManager filterManager = new FilteringManager(user, filterSettings);
		Graph<KnowledgeElement, Link> subgraph = filterManager.getSubgraphMatchingFilterSettings();
		for (KnowledgeElement element : subgraph.vertexSet()) {
			if (element.getType() == KnowledgeType.ISSUE) {
				issues.add(element);
			}
		}
	}

	/**
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return all available criteria (e.g. quality attributes, non-functional
	 *         requirements) for a project.
	 */
	public List<Criterion> getDecisionTableCriteria(ApplicationUser user) {
		List<Criterion> criteria = new ArrayList<>();
		String query = ConfigPersistenceManager.getDecisionTableCriteriaQuery(persistenceManager.getProjectKey());
		JiraQueryHandler queryHandler = new JiraQueryHandler(user, persistenceManager.getProjectKey(), "?jql=" + query);
		for (Issue jiraIssue : queryHandler.getJiraIssuesFromQuery()) {
			criteria.add(new Criterion(new KnowledgeElement(jiraIssue)));
		}
		return criteria;
	}

	/**
	 * 
	 * @param id
	 * @param location
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
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
				getArguments(elem, decisionTableData);
			}
		}
	}

	public void getArguments(KnowledgeElement rootElement, Map<String, List<DecisionTableElement>> decisionTableData) {
		Set<Link> outgoingLinks = graph.outgoingEdgesOf(rootElement);

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