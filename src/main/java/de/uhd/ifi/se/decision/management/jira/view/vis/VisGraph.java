package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private Set<VisNode> nodes;
	@XmlElement
	private Set<VisEdge> edges;
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

	public VisGraph(FilterSettings filterSettings) {
		this();
		if (filterSettings == null || filterSettings.getProjectKey() == null) {
			return;
		}
		this.graph = KnowledgeGraph.getOrCreate(filterSettings.getProjectKey());
	}

	public VisGraph(ApplicationUser user, FilterSettings filterSettings) {
		this(user, filterSettings, null);
	}

	public VisGraph(ApplicationUser user, FilterSettings filterSettings, String rootElementKey) {
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
		if (rootElementKey == null) {
			fillNodesAndEdges(elements.get(0));
			return;
		}
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager
				.getOrCreate(filterSettings.getProjectKey());
		KnowledgeElement rootElement = persistenceManager.getJiraIssueManager()
				.getDecisionKnowledgeElement(rootElementKey);
		this.rootElementKey = rootElement.getId() + "_" + rootElement.getDocumentationLocationAsString();
		fillNodesAndEdges(rootElement);
	}

	private void fillNodesAndEdges(KnowledgeElement element) {
		computeNodes(element);
		computeEdges();
	}

	private void computeNodes(KnowledgeElement startElement) {
		if (!graph.containsVertex(startElement)) {
			graph.addVertex(startElement);
		}

		BreadthFirstIterator<KnowledgeElement, Link> iterator = new BreadthFirstIterator<KnowledgeElement, Link>(graph,
				startElement);
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
		return !elements.contains(element);
	}

	public void setNodes(Set<VisNode> nodes) {
		this.nodes = nodes;
	}

	public void setEdges(Set<VisEdge> edges) {
		this.edges = edges;
	}

	public void setGraph(KnowledgeGraph graph) {
		this.graph = graph;

	}

	public Set<VisNode> getNodes() {
		return nodes;
	}

	public Set<VisEdge> getEdges() {
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
