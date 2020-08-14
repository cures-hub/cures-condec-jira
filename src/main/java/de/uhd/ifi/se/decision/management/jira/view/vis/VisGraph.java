package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jgrapht.graph.AsSubgraph;
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

	@XmlElement
	private String rootElementKey;

	@JsonIgnore
	private AsSubgraph<KnowledgeElement, Link> graph;
	@JsonIgnore
	private int cid = 0;

	public VisGraph() {
		this.nodes = new HashSet<VisNode>();
		this.edges = new HashSet<VisEdge>();
		this.rootElementKey = "";
	}

	public VisGraph(ApplicationUser user, FilterSettings filterSettings) {
		this();
		if (user == null || filterSettings == null || filterSettings.getProjectKey() == null) {
			return;
		}
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		graph = filteringManager.getSubgraphMatchingFilterSettings();
		if (graph == null || graph.vertexSet().isEmpty()) {
			return;
		}
		KnowledgeElement rootElement = filterSettings.getSelectedElement();
		if (rootElement == null || rootElement.getKey() == null) {
			addNodesAndEdges(null);
			return;
		}

		// TODO This is not a key but id_documentationLocation
		this.rootElementKey = rootElement.getId() + "_" + rootElement.getDocumentationLocationAsString();
		addNodesAndEdges(rootElement);
	}

	private void addNodesAndEdges(KnowledgeElement startElement) {
		if (startElement != null) {
			graph.addVertex(startElement);
		}

		BreadthFirstIterator<KnowledgeElement, Link> iterator = new BreadthFirstIterator<KnowledgeElement, Link>(graph,
				startElement);

		while (iterator.hasNext()) {
			KnowledgeElement element = iterator.next();
			nodes.add(new VisNode(element, false, 50 + iterator.getDepth(element), cid));
			cid++;

			for (Link link : graph.edgesOf(element)) {
				if (containsEdge(link)) {
					continue;
				}
				edges.add(new VisEdge(link));
			}
		}
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

	public Set<VisNode> getNodes() {
		return nodes;
	}

	public Set<VisEdge> getEdges() {
		return edges;
	}

	public AsSubgraph<KnowledgeElement, Link> getGraph() {
		return graph;
	}

	public String getRootElementKey() {
		return rootElementKey;
	}
}
