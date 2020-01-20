package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jgrapht.traverse.BreadthFirstIterator;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilteringManagerImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

@XmlRootElement(name = "vis")
@XmlAccessorType(XmlAccessType.FIELD)
public class VisGraph {
	@XmlElement
	private HashSet<VisNode> nodes;
	@XmlElement
	private HashSet<VisEdge> edges;
	@XmlElement
	private String rootElementKey;

	private KnowledgeGraph graph;
	// TODO Remove this attribute because we already have the nodes attribute
	private List<KnowledgeElement> elements;
	private int level = 50;
	private int cid = 0;

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

	public VisGraph(List<KnowledgeElement> elements, String projectKey) {
		this(projectKey);
		this.elements = elements;
		if (elements == null || elements.isEmpty()) {
			return;
		}
		fillNodesAndEdges(elements.get(0));
	}

	public VisGraph(KnowledgeElement rootElement, List<KnowledgeElement> elements) {
		this(rootElement.getProject().getProjectKey());
		this.elements = elements;
		this.rootElementKey = (rootElement.getId() + "_" + rootElement.getDocumentationLocationAsString());
		fillNodesAndEdges(rootElement);
	}

	public VisGraph(ApplicationUser user, FilterSettings filterSettings) {
		this(filterSettings);
		if (user == null) {
			return;
		}
		FilteringManager filteringManager = new FilteringManagerImpl(user, filterSettings);
		List<KnowledgeElement> elements = filteringManager.getAllElementsMatchingFilterSettings();
		this.elements = elements;
		if (elements == null || elements.isEmpty()) {
			return;
		}
		fillNodesAndEdges(elements.get(0));
	}

	public VisGraph(ApplicationUser user, String elementKey, FilterSettings filterSettings) {
		this(filterSettings);
		FilteringManager filterExtractor = new FilteringManagerImpl(user, filterSettings);
		this.elements = filterExtractor.getAllElementsMatchingFilterSettings();
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager
				.getOrCreate(filterSettings.getProjectKey());
		KnowledgeElement rootElement = persistenceManager.getJiraIssueManager().getDecisionKnowledgeElement(elementKey);
		this.rootElementKey = (rootElement.getId() + "_" + rootElement.getDocumentationLocationAsString());
		fillNodesAndEdges(rootElement);
	}

	private void fillNodesAndEdges(KnowledgeElement element) {
		if (element == null || element.getProject() == null) {
			return;
		}

		if (graph == null) {
			graph = KnowledgeGraph.getOrCreate(element.getProject().getProjectKey());
		}
		computeNodes(element);
		computeEdges();
	}

	private KnowledgeElement checkDepth(BreadthFirstIterator<KnowledgeElement, Link> iterator,
			KnowledgeElement iterNode, KnowledgeElement parentNode) {
		// Check Depth
		KnowledgeElement parentNodeTmp = iterator.getParent(iterNode);
		// New Parent Node means next level
		if (parentNodeTmp != null && parentNode != null && parentNodeTmp.getId() != parentNode.getId()) {
			level++;
			return parentNodeTmp;
		}
		return parentNode;
	}

	private void computeNodes(KnowledgeElement startElement) {
		BreadthFirstIterator<KnowledgeElement, Link> iterator;
		try {
			iterator = new BreadthFirstIterator<>(graph, startElement);
		} catch (IllegalArgumentException e) {
			graph.addVertex(startElement);
			iterator = new BreadthFirstIterator<>(graph, startElement);
		}
		KnowledgeElement parentNode = null;
		while (iterator.hasNext()) {
			KnowledgeElement iterNode = iterator.next();
			if (!containsNode(iterNode)) {
				parentNode = checkDepth(iterator, iterNode, parentNode);
				this.nodes.add(new VisNode(iterNode, isCollapsed(iterNode), level, cid));
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
			if (this.elements.contains(link.getSource()) && this.elements.contains(link.getTarget())) {
				if (!containsEdge(link)) {
					this.edges.add(new VisEdge(link));
				}
			}
		}
	}

	private boolean isCollapsed(KnowledgeElement element) {
		return elements.contains(element);
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

	private boolean containsNode(KnowledgeElement node) {
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
