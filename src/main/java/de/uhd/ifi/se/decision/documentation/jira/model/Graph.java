package de.uhd.ifi.se.decision.documentation.jira.model;

import de.uhd.ifi.se.decision.documentation.jira.persistence.PersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;
import de.uhd.ifi.se.decision.documentation.jira.view.treant.Node;

import java.util.*;

public class Graph {

	private PersistenceStrategy strategy;
	private List<Long> containedLinkIds;

	private Node nodeStructure;

	public Graph(String projectKey) {
		StrategyProvider strategyProvider = new StrategyProvider();
		strategy = strategyProvider.getStrategy(projectKey);
	}

	public Graph(String projectKey, String rootElement) {
		this(projectKey);
		DecisionKnowledgeElement decisionRootElement = strategy.getDecisionKnowledgeElement(rootElement);
	}

	public Graph(String projectKey, String rootElement, int linkDistance) {
		this(projectKey, rootElement);
		DecisionKnowledgeElement decisionRootElement = strategy.getDecisionKnowledgeElement(rootElement);
		nodeStructure = createNodeStructure(decisionRootElement,linkDistance,0);
	}

	public Node getNodeStructure() {
		return nodeStructure;
	}

	private Node createNodeStructure(DecisionKnowledgeElement decisionKnowledgeElement, int depth, int currentDepth) {
		Node node = new Node(decisionKnowledgeElement);

		if (currentDepth + 1 < depth) {
			List<Node> nodes = new ArrayList<Node>();
			// Moving getChildren in Graph
			// List<DecisionKnowledgeElement> children = strategy.getChildren(decisionKnowledgeElement);
			List<DecisionKnowledgeElement> children = computeChildElements(decisionKnowledgeElement) ;

			for (DecisionKnowledgeElement child : children) {
				nodes.add(createNodeStructure(child, depth, currentDepth + 1));
			}
			node.setChildren(nodes);
		}
		return node;
	}

	private List<DecisionKnowledgeElement> computeChildElements(DecisionKnowledgeElement decisionRootElement) {
		List<DecisionKnowledgeElement> children = new ArrayList<DecisionKnowledgeElement>();

		List<Link> outwardIssueLinks = strategy.getOutwardLinks(decisionRootElement);
		if (decisionRootElement.getType() != KnowledgeType.ARGUMENT) {
			for (Link link : outwardIssueLinks) {
				DecisionKnowledgeElement outwardElement = link.getOutgoingElement();
				if (outwardElement != null) {
					if (outwardElement.getType() != KnowledgeType.ARGUMENT) {
						children.add(outwardElement);
					}
				}
			}
		}

		List<Link> inwardIssueLinks = strategy.getInwardLinks(decisionRootElement);
		for (Link issueLink : inwardIssueLinks) {
			DecisionKnowledgeElement inwardElement = issueLink.getIngoingElement();
			if (inwardElement != null) {
				if (inwardElement.getType() == KnowledgeType.ARGUMENT) {
					children.add(inwardElement);
				}
			}
		}
		return children;
	}
}