package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.traverse.BreadthFirstIterator;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterExtractor;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterExtractorImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.Node;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;

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
	private List<DecisionKnowledgeElement> elementsMatchingFilterCriteria;
	@JsonIgnore
	int level = 50;
	@JsonIgnore
	int cid = 0;

	public VisGraph() {
		this.nodes = new HashSet<VisNode>();
		this.edges = new HashSet<VisEdge>();
		this.rootElementKey = "";
	}

	public VisGraph(String projectKey) {
		this();
		if (projectKey == null) {
			return;
		}
		this.graph = KnowledgeGraph.getOrCreate(projectKey);
	}

	public VisGraph(FilterSettings filterSettings) {
		this();
		if (filterSettings == null || filterSettings.getProjectKey() == null) {
			return;
		}
		this.graph = KnowledgeGraph.getOrCreate(filterSettings.getProjectKey());
	}

	public VisGraph(List<DecisionKnowledgeElement> elements, String projectKey) {
		this(projectKey);
		this.elementsMatchingFilterCriteria = elements;
		if (elements == null || elements.isEmpty()) {
			return;
		}
		for (DecisionKnowledgeElement element : elements) {
			fillNodesAndEdges(element);
		}
	}

	public VisGraph(DecisionKnowledgeElement rootElement, List<DecisionKnowledgeElement> elements) {
		this(rootElement.getProject().getProjectKey());
		this.elementsMatchingFilterCriteria = elements;
		this.rootElementKey = (rootElement.getId() + "_" + rootElement.getDocumentationLocationAsString());
		fillNodesAndEdges(rootElement);
	}

	public VisGraph(ApplicationUser user, FilterSettings filterSettings) {
		this(filterSettings);
		if (user == null) {
			return;
		}
		FilterExtractor filterExtractor = new FilterExtractorImpl(user, filterSettings);
		List<DecisionKnowledgeElement> elements = filterExtractor.getAllElementsMatchingCompareFilter();
		this.elementsMatchingFilterCriteria = elements;
		if (elements == null || elements.isEmpty()) {
			return;
		}
		for (DecisionKnowledgeElement element : elements) {
			fillNodesAndEdges(element);
		}
	}

	public VisGraph(ApplicationUser user, FilterSettings filterSettings, List<DecisionKnowledgeElement> decisions) {
		this(filterSettings);
		if (user == null || decisions == null) {
			return;
		}
		FilterExtractor filterExtractor = new FilterExtractorImpl(user, filterSettings);
		List<DecisionKnowledgeElement> elements = filterExtractor.getElementsLinkTypeFilterMatches(decisions);
		this.elementsMatchingFilterCriteria = elements;
		for (DecisionKnowledgeElement element : elements) {
			fillNodesAndEdges(element);
		}
	}

	public VisGraph(ApplicationUser user, String elementKey, FilterSettings filterSettings) {
		this(filterSettings);
		FilterExtractor filterExtractor = new FilterExtractorImpl(user, filterSettings);
		this.elementsMatchingFilterCriteria = filterExtractor.getAllElementsMatchingQuery();
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getOrCreate(filterSettings.getProjectKey()).getDefaultManagerForSingleLocation();
		DecisionKnowledgeElement rootElement = persistenceManager.getDecisionKnowledgeElement(elementKey);
		this.rootElementKey = (rootElement.getId() + "_" + rootElement.getDocumentationLocationAsString());
		fillNodesAndEdges(rootElement);
	}

	private void fillNodesAndEdges(DecisionKnowledgeElement element) {
		if (element == null || element.getProject() == null) {
			return;
		}

		if (graph == null) {
			graph = KnowledgeGraph.getOrCreate(element.getProject().getProjectKey());
		}
		computeNodes(element);
		computeEdges();
	}

	private Node checkDepth(BreadthFirstIterator<Node, Link> iterator, Node iterNode, Node parentNode) {
		// Check Depth
		Node parentNodeTmp = iterator.getParent(iterNode);
		// New Parent Node means next level
		if (parentNodeTmp != null && parentNode != null && parentNodeTmp.getId() != parentNode.getId()) {
			level++;
			return parentNodeTmp;
		}
		return parentNode;
	}

	private void computeNodes(DecisionKnowledgeElement element) {
		Node rootNode = findRootKnowledgeElement(element);
		BreadthFirstIterator<Node, Link> iterator;
		try {
			iterator = new BreadthFirstIterator<>(graph, rootNode);
		} catch (IllegalArgumentException e) {
			graph.addVertex(rootNode);
			iterator = new BreadthFirstIterator<>(graph, rootNode);
		}
		Node parentNode = null;
		while (iterator.hasNext()) {
			Node iterNode = iterator.next();
			DecisionKnowledgeElement nodeElement = null;
			if (iterNode instanceof DecisionKnowledgeElement) {
				nodeElement = (DecisionKnowledgeElement) iterNode;
			}
			if (!containsNode(nodeElement)) {
				parentNode = checkDepth(iterator, iterNode, parentNode);
				this.nodes.add(new VisNode(nodeElement, isCollapsed(nodeElement), level, cid));
				if (parentNode == null) {
					parentNode = iterNode;
					level++;
				}
				cid++;
			}
		}
	}

	private void computeEdges() {
		for (Link link : this.graph.edgeSet()) {
			if (this.elementsMatchingFilterCriteria.contains(link.getSource())
					&& this.elementsMatchingFilterCriteria.contains(link.getTarget())) {
				if (!containsEdge(link)) {
					this.edges.add(new VisEdge(link));
				}
			}
		}
	}

	private Node findRootKnowledgeElement(DecisionKnowledgeElement element) {
		BreadthFirstIterator<Node, Link> allNodeIterator = new BreadthFirstIterator<>(graph);
		DijkstraShortestPath<Node, Link> dijkstraAlg = new DijkstraShortestPath<>(graph);
		ShortestPathAlgorithm.SingleSourcePaths<Node, Link> paths = dijkstraAlg.getPaths(allNodeIterator.next());
		GraphPath<Node, Link> path = paths.getPath(element);
		if (path == null) {
			return element;
		}
		for (Node node : path.getVertexList()) {
			if (node instanceof DecisionKnowledgeElement) {
				DecisionKnowledgeElement nodeElement = (DecisionKnowledgeElement) node;
				if (KnowledgeType.getDefaultTypes().contains(nodeElement.getType())) {
					return nodeElement;
				}
			}
		}
		return element;
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

	public HashSet<VisNode> getNodes() {
		return nodes;
	}

	public HashSet<VisEdge> getEdges() {
		return edges;
	}

	public KnowledgeGraph getGraph() {
		return graph;
	}

	public String getRootElementKey() {
		return rootElementKey;
	}

	public void setRootElementKey(String rootElementKey) {
		this.rootElementKey = rootElementKey;
	}

	private boolean containsNode(Node node) {
		for (VisNode visNode : this.nodes) {
			if (visNode.getId().equals(node.getId() + "_" + node.getDocumentationLocation().getIdentifier())) {
				return true;
			}
		}
		return false;
	}

	private boolean containsEdge(Link link) {
		for (VisEdge visEdge : this.edges) {
			if (Long.parseLong(visEdge.getId()) == link.getId()) {
				return true;
			}
		}
		return false;
	}
}
