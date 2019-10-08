package de.uhd.ifi.se.decision.management.jira.view.vis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.uhd.ifi.se.decision.management.jira.model.*;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeGraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.NodeImpl;
import org.codehaus.jackson.annotate.JsonIgnore;

import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.*;

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
	private List<DecisionKnowledgeElement> elementsAlreadyAsNode;
	@JsonIgnore
	private List<DecisionKnowledgeElement> elementsMatchingFilterCriteria;
	@JsonIgnore
	private List<DecisionKnowledgeElement> elementsInGraph;

	public VisGraph() {
		nodes = new HashSet<>();
		edges = new HashSet<>();
		elementsAlreadyAsNode = new ArrayList<>();
		elementsInGraph = new ArrayList<>();
	}

	public VisGraph(List<DecisionKnowledgeElement> elements,String projectKey) {
		this();
		if(projectKey == null ){
			return;
		}
		this.elementsMatchingFilterCriteria = elements;
		this.graph = new KnowledgeGraphImpl(projectKey);
		this.setHyperlinked(false);
		if(elements== null || elements.size() ==0){
			this.nodes = new HashSet<>();
			this.edges = new HashSet<>();
			this.rootElementKey= "";
			return;
		}

		for(DecisionKnowledgeElement element: elements){
			fillNodesAndEdges(element);
		}
	}

	public VisGraph(DecisionKnowledgeElement rootElement, List<DecisionKnowledgeElement> elements) {
		this();
		this.elementsMatchingFilterCriteria = elements;
		this.rootElementKey = (rootElement.getId() + "_" + rootElement.getDocumentationLocationAsString());
		fillNodesAndEdges(rootElement);
	}

	// TODO Reduce complexity of this function
	// Way too big needs to be split in small functions
	private void fillNodesAndEdges(DecisionKnowledgeElement element) {
		int level = 50;
		int cid = 0;
		if (element == null || element.getProject() == null) {
			return;
		}
		if (graph == null) {
			graph = new KnowledgeGraphImpl(element.getProject().getProjectKey());
		}
		BreadthFirstIterator<Node,Link> iterator = new BreadthFirstIterator<>(graph, element);
		Node parentNode = null;
		while (iterator.hasNext()) {
			Node iterNode = iterator.next();
			Link iterLink = iterator.getSpanningTreeEdge(iterNode);
			if (iterLink != null) {
				switch (iterLink.getType()) {
					case "support":
						if (element.getId() == iterLink.getSourceElement().getId()) {
							if (!(this.elementsAlreadyAsNode.contains(element))) {
								this.elementsAlreadyAsNode.add(element);
								this.nodes.add(new VisNode(element, "pro", isCollapsed(element), level + 2, cid));
							}
						} else {
							if (!(this.elementsAlreadyAsNode.contains(element))) {
								this.elementsAlreadyAsNode.add(element);
								this.nodes.add(new VisNode(element, isCollapsed(element), level, cid));
							}
						}
						break;
					case "attack":
						if (element.getId() == iterLink.getSourceElement().getId()) {
							if (!(this.elementsAlreadyAsNode.contains(element))) {
								this.elementsAlreadyAsNode.add(element);
								this.nodes.add(new VisNode(element, "con", isCollapsed(element), level + 2, cid));
							}
						} else {
							if (!(this.elementsAlreadyAsNode.contains(element))) {
								this.elementsAlreadyAsNode.add(element);
								this.nodes.add(new VisNode(element, isCollapsed(element), level, cid));
							}
						}
						break;
					default:
						if (!(this.elementsAlreadyAsNode.contains(element))) {
							this.elementsAlreadyAsNode.add(element);
							this.nodes.add(new VisNode(element, isCollapsed(element), level, cid));
						}
						break;
				}
				this.edges.add(new VisEdge(iterLink));

			} else {
				if (!(this.elementsAlreadyAsNode.contains(element))) {
					this.elementsAlreadyAsNode.add(element);
					this.nodes.add(new VisNode(element, isCollapsed(element), level, cid));
				}
			}
			//Check Depth
			if(parentNode == null){
				parentNode = iterNode;
			}
			if(iterator.getParent(iterNode) != parentNode) {
				parentNode = iterator.getParent(iterNode);
				level++;
			}
			cid++;


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
