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
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
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
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		AsSubgraph<KnowledgeElement, Link> subgraph = filteringManager.getSubgraphMatchingFilterSettings();
		if (subgraph == null || subgraph.vertexSet().isEmpty()) {
			return;
		}
		if (rootElementKey == null || rootElementKey.isBlank()) {
			addNodesAndEdges(null, subgraph);
			return;
		}
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager
				.getOrCreate(filterSettings.getProjectKey());
		KnowledgeElement rootElement = persistenceManager.getJiraIssueManager()
				.getKnowledgeElement(rootElementKey);

		// TODO This is not a key but id_documentationLocation
		this.rootElementKey = rootElement.getId() + "_" + rootElement.getDocumentationLocationAsString();
		addNodesAndEdges(rootElement, subgraph);
	}

	private void addNodesAndEdges(KnowledgeElement startElement, AsSubgraph<KnowledgeElement, Link> subgraph) {
		if (startElement != null) {
			graph.addVertex(startElement);
			subgraph.addVertex(startElement);
		}

		BreadthFirstIterator<KnowledgeElement, Link> iterator = new BreadthFirstIterator<KnowledgeElement, Link>(
				subgraph, startElement);

		while (iterator.hasNext()) {
			KnowledgeElement element = iterator.next();
			nodes.add(new VisNode(element, false, 50 + iterator.getDepth(element), cid));
			cid++;

			for (Link link : subgraph.edgesOf(element)) {
				if (containsEdge(link)) {
					continue;
				}
				edges.add(new VisEdge(link));
			}
		}
	}

	// private boolean isCollapsed(KnowledgeElement element, Set<KnowledgeElement>
	// elements) {
	// return !elements.contains(element);
	// }

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
}
