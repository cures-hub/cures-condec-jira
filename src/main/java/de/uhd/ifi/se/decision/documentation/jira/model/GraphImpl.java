package de.uhd.ifi.se.decision.documentation.jira.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import de.uhd.ifi.se.decision.documentation.jira.persistence.PersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;

/**
 * @description Model class for a graph of decision knowledge elements
 */
@JsonAutoDetect
public class GraphImpl implements Graph {

	private PersistenceStrategy persistenceStrategy;
	private List<Long> linkIds;
	private DecisionKnowledgeElement rootElement;

	public GraphImpl() {
		linkIds = new ArrayList<>();
	}

	public GraphImpl(String projectKey) {
		this();
		StrategyProvider strategyProvider = new StrategyProvider();
		this.persistenceStrategy = strategyProvider.getStrategy(projectKey);
	}

	public GraphImpl(String projectKey, String rootElementKey) {
		this(projectKey);
		this.rootElement = persistenceStrategy.getDecisionKnowledgeElement(rootElementKey);
	}

	public GraphImpl(DecisionKnowledgeElement rootElement) {
		this(rootElement.getProjectKey());
		this.rootElement = rootElement;
	}

	public List<DecisionKnowledgeElement> getLinkedElements(DecisionKnowledgeElement element) {
		List<DecisionKnowledgeElement> linkedElements = new ArrayList<DecisionKnowledgeElement>();
		linkedElements.addAll(this.getElementsLinkedWithOutwardLinks(element));
		linkedElements.addAll(this.getElementsLinkedWithInwardLinks(element));
		return linkedElements;
	}

	public List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(DecisionKnowledgeElement element) {
		List<DecisionKnowledgeElement> linkedElements = new ArrayList<DecisionKnowledgeElement>();

		if (element == null) {
			return linkedElements;
		}

		List<Link> outwardIssueLinks = persistenceStrategy.getOutwardLinks(element);
		for (Link link : outwardIssueLinks) {
			if (!linkIds.contains(link.getLinkId())) {
				DecisionKnowledgeElement outwardElement = link.getDestinationObject();
				if (outwardElement != null) {
					if (outwardElement.getType() != KnowledgeType.ARGUMENT) {
						linkIds.add(link.getLinkId());
						linkedElements.add(outwardElement);
					}
				}
			}
		}

		return linkedElements;
	}

	public List<DecisionKnowledgeElement> getElementsLinkedWithInwardLinks(DecisionKnowledgeElement element) {
		List<DecisionKnowledgeElement> linkedElements = new ArrayList<DecisionKnowledgeElement>();

		if (element == null) {
			return linkedElements;
		}

		List<Link> inwardIssueLinks = persistenceStrategy.getInwardLinks(rootElement);
		for (Link link : inwardIssueLinks) {
			if (!linkIds.contains(link.getLinkId())) {
				DecisionKnowledgeElement inwardElement = link.getSourceObject();
				if (inwardElement != null) {
					if (inwardElement.getType() == KnowledgeType.ARGUMENT) {
						linkIds.add(link.getLinkId());
						linkedElements.add(inwardElement);
					}
				}
			}
		}

		return linkedElements;
	}

	public DecisionKnowledgeElement getRootElement() {
		return rootElement;
	}

	public void setRootElement(DecisionKnowledgeElement rootElement) {
		this.rootElement = rootElement;
	}
}