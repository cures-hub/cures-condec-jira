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
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;

/**
 * Creates tree viewer content. The tree viewer is rendered with the jstree
 * library.
 * 
 * Iterates over the filtered {@link KnowledgeGraph} provided by the
 * {@link FilteringManager}.
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
	 * If no element is selected, the tree viewer conatains a list of trees.
	 *
	 * @param filterSettings
	 *            For example, the {@link FilterSettings} cover the selected element
	 *            and the knowledge types to be shown.
	 */
	public TreeViewer(FilterSettings filterSettings) {
		this();
		if (filterSettings == null || filterSettings.getSelectedElement() == null
				|| filterSettings.getSelectedElement().getKey() == null) {
			return;
		}
		FilteringManager filteringManager = new FilteringManager(null, filterSettings);
		graph = filteringManager.getSubgraphMatchingFilterSettings();
		KnowledgeElement rootElement = filterSettings.getSelectedElement();
		nodes = new HashSet<TreeViewerNode>();
		if (rootElement == null) {
			for (KnowledgeElement element : graph.vertexSet()) {
				nodes.add(this.makeIdUnique(new TreeViewerNode(element)));
			}
		} else {
			TreeViewerNode rootNode = getTreeViewerNodeWithChildren(rootElement);
			nodes.add(rootNode);
		}

		// Match irrelevant sentences back to list
		/*
		 * for (Link link : GenericLinkManager.getLinksForElement(rootElement.getId(),
		 * DocumentationLocation.JIRAISSUE)) { KnowledgeElement opposite =
		 * link.getOppositeElement(rootElement.getId()); if (opposite instanceof
		 * PartOfJiraIssueText && isSentenceShown(opposite)) {
		 * rootNode.getChildren().add(new TreeViewerNode(opposite)); } }
		 */

	}

	// TODO Add this to FilterSettings and FilteringManager
	private boolean isSentenceShown(KnowledgeElement element) {
		return !((PartOfJiraIssueText) element).isRelevant()
				&& ((PartOfJiraIssueText) element).getDescription().length() > 0;
	}

	/**
	 * Constructor for a jstree tree viewer for a code class as the root element.
	 * The tree viewer comprises only one tree.
	 */
	public TreeViewer(String projectKey) {
		this();
		if (projectKey == null) {
			return;
		}
		nodes = new HashSet<TreeViewerNode>();
		CodeClassPersistenceManager manager = new CodeClassPersistenceManager(projectKey);
		List<KnowledgeElement> codeClasses = manager.getKnowledgeElements();
		for (KnowledgeElement element : codeClasses) {
			nodes.add(this.makeIdUnique(new TreeViewerNode(element)));
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

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public boolean isCheckCallback() {
		return checkCallback;
	}

	public void setCheckCallback(boolean checkCallback) {
		this.checkCallback = checkCallback;
	}

	public Map<String, Boolean> getThemes() {
		return themes;
	}

	public void setThemes(Map<String, Boolean> themes) {
		this.themes = themes;
	}

	public Set<TreeViewerNode> getData() {
		return nodes;
	}

	public void setData(Set<TreeViewerNode> data) {
		this.nodes = data;
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}
}
