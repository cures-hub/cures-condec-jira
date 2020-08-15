package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import java.util.ArrayList;
import java.util.Arrays;
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
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;

/**
 * Creates tree viewer content. The tree viewer is rendered with the jstree
 * library.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TreeViewer {

	@XmlElement
	private boolean multiple;

	@XmlElement(name = "check_callback")
	private boolean checkCallback;

	@XmlElement
	private Map<String, Boolean> themes;

	@XmlElement
	private Set<Data> data;

	@JsonIgnore
	private Graph<KnowledgeElement, Link> graph;
	@JsonIgnore
	private List<String> ids;
	@JsonIgnore
	private long index;
	@JsonIgnore
	private List<Long> alreadyVisitedEdges;

	public TreeViewer() {
		this.multiple = false;
		this.checkCallback = true;
		this.themes = ImmutableMap.of("icons", true);

		this.alreadyVisitedEdges = new ArrayList<Long>();
		this.ids = new ArrayList<String>();
	}

	/**
	 * Constructor for a jstree tree viewer for a list of trees where all root
	 * elements have a specific {@link KnowledgeType}.
	 *
	 * @param projectKey
	 *            of a Jira project.
	 * @param rootElementType
	 *            {@link KnowledgeType} of the root elements.
	 */
	public TreeViewer(String projectKey, KnowledgeType rootElementType) {
		this();
		if (rootElementType == KnowledgeType.OTHER) {
			return;
		}
		graph = KnowledgeGraph.getOrCreate(projectKey);
		List<KnowledgeElement> elements = ((KnowledgeGraph) graph).getElements(rootElementType);

		data = new HashSet<Data>();
		for (KnowledgeElement element : elements) {
			data.add(this.makeIdUnique(new Data(element)));
		}
	}

	/**
	 * Constructor for a jstree tree viewer for a single knowledge element as the
	 * root element. The tree viewer comprises only one tree.
	 *
	 * @param filterSettings
	 *            For example, the {@link FilterSettings}s cover the selected
	 *            element and the decision knowledge types to be shown.
	 */
	public TreeViewer(FilterSettings filterSettings) {
		this();
		if (filterSettings == null || filterSettings.getSelectedElement() == null
				|| filterSettings.getSelectedElement().getKey() == null) {
			return;
		}
		KnowledgeElement rootElement = filterSettings.getSelectedElement();
		FilteringManager filteringManager = new FilteringManager(null, filterSettings);
		graph = filteringManager.getSubgraphMatchingFilterSettings();
		Data rootNode = getDataStructure(rootElement);

		// Match irrelevant sentences back to list
		for (Link link : GenericLinkManager.getLinksForElement(rootElement.getId(), DocumentationLocation.JIRAISSUE)) {
			KnowledgeElement opposite = link.getOppositeElement(rootElement.getId());
			if (opposite instanceof PartOfJiraIssueText && isSentenceShown(opposite)) {
				rootNode.getChildren().add(new Data(opposite));
			}
		}

		data = new HashSet<Data>(Arrays.asList(rootNode));
	}

	/**
	 * Constructor for a jstree tree viewer for a code class as the root element.
	 * The tree viewer comprises only one tree.
	 */
	public TreeViewer(String projectKey) {
		this();
		if (projectKey != null) {
			data = new HashSet<Data>();
			CodeClassPersistenceManager manager = new CodeClassPersistenceManager(projectKey);
			List<KnowledgeElement> elementList = manager.getKnowledgeElements();
			for (KnowledgeElement element : elementList) {
				data.add(this.makeIdUnique(new Data(element)));
			}
		}
	}

	// TODO Add this to FilterSettings and FilteringManager
	private boolean isSentenceShown(KnowledgeElement element) {
		return !((PartOfJiraIssueText) element).isRelevant()
				&& ((PartOfJiraIssueText) element).getDescription().length() > 0;
	}

	/**
	 * @issue How can graph iteration be done over both outgoing and incoming edges
	 *        of a knowledge element (=node)?
	 * @decision Convert the directed graph into an undirected graph for graph
	 *           iteration!
	 */
	public Data getDataStructure(KnowledgeElement knowledgeElement) {
		if (knowledgeElement == null) {
			return new Data();
		}
		graph = new AsUndirectedGraph<KnowledgeElement, Link>(graph);
		Data data = new Data(knowledgeElement);
		data = this.makeIdUnique(data);
		List<Data> children = this.getChildren(knowledgeElement);
		data.setChildren(children);
		return data;
	}

	protected List<Data> getChildren(KnowledgeElement knowledgeElement) {
		List<Data> children = new ArrayList<Data>();
		BreadthFirstIterator<KnowledgeElement, Link> iterator;
		try {
			iterator = new BreadthFirstIterator<>(graph, knowledgeElement);
		} catch (IllegalArgumentException e) {
			graph.addVertex(knowledgeElement);
			iterator = new BreadthFirstIterator<>(graph, knowledgeElement);
		}
		while (iterator.hasNext()) {
			KnowledgeElement iterNode = iterator.next();
			KnowledgeElement parentNode = iterator.getParent(iterNode);
			if (parentNode == null || parentNode.getId() != knowledgeElement.getId()) {
				continue;
			}
			if (!(iterNode instanceof KnowledgeElement)) {
				continue;
			}
			KnowledgeElement nodeElement = iterNode;
			Link edge = graph.getEdge(parentNode, nodeElement);
			if (alreadyVisitedEdges.contains(edge.getId())) {
				continue;
			}
			this.alreadyVisitedEdges.add(edge.getId());
			Data dataChild = new Data(nodeElement, edge);
			if (((KnowledgeElement) dataChild.getElement()).getProject() == null
					|| !((KnowledgeElement) dataChild.getElement()).getProject().getProjectKey()
							.equals(knowledgeElement.getProject().getProjectKey())) {
				continue;
			}
			dataChild = this.makeIdUnique(dataChild);
			List<Data> childrenOfElement = this.getChildren(nodeElement);
			dataChild.setChildren(childrenOfElement);
			children.add(dataChild);
		}
		return children;
	}

	protected Data makeIdUnique(Data data) {
		if (!ids.contains(data.getId())) {
			ids.add(data.getId());
		} else {
			data.setId(index + data.getId());
			index++;
		}
		return data;
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

	public Set<Data> getData() {
		return data;
	}

	public void setData(Set<Data> data) {
		this.data = data;
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}
}
