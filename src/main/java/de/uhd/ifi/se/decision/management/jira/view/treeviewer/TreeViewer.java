package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;

/**
 * Creates tree viewer content. The tree viewer is rendered with the jstree
 * library.
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

	public TreeViewer() {
		this.multiple = false;
		this.checkCallback = true;
		this.themes = ImmutableMap.of("icons", true);
		this.ids = new ArrayList<String>();
	}

	/**
	 * Constructor for a jstree tree viewer that matches the {@link FilterSettings}.
	 * If a knowledge element is selected in the {@link FilterSettings}, the tree
	 * viewer comprises only one tree with the selected element as the root element.
	 * If no element is selected, the tree viewer contains a list of trees.
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
		nodes = new HashSet<TreeViewerNode>();

		FilteringManager filteringManager = new FilteringManager(null, filterSettings);
		graph = filteringManager.getSubgraphMatchingFilterSettings();

		KnowledgeElement rootElement = filterSettings.getSelectedElement();

		if (rootElement != null) {
			if (rootElement.getKey() == null) {
				return;
			}
			TreeViewerNode rootNode = getTreeViewerNodeWithChildren(rootElement);

			nodes.add(rootNode);
			return;
		}

		List<KnowledgeElement> knowledgeElements = new ArrayList<>();

		if (graph.vertexSet().isEmpty()) {
			return;
		}

		// TODO Improve code class handling and remove this case handling
		if ("codeClass".equals(filterSettings.getKnowledgeTypes().iterator().next())) {
			CodeClassPersistenceManager manager = new CodeClassPersistenceManager(filterSettings.getProjectKey());
			knowledgeElements = manager.getKnowledgeElements();
		} else {
			knowledgeElements.addAll(graph.vertexSet());
		}

		for (KnowledgeElement element : knowledgeElements) {
			nodes.add(makeIdUnique(new TreeViewerNode(element)));
		}
	}

	/**
	 * @issue How can graph iteration be done over both outgoing and incoming edges
	 *        of a knowledge element (=node)?
	 * @decision Convert the directed graph into an undirected graph for graph
	 *           iteration!
	 */
	public TreeViewerNode getTreeViewerNodeWithChildren(KnowledgeElement rootElement) {
		if (rootElement == null) {
			return new TreeViewerNode();
		}
		Graph<KnowledgeElement, Link> undirectedGraph = new AsUndirectedGraph<KnowledgeElement, Link>(graph);

		Map<KnowledgeElement, TreeViewerNode> elementToTreeViewerNodeMap = new HashMap<>();

		TreeViewerNode rootNode = new TreeViewerNode(rootElement);
		rootNode = this.makeIdUnique(rootNode);

		elementToTreeViewerNodeMap.put(rootElement, rootNode);

		BreadthFirstIterator<KnowledgeElement, Link> iterator = new BreadthFirstIterator<>(undirectedGraph,
				rootElement);

		while (iterator.hasNext()) {
			KnowledgeElement childElement = iterator.next();
			KnowledgeElement parentElement = iterator.getParent(childElement);
			if (parentElement == null) {
				continue;
			}
			Link edge = undirectedGraph.getEdge(childElement, parentElement);
			TreeViewerNode childNode = new TreeViewerNode(childElement, edge);
			childNode = this.makeIdUnique(childNode);
			elementToTreeViewerNodeMap.put(childElement, childNode);

			TreeViewerNode parentNode = elementToTreeViewerNodeMap.get(parentElement);
			if (parentNode == null) {
				continue;
			}

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
