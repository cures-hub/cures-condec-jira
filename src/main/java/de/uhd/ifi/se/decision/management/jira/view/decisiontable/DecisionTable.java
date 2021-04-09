package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class DecisionTable {

	private KnowledgeGraph graph;
	private KnowledgePersistenceManager persistenceManager;
	private List<KnowledgeElement> issues;
	private List<Alternative> alternatives;
	private List<KnowledgeElement> criteria;

	private static final Logger LOGGER = LoggerFactory.getLogger(DecisionTable.class);

	public DecisionTable(String projectKey) {
		graph = KnowledgeGraph.getInstance(projectKey);
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
		LOGGER.info(filterSettings.toString());
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
	public List<KnowledgeElement> getDecisionTableCriteria(ApplicationUser user) {
		List<KnowledgeElement> criteria = new ArrayList<>();
		String query = ConfigPersistenceManager.getDecisionTableCriteriaQuery(persistenceManager.getProjectKey());
		JiraQueryHandler queryHandler = new JiraQueryHandler(user, persistenceManager.getProjectKey(), "?jql=" + query);
		for (Issue jiraIssue : queryHandler.getJiraIssuesFromQuery()) {
			criteria.add(new KnowledgeElement(jiraIssue));
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
		alternatives = new ArrayList<Alternative>();
		criteria = new ArrayList<KnowledgeElement>();

		// TODO Check link type. A decision that leads to a new decision problem should
		// not be shown as solution option for this derived decision problem.
		for (Link currentLink : graph.edgesOf(rootElement)) {
			KnowledgeElement oppositeElement = currentLink.getOppositeElement(rootElement);
			if (oppositeElement.getType() == KnowledgeType.ALTERNATIVE
					|| oppositeElement.getType() == KnowledgeType.DECISION
					|| oppositeElement.getType() == KnowledgeType.SOLUTION) {
				alternatives.add(new Alternative(oppositeElement));
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
		int numberOfAlternatives = alternatives.size();
		Set<Link> incomingLinks = graph.incomingEdgesOf(solutionOption);

		for (Link currentLink : incomingLinks) {
			KnowledgeElement sourceElement = currentLink.getSource();
			if (KnowledgeType.replaceProAndConWithArgument(sourceElement.getType()) == KnowledgeType.ARGUMENT) {
				Alternative alternative = alternatives.get(numberOfAlternatives - 1);
				Argument argument = new Argument(sourceElement, currentLink);
				getArgumentCriteria(argument, criteria);
				alternative.addArgument(argument);
			}
		}
	}

	public void getArgumentCriteria(Argument argument, List<KnowledgeElement> criteria) {
		// TODO Make Argument class extend KnowledgeElement and remove calling
		// persistenceManager
		KnowledgeElement rootElement = persistenceManager.getKnowledgeElement(argument.getId(),
				argument.getDocumentationLocation());
		Set<Link> linksOfArgument = graph.edgesOf(rootElement);

		for (Link currentLink : linksOfArgument) {
			KnowledgeElement elem = currentLink.getOppositeElement(rootElement);
			// TODO Make checking criteria type more explicit
			if (elem.getType() == KnowledgeType.OTHER) {
				argument.setCriterion(elem);
				// TODO Use set and equals method in Criterion
				if (!criteria.contains(elem)) {
					criteria.add(elem);
				}
			}
		}
	}

	/**
	 * @return all decision problems for a certain Jira issue or for the entire
	 *         project.
	 */
	@XmlElement
	public List<KnowledgeElement> getIssues() {
		return issues;
	}

	@XmlElement
	public List<Alternative> getAlternatives() {
		return alternatives;
	}

	@XmlElement
	public List<KnowledgeElement> getCriteria() {
		return criteria;
	}
}