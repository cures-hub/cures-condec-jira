package de.uhd.ifi.se.decision.documentation.jira.view.treant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.model.Graph;
import de.uhd.ifi.se.decision.documentation.jira.model.Link;
import de.uhd.ifi.se.decision.documentation.jira.persistence.ActiveObjectStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.PersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;

/**
 * @description Model class for Treant
 */
@XmlRootElement(name = "treant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Treant {
	private static final Logger LOGGER = LoggerFactory.getLogger(Treant.class);

	@XmlElement
	private Chart chart;

	@XmlElement(name = "nodeStructure")
	private Node nodeStructure;

	private PersistenceStrategy strategy;

	public Treant() {
	}

	public Treant(String projectKey, String elementKey, int depth) {
		StrategyProvider strategyProvider = new StrategyProvider();
		strategy = strategyProvider.getStrategy(projectKey);
		DecisionKnowledgeElement decisionKnowledgeElement = strategy.getDecisionKnowledgeElement(elementKey);
		this.setChart(new Chart());

//		Graph graph = new Graph(decisionKnowledgeElement);
//		Node node = new Node(decisionKnowledgeElement);
//		List<Node> nodes = new ArrayList<Node>();
//		Map<DecisionKnowledgeElement, Set<Link>> linkedElements = graph.getLinkedElements();
//		for (Map.Entry<DecisionKnowledgeElement, Set<Link>> linkedElement : linkedElements.entrySet()) {
//			DecisionKnowledgeElement element = linkedElement.getKey();
//			System.out.println("Summary " + element.getSummary());
//			Set<Link> links = linkedElement.getValue();
//			System.out.println("Number of links " + links.size());
//			for (Link link : links) {
//				nodes.add(new Node(link.getOutgoingElement()));
//				//nodes.add(new Node(link.getIngoingElement()));
//			}
//		}
//		node.setChildren(nodes);
//		this.setNodeStructure(node);
		this.setNodeStructure(createNodeStructure(decisionKnowledgeElement, depth, 0));
	}

	private Node createNodeStructure(DecisionKnowledgeElement decisionKnowledgeElement, int depth, int currentDepth) {
		Node node = new Node(decisionKnowledgeElement);

		if (currentDepth + 1 < depth) {
			List<Node> nodes = new ArrayList<Node>();
			List<DecisionKnowledgeElement> children = strategy.getChildren(decisionKnowledgeElement);

			for (DecisionKnowledgeElement child : children) {
				nodes.add(createNodeStructure(child, depth, currentDepth + 1));
			}
			node.setChildren(nodes);
		}
		return node;
	}

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
	}

	public Node getNodeStructure() {
		return nodeStructure;
	}

	public void setNodeStructure(Node nodeStructure) {
		this.nodeStructure = nodeStructure;
	}
}