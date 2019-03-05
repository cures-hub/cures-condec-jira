package de.uhd.ifi.se.decision.management.jira.view.vis;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImplFiltered;
import de.uhd.ifi.se.decision.management.jira.filtering.GraphFiltering;
import org.json.JSONPropertyIgnore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


@XmlRootElement(name = "vis")
@XmlAccessorType(XmlAccessType.FIELD)
public class Vis {
	@XmlElement
	private HashSet<VisNode> nodes;
	@XmlElement
	private HashSet<VisEdge> edges;

	private Graph graph;
	private boolean isHyperlinked;

	public Vis(){
	}

	public Vis(String projectKey, String elementKey, boolean isHyperlinked){
		this.graph = new GraphImpl(projectKey, elementKey);
		this.setHyperlinked(isHyperlinked);
		DecisionKnowledgeElement rootElement = this.graph.getRootElement();
		nodes = new HashSet<>();
		edges= new HashSet<>();
		fillNodesAndEdges(rootElement,null);
	}

	public Vis(String projectKey, String elementKey, boolean isHyperlinked, String query, ApplicationUser user){
		GraphFiltering filter;
		if ((query.matches("\\?jql=(.)+")) || (query.matches("\\?filter=(.)+"))) {
			filter = new GraphFiltering(projectKey, query, user);
			filter.produceResultsFromQuery();
			this.graph = new GraphImplFiltered(projectKey, elementKey, filter);
		} else {
			this.graph = new GraphImpl(projectKey, elementKey);
		}
		this.setHyperlinked(isHyperlinked);
		DecisionKnowledgeElement rootElement = this.graph.getRootElement();
		System.out.println("Filling Nodes and Edges");
		nodes = new HashSet<>();
		edges= new HashSet<>();
		fillNodesAndEdges(rootElement, null);
	}



	private void fillNodesAndEdges(DecisionKnowledgeElement element, Link link) {
		if (element == null || element.getProject() == null) {
			return;
		}
		if (graph == null) {
			graph = new GraphImpl(element);
		}
		Map<DecisionKnowledgeElement, Link> childrenAndLinks = graph.getAdjacentElementsAndLinks(element);
		System.out.println(childrenAndLinks.size());
		if (link != null) {
			switch (link.getType()) {
				case "support":
					if (element.getId() == link.getSourceElement().getId()) {
						this.nodes.add(new VisNode(element, "pro"));
					}
					break;
				case "attack":
					if (element.getId() == link.getSourceElement().getId()) {
						this.nodes.add(new VisNode(element, "con"));
					}
					break;
				default:
					this.nodes.add(new VisNode(element));
					break;
			}

		} else {
			this.nodes.add(new VisNode(element));
		}
		for (Map.Entry<DecisionKnowledgeElement, Link> childAndLink : childrenAndLinks.entrySet()) {
			System.out.println(childAndLink.getKey().getKey());
			fillNodesAndEdges(childAndLink.getKey(),childAndLink.getValue());
			this.edges.add(new VisEdge(childAndLink.getValue()));
		}
	}

	public void setNodes(HashSet<VisNode> nodes) {
		this.nodes = nodes;
	}

	public void setEdges(HashSet<VisEdge> edges) {
		this.edges = edges;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;

	}

	public void setHyperlinked(boolean hyperlinked) {
		isHyperlinked = hyperlinked;
	}

	public HashSet<VisNode> getNodes() {
		return nodes;
	}

	public HashSet<VisEdge> getEdges() {
		return edges;
	}

	public Graph getGraph() {
		return graph;
	}

	public boolean isHyperlinked() {
		return isHyperlinked;
	}
}
