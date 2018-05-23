package de.uhd.ifi.se.decision.documentation.jira.model;

import de.uhd.ifi.se.decision.documentation.jira.persistence.PersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;
import de.uhd.ifi.se.decision.documentation.jira.view.treant.Node;
import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.Data;

import java.util.*;

public class Graph {

	private PersistenceStrategy strategy;
	private List<Long> containedLinkIds;
	private Node nodeStructure;

	public Graph(String projectKey) {
		containedLinkIds = new ArrayList<>();
		StrategyProvider strategyProvider = new StrategyProvider();
		strategy = strategyProvider.getStrategy(projectKey);
	}

	public Graph(String projectKey, String rootElement, int linkDistance) {
		this(projectKey);
		DecisionKnowledgeElement decisionRootElement = strategy.getDecisionKnowledgeElement(rootElement);
		nodeStructure = createNodeStructure(decisionRootElement,linkDistance,0);
	}

	public Node getNodeStructure() {
		return nodeStructure;
	}

	public Data getDataStructure(DecisionKnowledgeElement decisionKnowledgeElement){
		if(decisionKnowledgeElement == null){
			return new Data();
		}
		Data dataRoot = new Data(decisionKnowledgeElement);
		dataRoot.setChildren(computeDataChildElements(decisionKnowledgeElement));
		return  dataRoot;
	}

	private List<Data> computeDataChildElements(DecisionKnowledgeElement decisionRootElement){
		List<Data> childrenList = new ArrayList<>();

		List<DecisionKnowledgeElement> children = computeChildElements(decisionRootElement);
		for (DecisionKnowledgeElement child : children) {
			Data dataChild = new Data(child);
			dataChild.setChildren(computeDataChildElements(child));
			childrenList.add(dataChild);
		}
		return  childrenList;
	}

	private Node createNodeStructure(DecisionKnowledgeElement decisionKnowledgeElement, int depth, int currentDepth) {
		Node node = new Node(decisionKnowledgeElement);

		if (currentDepth + 1 < depth) {
			List<Node> nodes = new ArrayList<Node>();
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

		for (Link link : outwardIssueLinks) {
			if(!containedLinkIds.contains(link.getLinkId())) {
				DecisionKnowledgeElement outwardElement = link.getDestinationObject();
				if (outwardElement != null) {
					if (outwardElement.getType() != KnowledgeType.ARGUMENT) {
						containedLinkIds.add(link.getLinkId());
						children.add(outwardElement);
					}
				}
			}
		}


		List<Link> inwardIssueLinks = strategy.getInwardLinks(decisionRootElement);
		for (Link link : inwardIssueLinks) {
			if(!containedLinkIds.contains(link.getLinkId())) {
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
}