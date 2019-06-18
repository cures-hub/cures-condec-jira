package de.uhd.ifi.se.decision.management.jira.view.vis;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.filtering.GraphFiltering;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;


@XmlRootElement(name = "vis")
@XmlAccessorType(XmlAccessType.FIELD)
public class Vis {
	@XmlElement
	private HashSet<VisNode> nodes;
	@XmlElement
	private HashSet<VisEdge> edges;
	@XmlElement
	private String rootElementKey;

	@JsonIgnore
	private Graph graph;
	@JsonIgnore
	private boolean isHyperlinked;
	@JsonIgnore
	private List<DecisionKnowledgeElement> elementsAlreadyAsNode;
	@JsonIgnore
	private List<DecisionKnowledgeElement> elementsMatchingFilterCriteria;
	@JsonIgnore
	private int level;
	@JsonIgnore
	private String documentationLocation;
	@JsonIgnore
	private int cid;

	public Vis(){
	}

	public Vis(String projectKey, String elementKey){
		this.graph = new GraphImpl(projectKey, elementKey);
		this.elementsAlreadyAsNode = new ArrayList<>();
		DecisionKnowledgeElement rootElement = this.graph.getRootElement();
		this.rootElementKey = (rootElement.getId()+ "_" + rootElement.getDocumentationLocationAsString());
		nodes = new HashSet<>();
		edges= new HashSet<>();
		elementsAlreadyAsNode = new ArrayList<>();
		elementsMatchingFilterCriteria = graph.getAllElements();
		level = 50;
		cid = 0;
		for (DocumentationLocation location : DocumentationLocation.values()) {
			this.documentationLocation = this.documentationLocation + DocumentationLocation.getName(location);
		}
		fillNodesAndEdges(rootElement,null,0, 0);
	}

	public Vis(String projectKey, String elementKey, boolean isHyperlinked, String query, ApplicationUser user){
		this.graph = new GraphImpl(projectKey, elementKey);
		GraphFiltering filter;
		for (DocumentationLocation location : DocumentationLocation.values()) {
			this.documentationLocation = this.documentationLocation + DocumentationLocation.getName(location);
		}
		if ((query.matches("\\?jql=(.)+")) || (query.matches("\\?filter=(.)+"))) {
			filter = new GraphFiltering(projectKey, query, user, false);
			filter.produceResultsFromQuery();
			this.elementsMatchingFilterCriteria = filter.getAllElementsMatchingQuery();
			//this.graph = new GraphImplFiltered(projectKey, elementKey, filter);
		} else {
			//set filter to all Issues
			filter = new GraphFiltering(projectKey,"asdf§filter=-4",user,false);
			filter.produceResultsFromQuery();
			this.elementsMatchingFilterCriteria = filter.getAllElementsMatchingQuery();
		}
		this.setHyperlinked(isHyperlinked);
		DecisionKnowledgeElement rootElement = this.graph.getRootElement();
		this.rootElementKey = (rootElement.getId()+ "_" + rootElement.getDocumentationLocationAsString());
		nodes = new HashSet<>();
		edges= new HashSet<>();
		elementsAlreadyAsNode = new ArrayList<>();
		level = 50;
		cid = 0;
		fillNodesAndEdges(rootElement, null, level, cid);
	}

	public Vis(String projectKey, String elementKey, boolean isHyperlinked, String searchTerm, ApplicationUser user,
			   String issueTypes, long createdEarliest, long createdLatest, String documentationLocation) {
		this.graph = new GraphImpl(projectKey,elementKey);
		GraphFiltering filter;
		filter = new GraphFiltering(projectKey, searchTerm, user, false);
		filter.produceResultsWithAdditionalFilters(issueTypes,createdEarliest,createdLatest);
		this.elementsMatchingFilterCriteria = filter.getAllElementsMatchingQuery();
		DecisionKnowledgeElement rootElement = this.graph.getRootElement();
		this.isHyperlinked = isHyperlinked;
		this.rootElementKey = (rootElement.getId()+ "_" + rootElement.getDocumentationLocationAsString());
		this.documentationLocation = documentationLocation;
		nodes = new HashSet<>();
		edges= new HashSet<>();
		elementsAlreadyAsNode = new ArrayList<>();
		level = 50;
		cid= 0;
		fillNodesAndEdges(rootElement, null, level, cid);
	}


	//TODO Fix complexity of this function
	// Way to big needs to be split in small functions
	private void fillNodesAndEdges(DecisionKnowledgeElement element, Link link, int level, int cid) {
		if (element == null || element.getProject() == null) {
			return;
		}
		if (graph == null) {
			graph = new GraphImpl(element);
		}

		if (link != null) {
			switch (link.getType()) {
				case "support":
					if (element.getId() == link.getSourceElement().getId()) {
						if (!(this.elementsAlreadyAsNode.contains(element))) {
							this.elementsAlreadyAsNode.add(element);
							this.nodes.add(new VisNode(element, "pro", isCollapsed(element),level+2, cid));
						}
					} else {
						if (!(this.elementsAlreadyAsNode.contains(element))) {
							this.elementsAlreadyAsNode.add(element);
							this.nodes.add(new VisNode(element, isCollapsed(element), level, cid));
						}
					}
					break;
				case "attack":
					if (element.getId() == link.getSourceElement().getId()) {
						if (!(this.elementsAlreadyAsNode.contains(element))) {
							this.elementsAlreadyAsNode.add(element);
							this.nodes.add(new VisNode(element, "con", isCollapsed(element),level+2, cid));
						}
					}
					else {
						if (!(this.elementsAlreadyAsNode.contains(element))) {
							this.elementsAlreadyAsNode.add(element);
							this.nodes.add(new VisNode(element, isCollapsed(element), level, cid));
						}
					}
					break;
				default:
					if (!(this.elementsAlreadyAsNode.contains(element))) {
						this.elementsAlreadyAsNode.add(element);
						this.nodes.add(new VisNode(element, isCollapsed(element),level, cid));
					}
					break;
			}
			this.edges.add(new VisEdge(link));

		} else {
			if (!(this.elementsAlreadyAsNode.contains(element))) {
				this.elementsAlreadyAsNode.add(element);
				this.nodes.add(new VisNode(element, isCollapsed(element),level, cid));
			}
		}
		Map<DecisionKnowledgeElement, Link> childrenAndLinks = graph.getAdjacentElementsAndLinks(element);
		for (Map.Entry<DecisionKnowledgeElement, Link> childAndLink : childrenAndLinks.entrySet()) {
			if (childAndLink.getKey() != null) {
				if (childAndLink.getValue().getSourceElement().equals(element)) {
					this.level = level + 1;
				} else {
					this.level = level - 1;
				}
				this.cid = cid+1;
				fillNodesAndEdges(childAndLink.getKey(), childAndLink.getValue(), this.level, this.cid);
			}
		}
	}

	private boolean isCollapsed(DecisionKnowledgeElement element) {
		if (this.documentationLocation.contains(DocumentationLocation.getName(element.getDocumentationLocation()))
			&& this.elementsMatchingFilterCriteria.contains(element)) {
			return true;
		}
		return false;
	}

	public void setNodes(HashSet<VisNode> nodes) {
		this.nodes = nodes;
	}

	public void setEdges(HashSet<VisEdge> edges) {
		this.edges = edges;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;

	}

	public void setHyperlinked(boolean hyperlinked) {
		isHyperlinked = hyperlinked;
	}

	public HashSet<VisNode> getNodes() {
		return nodes;
	}

	public HashSet<VisEdge> getEdges() {
		return edges;
	}

	public Graph getGraph() {
		return graph;
	}

	public boolean isHyperlinked() {
		return isHyperlinked;
	}

	public String getRootElementKey() {
		return rootElementKey;
	}

	public void setRootElementKey(String rootElementKey) {
		this.rootElementKey = rootElementKey;
	}
}
