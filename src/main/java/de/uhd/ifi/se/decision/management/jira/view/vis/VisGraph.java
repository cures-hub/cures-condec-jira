package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jgrapht.traverse.BreadthFirstIterator;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.Node;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeGraphImpl;

@XmlRootElement(name = "vis")
@XmlAccessorType(XmlAccessType.FIELD)
public class VisGraph {
	@XmlElement
	private HashSet<VisNode> nodes;
	@XmlElement
	private HashSet<VisEdge> edges;
	@XmlElement
	private String rootElementKey;

	@JsonIgnore
	private KnowledgeGraph graph;
	@JsonIgnore
	private boolean isHyperlinked;
	@JsonIgnore
	private List<DecisionKnowledgeElement> elementsMatchingFilterCriteria;
	@JsonIgnore
	int level = 50;
	@JsonIgnore
	int cid = 0;

	private List<Long> linkIdsVisited;

	public VisGraph() {
		nodes = new HashSet<>();
		edges = new HashSet<>();
		linkIdsVisited = new ArrayList<>();
	}

	public VisGraph(List<DecisionKnowledgeElement> elements, String projectKey) {
		this();
		if (projectKey == null) {
			return;
		}
		this.elementsMatchingFilterCriteria = elements;
		this.graph = new KnowledgeGraphImpl(projectKey);
		this.setHyperlinked(false);
		if (elements == null || elements.size() == 0) {
			this.nodes = new HashSet<>();
			this.edges = new HashSet<>();
			this.rootElementKey = "";
			return;
		}
		for (DecisionKnowledgeElement element : elements) {
			fillNodesAndEdges(element);
		}
	}

	public VisGraph(DecisionKnowledgeElement rootElement, List<DecisionKnowledgeElement> elements) {
		this();
		this.elementsMatchingFilterCriteria = elements;
		this.rootElementKey = (rootElement.getId() + "_" + rootElement.getDocumentationLocationAsString());
		this.graph = new KnowledgeGraphImpl(rootElement.getProject().getProjectKey());
		fillNodesAndEdges(rootElement);
	}

	private void fillNodesAndEdges(DecisionKnowledgeElement element) {
		if (element == null || element.getProject() == null) {
			return;
		}

		if (graph == null) {
			graph = new KnowledgeGraphImpl(element.getProject().getProjectKey());
		}
		BreadthFirstIterator<Node, Link> iterator = new BreadthFirstIterator<>(graph, element);
		Node parentNode = null;
		while (iterator.hasNext()) {
			Node iterNode = iterator.next();
			DecisionKnowledgeElement nodeElement = null;
			if (iterNode instanceof DecisionKnowledgeElement) {
				nodeElement = (DecisionKnowledgeElement) iterNode;
			}
			this.nodes.add(new VisNode(nodeElement, isCollapsed(nodeElement), level, cid));
			// Check Depth
			Node parentNodeTmp = iterator.getParent(iterNode);
			// Is Root Node and will be alone as Top Element
			if (parentNode == null) {
				parentNode = iterNode;
				level++;
			}
			// New Parent Node means next level
			if (parentNodeTmp != null && parentNodeTmp.getId() != parentNode.getId()) {
				parentNode = parentNodeTmp;
				level++;
			}
			cid++;
		}
		for (Link link : this.graph.edgeSet()) {
			if (this.elementsMatchingFilterCriteria.contains(link.getSourceElement())
					&& this.elementsMatchingFilterCriteria.contains(link.getDestinationElement())) {
				if(!this.linkIdsVisited.contains(link.getId())){
					this.linkIdsVisited.add(link.getId());
					this.edges.add(new VisEdge(link));
				}
			}
		}
	}

	private boolean isCollapsed(DecisionKnowledgeElement element) {
		return elementsMatchingFilterCriteria.contains(element);
	}

	public void setNodes(HashSet<VisNode> nodes) {
		this.nodes = nodes;
	}

	public void setEdges(HashSet<VisEdge> edges) {
		this.edges = edges;
	}

	public void setGraph(KnowledgeGraph graph) {
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

	public KnowledgeGraph getGraph() {
		return graph;
	}

	public boolean isHyperlinked() {
		return isHyperlinked;
	}

	public String getRootElementKey() {
		return rootElementKey;
	}

	public void setRootElementKey(String rootElementKey) {
		this.rootElementKey = rootElementKey;
	}
}
