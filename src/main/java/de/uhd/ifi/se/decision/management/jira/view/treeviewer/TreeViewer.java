package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

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
public class TreeViewer {

	@XmlElement
	private boolean multiple;

	@XmlElement(name = "check_callback")
	private boolean checkCallback;

	@XmlElement
	private Map<String, Boolean> themes;

	@XmlElement
	protected Set<Data> data;

	protected KnowledgeGraph graph;
	private List<String> ids;
	private long index;

	private List<Long> alreadyVisitedEdges;

	private FilteringManager filteringManager;

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
		List<KnowledgeElement> elements = graph.getElements(rootElementType);

		Set<Data> dataSet = new HashSet<Data>();
		for (KnowledgeElement element : elements) {
			dataSet.add(this.makeIdUnique(new Data(element)));
		}

		this.data = dataSet;
	}

	/**
	 * Constructor for a jstree tree viewer for a single knowledge element as the
	 * root element. The tree viewer comprises only one tree.
	 *
	 * @param jiraIssueKey
	 *            the issue id
	 * @param filterSettings
	 *            {@link FilterSettings} object. The filter settings cover the
	 *            decision knowledge types to be shown.
	 */
	public TreeViewer(FilterSettings filterSettings) {
		this();
		if (filterSettings == null || filterSettings.getSelectedElement() == null
				|| filterSettings.getSelectedElement().getKey() == null) {
			return;
		}
		KnowledgeElement rootElement = filterSettings.getSelectedElement();
		graph = KnowledgeGraph.getOrCreate(rootElement.getProject().getProjectKey());

		Data rootNode = this.getDataStructure(rootElement);

		// Match irrelevant sentences back to list
		for (Link link : GenericLinkManager.getLinksForElement(rootElement.getId(), DocumentationLocation.JIRAISSUE)) {
			KnowledgeElement opposite = link.getOppositeElement(rootElement.getId());
			if (opposite instanceof PartOfJiraIssueText && isSentenceShown(opposite)) {
				rootNode.getChildren().add(new Data(opposite));
			}
		}

		filteringManager = new FilteringManager(filterSettings);
		data = new HashSet<Data>(Arrays.asList(rootNode));
		for (Data node : this.data) {
			Iterator<Data> iterator = node.getChildren().iterator();
			while (iterator.hasNext()) {
				removeChildrenWithType(iterator);
			}
		}
	}

	/**
	 * Constructor for a jstree tree viewer for a code class as the root element.
	 * The tree viewer comprises only one tree.
	 */
	public TreeViewer(String projectKey) {
		this();
		if (projectKey != null) {
			Set<Data> dataSet = new HashSet<Data>();
			CodeClassPersistenceManager manager = new CodeClassPersistenceManager(projectKey);
			List<KnowledgeElement> elementList = manager.getKnowledgeElements();
			for (KnowledgeElement element : elementList) {
				dataSet.add(this.makeIdUnique(new Data(element)));
			}
			this.data = dataSet;
		}
	}

	private void removeChildrenWithType(Iterator<Data> iterator) {
		Data currentNode = iterator.next();
		if (!filteringManager.isElementMatchingKnowledgeTypeFilter((KnowledgeElement) currentNode.getElement())) {
			iterator.remove();
			return;
		} else {
			Iterator<Data> it = currentNode.getChildren().iterator();
			while (it.hasNext()) {
				removeChildrenWithType(it);
			}
		}
	}

	private boolean isSentenceShown(KnowledgeElement element) {
		return !((PartOfJiraIssueText) element).isRelevant()
				&& ((PartOfJiraIssueText) element).getDescription().length() > 0;
	}

	public Data getDataStructure(KnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement == null) {
			return new Data();
		}
		if (graph == null) {
			graph = KnowledgeGraph.getOrCreate(decisionKnowledgeElement.getKey());
		}
		Data data = new Data(decisionKnowledgeElement);
		data = this.makeIdUnique(data);
		List<Data> children = this.getChildren(decisionKnowledgeElement);
		data.setChildren(children);
		return data;
	}

	protected List<Data> getChildren(KnowledgeElement decisionKnowledgeElement) {
		List<Data> children = new ArrayList<Data>();
		BreadthFirstIterator<KnowledgeElement, Link> iterator;
		try {
			iterator = new BreadthFirstIterator<>(graph, decisionKnowledgeElement);
		} catch (IllegalArgumentException e) {
			graph.addVertex(decisionKnowledgeElement);
			iterator = new BreadthFirstIterator<>(graph, decisionKnowledgeElement);
		}
		while (iterator.hasNext()) {
			KnowledgeElement iterNode = iterator.next();
			KnowledgeElement parentNode = iterator.getParent(iterNode);
			if (parentNode == null || parentNode.getId() != decisionKnowledgeElement.getId()) {
				continue;
			}
			if (!(iterNode instanceof KnowledgeElement)) {
				continue;
			}
			KnowledgeElement nodeElement = iterNode;
			Link edge = this.graph.getEdge(parentNode, nodeElement);
			if (this.alreadyVisitedEdges.contains(edge.getId())) {
				continue;
			}
			this.alreadyVisitedEdges.add(edge.getId());
			Data dataChild = new Data(nodeElement, edge);
			if (((KnowledgeElement) dataChild.getElement()).getProject() == null
					|| !((KnowledgeElement) dataChild.getElement()).getProject().getProjectKey()
							.equals(decisionKnowledgeElement.getProject().getProjectKey())) {
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
