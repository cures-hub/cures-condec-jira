package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CodeFileExtractorAndMaintainer;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

/**
 * Creates the knowledge graph for the entire project. Nodes of the knowledge
 * graph are decision knowledge elements, Jira issues such as requirements and
 * work items, and code files. Extends the JGraphT graph interface. The
 * knowledge graph can be disconnected.
 * 
 * @see GitClient
 * @see Graph
 */
public class KnowledgeGraph extends DirectedWeightedMultigraph<KnowledgeElement, Link> {

	private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeGraph.class);

	private static final long serialVersionUID = 1L;
	protected List<Long> linkIds;
	private KnowledgePersistenceManager persistenceManager;

	// for elements that do not exist in database
	private long id = -1;
	private long linkId = -1;

	/**
	 * Instances of knowledge graphs that are identified by the project key.
	 */
	public static Map<String, KnowledgeGraph> instances = new HashMap<String, KnowledgeGraph>();

	/**
	 * Retrieves an existing {@link KnowledgeGraph} instance or creates a new
	 * instance if there is no instance for the given project key.
	 * 
	 * @param projectKey
	 *            of the Jira project.
	 * @return either a new or already existing {@link KnowledgeGraph} instance.
	 */
	public static KnowledgeGraph getOrCreate(String projectKey) {
		if (projectKey == null || projectKey.isBlank()) {
			return null;
		}
		if (instances.containsKey(projectKey)) {
			return instances.get(projectKey);
		}
		KnowledgeGraph knowledgeGraph = new KnowledgeGraph(projectKey);
		instances.put(projectKey, knowledgeGraph);
		return knowledgeGraph;
	}

	public static KnowledgeGraph getOrCreate(DecisionKnowledgeProject project) {
		if (project == null) {
			return null;
		}
		return getOrCreate(project.getProjectKey());
	}

	public KnowledgeGraph() {
		super(Link.class);
		this.linkIds = new ArrayList<Long>();
	}

	public KnowledgeGraph(String projectKey) {
		this();
		this.persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);
		createGraph();
		new CodeFileExtractorAndMaintainer(projectKey).extractAllChangedFiles();
	}

	private void createGraph() {
		addElements();
		addEdges();
	}

	private void addElements() {
		List<KnowledgeElement> elements = persistenceManager.getKnowledgeElements();
		for (KnowledgeElement element : elements) {
			addVertex(element);
		}
	}

	private void addEdges() {
		for (KnowledgeElement element : this.vertexSet()) {
			List<Link> links = persistenceManager.getLinks(element);
			for (Link link : links) {
				KnowledgeElement destination = link.getTarget();
				KnowledgeElement source = link.getSource();
				if (destination == null || source == null) {
					continue;
				}
				if (destination.equals(source)) {
					continue;
				}
				if (!linkIds.contains(link.getId()) && this.containsVertex(link.getTarget())
						&& this.containsVertex(link.getSource())) {
					this.addEdge(link);
					linkIds.add(link.getId());
				}
			}
		}
	}

	/**
	 * Adds the specified edge ({@link Link} object) to this graph, going from the
	 * source vertex to the target vertex. More formally, adds the specified edge,
	 * <code>e</code>, to this graph if this graph contains no edge <code>e2</code>
	 * such that <code>e2.equals(e)</code>. If this graph already contains such an
	 * edge, the call leaves this graph unchanged and returns <tt>false</tt>. If the
	 * edge was added to the graph, returns <code>true</code>.
	 *
	 * @param link
	 *            edge to be added to this graph as a {@link Link} object.
	 *
	 * @return <tt>true</tt> if this graph did not already contain the specified
	 *         edge.
	 *
	 * @see #addEdge(Object, Object)
	 * @see #getEdgeSupplier()
	 */
	public boolean addEdge(Link link) {
		boolean isEdgeCreated = false;
		KnowledgeElement source = link.getSource();
		addVertex(source);

		KnowledgeElement destination = link.getTarget();
		addVertex(destination);

		try {
			isEdgeCreated = addEdge(source, destination, link);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Error adding link to the graph: " + e.getMessage());
		}
		return isEdgeCreated;
	}

	public KnowledgeElement addVertexNotBeingInDatabase(KnowledgeElement element) {
		KnowledgeElement existingElement = getElementsNotInDatabaseBySummary(element.getSummary());
		if (existingElement != null) {
			return existingElement;
		}
		--id;
		element.setId(id);
		element.setKey(element.getProject().getProjectKey() + ":graph:" + id);
		super.addVertex(element);
		return element;
	}

	public void addEdgeNotBeingInDatabase(Link link) {
		link.setId(--linkId);
		this.addEdge(link);
	}

	/**
	 * Updates a knowledge element. If the element is not in the graph it will be
	 * added.
	 * 
	 * @param element
	 */
	public boolean updateElement(KnowledgeElement node) {
		KnowledgeElement oldElement = null;
		for (KnowledgeElement currentElement : vertexSet()) {
			if (node.equals(currentElement)) {
				oldElement = currentElement;
			}
		}
		if (oldElement == null) {
			return false;
		}
		return replaceVertex(oldElement, node);
	}

	private boolean replaceVertex(KnowledgeElement vertex, KnowledgeElement replace) {
		Set<Link> newLinks = new HashSet<Link>();
		for (Link edge : outgoingEdgesOf(vertex)) {
			newLinks.add(new Link(replace, edge.getTarget(), edge.getType()));
		}
		for (Link edge : incomingEdgesOf(vertex)) {
			newLinks.add(new Link(edge.getSource(), replace, edge.getType()));
		}
		removeVertex(vertex);
		for (Link link : newLinks) {
			addEdge(link);
		}
		return true;
	}

	/**
	 * Returns <tt>true</tt> if this graph contains the specified edge. More
	 * formally, returns <tt>true</tt> if and only if this graph contains an edge
	 * <code>e2</code> such that <code>e.equals(e2)</code>. If the specified edge is
	 * <code>null</code> returns <code>false</code>.
	 *
	 * @param link
	 *            edge whose presence in this graph is to be tested.
	 *
	 * @return <tt>true</tt> if this graph contains the specified edge.
	 */
	@Override
	public boolean containsEdge(Link link) {
		if (link == null) {
			return false;
		}
		return super.containsEdge(link.getSource(), link.getTarget());
	}

	/**
	 * Removes the specified edge from the graph if it is present. Returns
	 * <tt>true</tt> if the graph contained the specified edge. (The graph will not
	 * contain the specified edge once the call returns).
	 *
	 * @param link
	 *            edge to be removed from this graph, if present.
	 *
	 * @return <code>true</code> if and only if the graph contained the specified
	 *         edge.
	 */
	@Override
	public boolean removeEdge(Link link) {
		Link removedLink = super.removeEdge(link.getSource(), link.getTarget());
		if (removedLink == null) {
			removedLink = super.removeEdge(link.getTarget(), link.getSource());
		}
		return removedLink != null;
	}

	@Override
	public Set<Link> edgesOf(KnowledgeElement element) {
		Set<Link> edges = new HashSet<Link>();
		try {
			edges = super.edgesOf(element);
		} catch (IllegalArgumentException | NullPointerException e) {
			LOGGER.error("Edges for node could not be returned. " + e);
		}
		return edges;
	}

	/**
	 * Returns all unlinked elements of the knowledge element for a project. Sorts
	 * the elements according to their similarity and their likelihood that they
	 * should be linked.
	 * 
	 * TODO Sorting according to the likelihood that they should be linked.
	 * 
	 * @issue How can the sorting be implemented?
	 *
	 * @param element
	 *            {@link KnowledgeElement} with id in database. The id is different
	 *            to the key.
	 * @return set of unlinked elements, sorted by the likelihood that they should
	 *         be linked.
	 */
	public List<KnowledgeElement> getUnlinkedElements(KnowledgeElement element) {
		List<KnowledgeElement> elements = new ArrayList<KnowledgeElement>();
		elements.addAll(this.vertexSet());
		if (element == null) {
			return elements;
		}
		elements.remove(element);

		List<KnowledgeElement> linkedElements = Graphs.neighborListOf(this, element);
		elements.removeAll(linkedElements);

		return elements;
	}

	/**
	 * @return list of all knowledge elements for a project with a certain knowledge
	 *         type.
	 * @see KnowledgeElement
	 * @see DecisionKnowledgeProject
	 * @see KnowledgeType
	 */
	public List<KnowledgeElement> getElements(KnowledgeType type) {
		KnowledgeType simpleType = type.replaceProAndConWithArgument();
		List<KnowledgeElement> elements = new ArrayList<KnowledgeElement>();
		elements.addAll(this.vertexSet());
		Iterator<KnowledgeElement> iterator = elements.iterator();
		while (iterator.hasNext()) {
			KnowledgeElement element = iterator.next();
			if (element.getType().replaceProAndConWithArgument() != simpleType) {
				iterator.remove();
			}
		}
		return elements;
	}

	/**
	 * @param elements
	 *            nodes/verteces of the {@link KnowledgeGraph} that irrelevant
	 *            sentences from Jira issue description and comments are linked to.
	 * @return {@link KnowledgeGraph} with irrelevant sentences from Jira issue
	 *         description and comments as new nodes/verteces.
	 */
	public KnowledgeGraph addIrrelevantSentencesLinkedTo(Set<KnowledgeElement> elements) {
		for (KnowledgeElement element : elements) {
			for (Link link : GenericLinkManager.getLinksForElement(element.getId(), DocumentationLocation.JIRAISSUE)) {
				KnowledgeElement opposite = link.getOppositeElement(element);
				if (opposite instanceof PartOfJiraIssueText && isSentenceShown(opposite)) {
					this.addEdge(link);
				}
			}
		}
		return this;
	}

	private static boolean isSentenceShown(KnowledgeElement element) {
		return !((PartOfJiraIssueText) element).isRelevant()
				&& ((PartOfJiraIssueText) element).getDescription().length() > 0;
	}

	/**
	 * @return copy of the {@link KnowledgeGraph} instance.
	 */
	public static KnowledgeGraph copy(Graph<KnowledgeElement, Link> graphToBeCopied) {
		KnowledgeGraph copiedGraph = new KnowledgeGraph();
		graphToBeCopied.vertexSet().forEach(vertex -> copiedGraph.addVertex(vertex));
		graphToBeCopied.edgeSet().forEach(link -> copiedGraph.addEdge(link.getSource(), link.getTarget(), link));
		return copiedGraph;
	}

	public KnowledgeElement getElement(String elementKey) {
		Iterator<KnowledgeElement> iterator = vertexSet().iterator();
		while (iterator.hasNext()) {
			KnowledgeElement element = iterator.next();
			if (element.getKey().equals(elementKey)) {
				return element;
			}
		}
		return null;
	}

	public KnowledgeElement getElementsNotInDatabaseBySummary(String summary) {
		Iterator<KnowledgeElement> iterator = vertexSet().iterator();
		while (iterator.hasNext()) {
			KnowledgeElement element = iterator.next();
			if (element.getId() > 0) {
				continue;
			}
			if (element.getSummary().equals(summary)) {
				return element;
			}
		}
		return null;
	}
}
