package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.view.GraphFiltering;

/**
 * Creates Treant content
 */
@XmlRootElement(name = "treant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Treant {
	@XmlElement
	private Chart chart;

	@XmlElement
	private Node nodeStructure;

	private Graph graph;
	private int absolutDepth;
	private boolean isHyperlinked;

	public Treant() {
	}

	public Treant(String projectKey, String elementKey, int depth, boolean isHyperlinked) {
		this.graph = new GraphImpl(projectKey, elementKey);
		DecisionKnowledgeElement rootElement = this.graph.getRootElement();
		this.setAbsoluteDepth(0);
		this.setChart(new Chart());
		this.setNodeStructure(this.createNodeStructure(rootElement, null, depth, 1));
		this.setHyperlinked(isHyperlinked);
	}

	public Treant(String projectKey, String elementKey, int depth) {
		this(projectKey, elementKey, depth, false);
	}


	public Treant(String projectKey, String elementKey, int depth, String query, ApplicationUser user) {
		GraphFiltering filter = null;
		if ((query.matches("\\?jql=(.)+")) || (query.matches("\\?filter=(.)+"))) {
			filter = new GraphFiltering(projectKey,query,user);
			filter.produceResultsFromQuery();
		}
		this.graph = new GraphImpl(projectKey, elementKey, filter);
		DecisionKnowledgeElement rootElement = this.graph.getRootElement();
		this.setChart(new Chart());
		this.setNodeStructure(this.createNodeStructure(rootElement, null, depth, 1));
		this.setHyperlinked(false);
	}

	public Node createNodeStructure(DecisionKnowledgeElement element, Link link, int depth, int currentDepth) {
		if (element == null || element.getProject().getProjectKey() == null) {
			return new Node();
		}

		if (graph == null) {
			graph = new GraphImpl(element);
		}
		Map<DecisionKnowledgeElement, Link> childrenAndLinks = graph.getLinkedElementsAndLinks(element);

		boolean isCollapsed = false;
		if (currentDepth == depth && childrenAndLinks.size() != 0) {
			isCollapsed = true;
		}

		Node node;
		if (link != null) {
			node = new Node(element, link, isCollapsed, isHyperlinked);
		} else {
			node = new Node(element, isCollapsed, isHyperlinked);
		}
		List<Node> nodes = new ArrayList<Node>();
		for (Map.Entry<DecisionKnowledgeElement, Link> childAndLink : childrenAndLinks.entrySet()) {
			if ((childAndLink.getKey() instanceof Sentence && ((Sentence) childAndLink.getKey()).isRelevant())
					|| !(childAndLink.getKey() instanceof Sentence)) {
				Node newChildNode = createNodeStructure(childAndLink.getKey(), childAndLink.getValue(), depth,
						currentDepth + 1);
				nodes.add(newChildNode);
			}
		}
		node.setChildren(nodes);
		if (this.absolutDepth < currentDepth) {
			this.absolutDepth = currentDepth;
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

	public int getAbsoluteDepth() {
		return absolutDepth;
	}

	public void setAbsoluteDepth(int absoluteDepth) {
		this.absolutDepth = absoluteDepth;
	}

	public boolean isHyperlinked() {
		return isHyperlinked;
	}

	public void setHyperlinked(boolean isHyperlinked) {
		this.isHyperlinked = isHyperlinked;
	}
}