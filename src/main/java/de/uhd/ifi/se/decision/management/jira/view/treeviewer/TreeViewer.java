package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.KnowledgeElementWithImpact;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Creates indented outline (tree viewer) content. The indented outline is
 * rendered with the jstree library.
 * 
 * Iterates over the filtered {@link KnowledgeGraph} provided by the
 * {@link FilteringManager}.
 * 
 * If you want to change the shown (sub-)graph, do not change this class but
 * change the {@link FilteringManager} and/or the {@link KnowledgeGraph}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TreeViewer {

	@XmlElement
	private boolean multiple;
	@XmlElement(name = "check_callback")
	private boolean checkCallback;
	@XmlElement
	private Map<String, Boolean> themes;
	@XmlElement(name = "data")
	private Set<TreeViewerNode> nodes;
	@JsonIgnore
	private Graph<KnowledgeElement, Link> graph;
	@JsonIgnore
	private List<String> ids;
	@JsonIgnore
	private long index;
	@JsonIgnore
	private FilterSettings filterSettings;

	private static final Logger LOGGER = LoggerFactory.getLogger(TreeViewer.class);

	public TreeViewer() {
		this.multiple = false;
		this.filterSettings = new FilterSettings();
		this.checkCallback = true;
		this.themes = ImmutableMap.of("icons", true);
		this.ids = new ArrayList<String>();
		nodes = new HashSet<TreeViewerNode>();
	}

	/**
	 * Constructor for an indented outline (jstree tree viewer) that matches the
	 * {@link FilterSettings}. If a knowledge element is selected in the
	 * {@link FilterSettings}, the tree viewer comprises only one tree with the
	 * selected element as the root element. If no element is selected, the tree
	 * viewer contains a list of trees.
	 *
	 * @param filterSettings
	 *            For example, the {@link FilterSettings} cover the selected element
	 *            and the knowledge types to be shown. The selected element can be
	 *            null.
	 */
	public TreeViewer(FilterSettings filterSettings) {
		this();
		if (filterSettings == null) {
			return;
		}
		LOGGER.info(filterSettings.toString());

		this.filterSettings = filterSettings;

		FilteringManager filteringManager = new FilteringManager(filterSettings);
		graph = filteringManager.getFilteredGraph();

		KnowledgeElement rootElement = filterSettings.getSelectedElement();

		if (rootElement != null) {
			// only one tree is shown
			TreeViewerNode rootNode = getTreeViewerNodeWithChildren(rootElement);
			nodes.add(rootNode);
			return;
		}

		// many trees are shown in overview and rationale backlog
		Set<KnowledgeElement> rootElements = graph.vertexSet();
		if (filteringManager.getFilterSettings().getLinkDistance() == 0) {
			rootElements.forEach(element -> nodes.add(new TreeViewerNode(element, filterSettings)));
			return;
		}

		filteringManager.getFilterSettings().setKnowledgeTypes(null);
		filteringManager.getFilterSettings().setStatus(null);

		for (KnowledgeElement element : rootElements) {
			filteringManager.getFilterSettings().setSelectedElementObject(element);
			graph = filteringManager.getFilteredGraph();
			nodes.add(getTreeViewerNodeWithChildren(element));
		}
	}

	/**
	 * A specialized constructor for an indented outline (jstree tree viewer) that
	 * matches the {@link FilterSettings}. The indented outline comprises only one
	 * tree with the selected element as the root element. Vertices that are not
	 * included in the supplied list of {@link KnowledgeElementWithImpact} are
	 * removed.
	 *
	 * @param filterSettings
	 *            For example, the {@link FilterSettings} cover the selected element
	 *            and the knowledge types to be shown. The selected element cannot
	 *            be null.
	 * @param impactedElements
	 *            A list of {@link KnowledgeElementWithImpact}.
	 */
	public TreeViewer(FilterSettings filterSettings, List<KnowledgeElementWithImpact> impactedElements) {
		this();
		if (filterSettings == null) {
			return;
		}
		LOGGER.info(filterSettings.toString());

		this.filterSettings = filterSettings;

		FilteringManager filteringManager = new FilteringManager(filterSettings);
		graph = filteringManager.getFilteredGraph(impactedElements);

		KnowledgeElement rootElement = filterSettings.getSelectedElement();

		if (rootElement != null) {
			// only one tree is shown
			TreeViewerNode rootNode = getTreeViewerNodeWithChildren(rootElement);
			nodes.add(rootNode);
			return;
		}
	}

	/**
	 * @issue How can graph iteration be done over both outgoing and incoming edges
	 *        of a knowledge element (=node)?
	 * @decision Convert the directed graph into an undirected graph for graph
	 *           iteration!
	 * 
	 * @issue How to handle cycles in the graph?
	 * @decision Traverse all edges in the graph and duplicate knowledge elements to
	 *           resolve the cycle! Store edge traversed by the breadth first
	 *           iterator and traverse the remaining edges afterwards!
	 * @alternative Only use breadth first iteration to traverse the graph!
	 * @con The breadth first iterator does not traverse all edges but every node
	 *      once. Criteria linked to many arguments would only be shown for one
	 *      argument in the tree, which is misleading and information loss.
	 */
	public TreeViewerNode getTreeViewerNodeWithChildren(KnowledgeElement rootElement) {
		if (rootElement == null) {
			return new TreeViewerNode();
		}
		Graph<KnowledgeElement, Link> undirectedGraph = new AsUndirectedGraph<KnowledgeElement, Link>(graph);

		Map<KnowledgeElement, TreeViewerNode> elementToTreeViewerNodeMap = new HashMap<>();

		TreeViewerNode rootNode = new TreeViewerNode(rootElement, filterSettings);
		rootNode = this.makeIdUnique(rootNode);

		elementToTreeViewerNodeMap.put(rootElement, rootNode);

		BreadthFirstIterator<KnowledgeElement, Link> iterator = new BreadthFirstIterator<>(undirectedGraph,
				rootElement);
		Set<Link> traversedEdges = new HashSet<>();

		while (iterator.hasNext()) {
			KnowledgeElement childElement = iterator.next();
			KnowledgeElement parentElement = iterator.getParent(childElement);
			if (parentElement == null) {
				continue;
			}
			Link edge = undirectedGraph.getEdge(childElement, parentElement);
			TreeViewerNode childNode = makeIdUnique(new TreeViewerNode(childElement, edge, filterSettings));
			childNode = this.makeIdUnique(childNode);
			elementToTreeViewerNodeMap.put(childElement, childNode);

			TreeViewerNode parentNode = elementToTreeViewerNodeMap.get(parentElement);
			parentNode.getChildren().add(childNode);

			traversedEdges.add(edge);
		}

		Set<Link> remainingEdges = undirectedGraph.edgeSet().stream().filter(edge -> !traversedEdges.contains(edge))
				.collect(Collectors.toSet());

		for (Link edge : remainingEdges) {
			KnowledgeElement sourceElement = edge.getSource();
			KnowledgeElement targetElement = edge.getTarget();

			KnowledgeElement childElement = sourceElement;
			TreeViewerNode parentNode = elementToTreeViewerNodeMap.get(targetElement);
			if (parentNode == null) {
				parentNode = elementToTreeViewerNodeMap.get(sourceElement);
				childElement = targetElement;
			}
			if (parentNode == null) {
				continue;
			}

			TreeViewerNode childNode = makeIdUnique(new TreeViewerNode(childElement, edge, filterSettings));
			elementToTreeViewerNodeMap.put(childElement, childNode);
			parentNode.getChildren().add(childNode);
		}

		return rootNode;
	}

	protected TreeViewerNode makeIdUnique(TreeViewerNode node) {
		if (!ids.contains(node.getId())) {
			ids.add(node.getId());
		} else {
			node.setId(index + node.getId());
			index++;
		}
		return node;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public boolean isCheckCallback() {
		return checkCallback;
	}

	public Map<String, Boolean> getThemes() {
		return themes;
	}

	public Set<TreeViewerNode> getNodes() {
		return nodes;
	}

	public void setData(Set<TreeViewerNode> nodes) {
		this.nodes = nodes;
	}
}
