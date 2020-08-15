package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

@XmlRootElement(name = "vis")
@XmlAccessorType(XmlAccessType.FIELD)
public class VisGraph {

	@XmlElement
	private Set<VisNode> nodes;

	@XmlElement
	private Set<VisEdge> edges;

	@JsonIgnore
	private KnowledgeElement rootElement;

	@JsonIgnore
	private Graph<KnowledgeElement, Link> subgraph;

	public VisGraph() {
		this.nodes = new HashSet<>();
		this.edges = new HashSet<>();
	}

	public VisGraph(ApplicationUser user, FilterSettings filterSettings) {
		this();
		if (user == null || filterSettings == null || filterSettings.getProjectKey() == null) {
			return;
		}
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		subgraph = filteringManager.getSubgraphMatchingFilterSettings();
		if (subgraph == null || subgraph.vertexSet().isEmpty()) {
			return;
		}
		rootElement = filterSettings.getSelectedElement();
		if (rootElement == null || rootElement.getKey() == null) {
			addNodesAndEdges();
			return;
		}
		addNodesAndEdges();
	}

	/**
	 * @issue How can graph iteration be done over both outgoing and incoming edges
	 *        of a knowledge element (=node)?
	 * @decision Convert the directed graph into an undirected graph for graph
	 *           iteration!
	 */
	private void addNodesAndEdges() {
		if (rootElement != null) {
			subgraph.addVertex(rootElement);
		}

		Graph<KnowledgeElement, Link> undirectedGraph = new AsUndirectedGraph<>(subgraph);

		Set<Link> allEdges = new HashSet<>();
		BreadthFirstIterator<KnowledgeElement, Link> iterator = new BreadthFirstIterator<>(undirectedGraph,
				rootElement);

		while (iterator.hasNext()) {
			KnowledgeElement element = iterator.next();
			nodes.add(new VisNode(element, iterator.getDepth(element)));
			allEdges.addAll(undirectedGraph.edgesOf(element));
		}

		for (Link link : allEdges) {
			edges.add(new VisEdge(link));
		}
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

	public Graph<KnowledgeElement, Link> getGraph() {
		return subgraph;
	}

	@XmlElement(name = "rootElementId")
	public String getRootElementId() {
		return rootElement == null ? "" : rootElement.getId() + "_" + rootElement.getDocumentationLocationAsString();
	}
}