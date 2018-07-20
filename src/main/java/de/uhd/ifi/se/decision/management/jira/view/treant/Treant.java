package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Creates Treant content
 */
@XmlRootElement(name = "treant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Treant {
	@XmlElement
	private Chart chart;

	@XmlElement(name = "nodeStructure")
	private Node nodeStructure;

	private Graph graph;

	public Treant() {
	}

	public Treant(String projectKey, String elementKey, int depth) {
		this.graph = new GraphImpl(projectKey, elementKey);
		DecisionKnowledgeElement rootElement = this.graph.getRootElement();
		this.setChart(new Chart());
		this.setNodeStructure(this.createNodeStructure(rootElement, null, depth, 0));
	}

	public Node createNodeStructure(DecisionKnowledgeElement decisionKnowledgeElement, Link link, int depth,
			int currentDepth) {
		if (decisionKnowledgeElement == null || decisionKnowledgeElement.getProject().getProjectKey() == null) {
			return new Node();
		}
		if (graph == null) {
			graph = new GraphImpl(decisionKnowledgeElement);
		}

		Node node;
		if (link != null) {
			node = new Node(decisionKnowledgeElement, link);
		} else {
			node = new Node(decisionKnowledgeElement);
		}

		if (currentDepth + 1 < depth) {
			List<Node> nodes = new ArrayList<Node>();
			Map<DecisionKnowledgeElement, Link> childrenAndLinks = graph
					.getLinkedElementsAndLinks(decisionKnowledgeElement);

			for (Map.Entry<DecisionKnowledgeElement, Link> childAndLink : childrenAndLinks.entrySet()) {
				nodes.add(createNodeStructure(childAndLink.getKey(), childAndLink.getValue(), depth, currentDepth + 1));
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