package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jgrapht.graph.AsSubgraph;
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
		Set<KnowledgeElement> elements = new HashSet<>(filteringManager.getAllElementsMatchingFilterSettings());
		if (elements == null || elements.isEmpty()) {
			return;
		}
		if (rootElementKey == null) {
			addNodesAndEdges(null, elements);
			return;
		}
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager
				.getOrCreate(filterSettings.getProjectKey());
		KnowledgeElement rootElement = persistenceManager.getJiraIssueManager()
				.getDecisionKnowledgeElement(rootElementKey);
		this.rootElementKey = rootElement.getId() + "_" + rootElement.getDocumentationLocationAsString();
		addNodesAndEdges(rootElement, elements);
	}

	private void addNodesAndEdges(KnowledgeElement startElement, Set<KnowledgeElement> elements) {
		if (startElement != null && !graph.containsVertex(startElement)) {
			graph.addVertex(startElement);
		}

		AsSubgraph<KnowledgeElement, Link> subgraph = new AsSubgraph<KnowledgeElement, Link>(graph, elements);
		BreadthFirstIterator<KnowledgeElement, Link> iterator = new BreadthFirstIterator<KnowledgeElement, Link>(
				subgraph, startElement);

		while (iterator.hasNext()) {
			KnowledgeElement element = iterator.next();
			nodes.add(new VisNode(element, isCollapsed(element, elements), 50 + iterator.getDepth(element), cid));
			cid++;

			for (Link link : subgraph.edgesOf(element)) {
				if (containsEdge(link)) {
					continue;
				}
				edges.add(new VisEdge(link));
			}
		}
	}

	private boolean isCollapsed(KnowledgeElement element, Set<KnowledgeElement> elements) {
		return !elements.contains(element);
	}

	private boolean containsEdge(Link link) {
		for (VisEdge visEdge : edges) {
			if (visEdge.getId() == link.getId()) {
				return true;
			}
		}
		return false;
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
}
