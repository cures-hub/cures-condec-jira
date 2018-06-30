package de.uhd.ifi.se.decision.documentation.jira.view.treant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.model.Graph;
import de.uhd.ifi.se.decision.documentation.jira.model.GraphImpl;
import de.uhd.ifi.se.decision.documentation.jira.model.Link;

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
		graph = new GraphImpl(projectKey, elementKey);
		DecisionKnowledgeElement rootElement = graph.getRootElement();
		this.setChart(new Chart());
		this.setNodeStructure(this.createNodeStructure(rootElement, null, depth, 0));
	}

	public Node createNodeStructure(DecisionKnowledgeElement decisionKnowledgeElement, Link link, int depth,
			int currentDepth) {
		if (decisionKnowledgeElement == null || decisionKnowledgeElement.getProjectKey() == null) {
			return new Node();
		}
		if (graph == null) {
			graph = new GraphImpl(decisionKnowledgeElement.getProjectKey());
		}

		Node node;
		if (link != null) {
			node = new Node(decisionKnowledgeElement, link);
			System.out.println(decisionKnowledgeElement.getSummary() + ": " + link.getLinkType());
		} else {
			node = new Node(decisionKnowledgeElement);
		}

		if (currentDepth + 1 < depth) {
			List<Node> nodes = new ArrayList<Node>();
			Map<DecisionKnowledgeElement, Link> childrenAndLinks = graph
					.getLinkedElementsAndLinks(decisionKnowledgeElement);

			for (Map.Entry<DecisionKnowledgeElement, Link> childAndLink : childrenAndLinks.entrySet()) {
				Link childLink = childAndLink.getValue();
//				System.out.println(childLink.getLinkType());
//				childLink.setLinkType("supports");
				nodes.add(createNodeStructure(childAndLink.getKey(), childLink, depth, currentDepth + 1));
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