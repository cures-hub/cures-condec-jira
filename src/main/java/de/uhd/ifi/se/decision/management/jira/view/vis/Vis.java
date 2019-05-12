package de.uhd.ifi.se.decision.management.jira.view.vis;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.filtering.GraphFiltering;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;


@XmlRootElement(name = "vis")
@XmlAccessorType(XmlAccessType.FIELD)
public class Vis {
	@XmlElement
	private HashSet<VisNode> nodes;
	@XmlElement
	private HashSet<VisEdge> edges;

	private Graph graph;
	private boolean isHyperlinked;
	private List<DecisionKnowledgeElement> elementsAlreadyAsNode;
	private List<DecisionKnowledgeElement> elementsMatchingFilterCriteria;
	private int level;

	public Vis(){
	}

	public Vis(String projectKey, String elementKey, boolean isHyperlinked){
		this.graph = new GraphImpl(projectKey, elementKey);
		this.setHyperlinked(isHyperlinked);
		DecisionKnowledgeElement rootElement = this.graph.getRootElement();
		nodes = new HashSet<>();
		edges= new HashSet<>();
		this.elementsAlreadyAsNode = new ArrayList<>();
		fillNodesAndEdges(rootElement,null,0);
	}

	public Vis(String projectKey, String elementKey, boolean isHyperlinked, String query, ApplicationUser user){
		this.graph = new GraphImpl(projectKey, elementKey);
		GraphFiltering filter;
		if ((query.matches("\\?jql=(.)+")) || (query.matches("\\?filter=(.)+"))) {
			filter = new GraphFiltering(projectKey, query, user, false);
			filter.produceResultsFromQuery();
			this.elementsMatchingFilterCriteria = filter.getAllElementsMatchingQuery();
			//this.graph = new GraphImplFiltered(projectKey, elementKey, filter);
		} else {
			this.elementsMatchingFilterCriteria = graph.getAllElements();
			this.graph = new GraphImpl(projectKey, elementKey);
		}
		this.setHyperlinked(isHyperlinked);
		DecisionKnowledgeElement rootElement = this.graph.getRootElement();
		nodes = new HashSet<>();
		edges= new HashSet<>();
		elementsAlreadyAsNode = new ArrayList<>();
		level = 50;
		fillNodesAndEdges(rootElement, null, level);

	}



	private void fillNodesAndEdges(DecisionKnowledgeElement element, Link link, int level) {
		if (element == null || element.getProject() == null) {
			return;
		}
		if (graph == null) {
			graph = new GraphImpl(element);
		}

		if (link != null) {
			switch (link.getType()) {
				case "support":
					if (element.getId() == link.getSourceElement().getId()) {
						if (!(this.elementsAlreadyAsNode.contains(element))) {
							this.elementsAlreadyAsNode.add(element);
							this.nodes.add(new VisNode(element, "pro", !this.elementsMatchingFilterCriteria.contains(element),level+2));
						}
					}
					break;
				case "attack":
					if (element.getId() == link.getSourceElement().getId()) {
						if (!(this.elementsAlreadyAsNode.contains(element))) {
							this.elementsAlreadyAsNode.add(element);
							this.nodes.add(new VisNode(element, "con", !this.elementsMatchingFilterCriteria.contains(element),level+2));
						}
					}
					break;
				default:
					if (!(this.elementsAlreadyAsNode.contains(element))) {
						this.elementsAlreadyAsNode.add(element);
						this.nodes.add(new VisNode(element, !this.elementsMatchingFilterCriteria.contains(element),level));
					}
					break;
			}
			this.edges.add(new VisEdge(link));

		} else {
			if (!(this.elementsAlreadyAsNode.contains(element))) {
				this.elementsAlreadyAsNode.add(element);
				this.nodes.add(new VisNode(element, !this.elementsMatchingFilterCriteria.contains(element),level));
			}
		}
		Map<DecisionKnowledgeElement, Link> childrenAndLinks = graph.getAdjacentElementsAndLinks(element);
		for (Map.Entry<DecisionKnowledgeElement, Link> childAndLink : childrenAndLinks.entrySet()) {
			if (childAndLink.getValue().getSourceElement().equals(element)) {
				this.level = level + 1;
			} else {
				this.level = level - 1;
			}
			fillNodesAndEdges(childAndLink.getKey(),childAndLink.getValue(),this.level);
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
