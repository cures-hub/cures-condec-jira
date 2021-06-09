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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Creates the vis graph content. The vis graph is rendered with the vis.js
 * library.
 * 
 * Iterates over the filtered {@link KnowledgeGraph} provided by the
 * {@link FilteringManager}.
 * 
 * If you want to change the shown (sub-)graph, do not change this class but
 * change the {@link FilteringManager} and/or the {@link KnowledgeGraph}.
 */
@XmlRootElement(name = "vis")
@XmlAccessorType(XmlAccessType.FIELD)
public class VisGraph {

	@XmlElement
	private Set<VisNode> nodes;

	@XmlElement
	private Set<VisEdge> edges;

	@JsonIgnore
	private KnowledgeElement selectedElement;

	@JsonIgnore
	private Graph<KnowledgeElement, Link> subgraph;

	private static final Logger LOGGER = LoggerFactory.getLogger(VisGraph.class);

	public VisGraph() {
		this.nodes = new HashSet<>();
		this.edges = new HashSet<>();
	}

	public VisGraph(ApplicationUser user, FilterSettings filterSettings) {
		this();
		if (user == null || filterSettings == null || filterSettings.getProjectKey() == null) {
			return;
		}
		LOGGER.info(filterSettings.toString());
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		subgraph = filteringManager.getFilteredGraph();
		selectedElement = filterSettings.getSelectedElement();
		addNodesAndEdges(filterSettings.isHierarchical(), filterSettings.isNoColors());
	}

	/**
	 * @param isHierarchical
	 *            true if the {@link KnowledgeGraph} or a respective subgraph
	 *            provided by the {@link FilteringManager} should be shown with a
	 *            hierarchy of nodes.
	 */
	private void addNodesAndEdges(boolean isHierarchical, boolean noColors) {
		if (selectedElement != null) {
			subgraph.addVertex(selectedElement);
		}
		if (isHierarchical) {
			addNodesAndEdgesWithHierarchy(noColors);
		} else {
			addNodesAndEdgesWithoutHierarchy(noColors);
		}
	}

	/**
	 * @issue How can graph iteration be done over both outgoing and incoming edges
	 *        of a knowledge element (=node)?
	 * @decision Convert the directed graph into an undirected graph for graph
	 *           iteration!
	 */
	private void addNodesAndEdgesWithHierarchy(boolean noColors) {
		Graph<KnowledgeElement, Link> undirectedGraph = new AsUndirectedGraph<>(subgraph);

		Set<Link> allEdges = new HashSet<>();
		BreadthFirstIterator<KnowledgeElement, Link> iterator = new BreadthFirstIterator<>(undirectedGraph,
				selectedElement);

		while (iterator.hasNext()) {
			KnowledgeElement element = iterator.next();
			nodes.add(new VisNode(element, iterator.getDepth(element), noColors));
			allEdges.addAll(undirectedGraph.edgesOf(element));
		}

		allEdges.forEach(link -> edges.add(new VisEdge(link)));
	}

	private void addNodesAndEdgesWithoutHierarchy(boolean noColors) {
		subgraph.vertexSet().forEach(element -> nodes.add(new VisNode(element, noColors)));
		subgraph.edgeSet().forEach(link -> edges.add(new VisEdge(link)));
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

	@XmlElement(name = "selectedVisNodeId")
	public String getSelectedVisNodeId() {
		return selectedElement == null ? ""
				: selectedElement.getId() + "_" + selectedElement.getDocumentationLocationAsString();
	}
}