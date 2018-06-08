package de.uhd.ifi.se.decision.documentation.jira.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import de.uhd.ifi.se.decision.documentation.jira.persistence.PersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;

@JsonAutoDetect
public class Graph {

	private PersistenceStrategy persistenceStrategy;
	private List<Long> containedLinkIds;
	private DecisionKnowledgeElement rootElement;

	public Graph() {
		containedLinkIds = new ArrayList<>();
	}

	public Graph(String projectKey) {
		this();
		StrategyProvider strategyProvider = new StrategyProvider();
		this.persistenceStrategy = strategyProvider.getStrategy(projectKey);
	}

	public Graph(String projectKey, String rootElementKey) {
		this(projectKey);
		this.rootElement = persistenceStrategy.getDecisionKnowledgeElement(rootElementKey);
	}

	public Graph(DecisionKnowledgeElement rootElement) {
		this(rootElement.getProjectKey());
		this.rootElement = rootElement;
	}

	public List<DecisionKnowledgeElement> getChildElements(DecisionKnowledgeElement rootElement) {
		List<DecisionKnowledgeElement> children = new ArrayList<DecisionKnowledgeElement>();

		if (rootElement == null) {
			return children;
		}

		List<Link> outwardIssueLinks = persistenceStrategy.getOutwardLinks(rootElement);

		for (Link link : outwardIssueLinks) {
			if (!containedLinkIds.contains(link.getLinkId())) {
				DecisionKnowledgeElement outwardElement = link.getDestinationObject();
				if (outwardElement != null) {
					if (outwardElement.getType() != KnowledgeType.ARGUMENT) {
						containedLinkIds.add(link.getLinkId());
						children.add(outwardElement);
					}
				}
			}
		}

		List<Link> inwardIssueLinks = persistenceStrategy.getInwardLinks(rootElement);
		for (Link link : inwardIssueLinks) {
			if (!containedLinkIds.contains(link.getLinkId())) {
				DecisionKnowledgeElement inwardElement = link.getSourceObject();
				if (inwardElement != null) {
					if (inwardElement.getType() == KnowledgeType.ARGUMENT) {
						containedLinkIds.add(link.getLinkId());
						children.add(inwardElement);
					}
				}
			}
		}
		return children;
	}

	public DecisionKnowledgeElement getRootElement() {
		return rootElement;
	}

	public void setRootElement(DecisionKnowledgeElement rootElement) {
		this.rootElement = rootElement;
	}
}