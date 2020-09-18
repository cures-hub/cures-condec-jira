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
// explanation!)
public class DecisionTable {

	private KnowledgeGraph graph;
	private KnowledgePersistenceManager persistenceManager;

	@XmlElement
	private List<KnowledgeElement> issues;

	@XmlElement
	private Map<String, List<DecisionTableElement>> decisionTableData;

	public DecisionTable(String projectKey) {
		graph = KnowledgeGraph.getOrCreate(projectKey);
		persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);
		decisionTableData = new HashMap<>();
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
	 * @param rootElement
	 *            decision problem as a {@link KnowledgeElement} object.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 */
	public void setDecisionTableForIssue(KnowledgeElement rootElement, ApplicationUser user) {
		decisionTableData.put("alternatives", new ArrayList<DecisionTableElement>());
		decisionTableData.put("criteria", new ArrayList<DecisionTableElement>());

		// TODO Check link type. A decision that leads to a new decision problem should
		// not be shown as solution option for this derived decision problem.
		for (Link currentLink : graph.edgesOf(rootElement)) {
			KnowledgeElement oppositeElement = currentLink.getOppositeElement(rootElement);
			System.out.println(oppositeElement);
			if (oppositeElement.getType() == KnowledgeType.ALTERNATIVE
					|| oppositeElement.getType() == KnowledgeType.DECISION
					|| oppositeElement.getType() == KnowledgeType.SOLUTION) {
				decisionTableData.get("alternatives").add(new Alternative(oppositeElement));
				getArguments(oppositeElement);
			}
		}
	}

	/**
	 * @param solutionOption
	 *            either an alternative or decision as a {@link KnowledgeElement}
	 *            object.
	 */
	public void getArguments(KnowledgeElement solutionOption) {
		if (decisionTableData.get("alternatives") == null) {
			return;
		}
		int numberOfAlternatives = decisionTableData.get("alternatives").size();
		Set<Link> incomingLinks = graph.incomingEdgesOf(solutionOption);

		for (Link currentLink : incomingLinks) {
			KnowledgeElement sourceElement = currentLink.getSource();
			if (KnowledgeType.replaceProAndConWithArgument(sourceElement.getType()) == KnowledgeType.ARGUMENT) {
				Alternative alternative = (Alternative) decisionTableData.get("alternatives")
						.get(numberOfAlternatives - 1);
				Argument argument = new Argument(sourceElement, currentLink);
				getArgumentCriteria(argument, decisionTableData.get("criteria"));
				alternative.addArgument(argument);
			}
		}
	}

	public void getArgumentCriteria(Argument argument, List<DecisionTableElement> criteria) {
		// TODO Make Argument class extend KnowledgeElement and remove calling
		// persistenceManager
		KnowledgeElement rootElement = persistenceManager.getKnowledgeElement(argument.getId(),
				argument.getDocumentationLocation());
		Set<Link> linksOfArgument = this.graph.edgesOf(rootElement);

		for (Link currentLink : linksOfArgument) {
			KnowledgeElement elem = currentLink.getOppositeElement(rootElement);
			// TODO Make checking criteria type more explicit
			if (elem.getType() == KnowledgeType.OTHER) {
				argument.setCriterion(elem);
				// TODO Use set and equals method in Criterion
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
		return issues;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, List<DecisionTableElement>> getDecisionTableData() {
		return decisionTableData;
	}
}