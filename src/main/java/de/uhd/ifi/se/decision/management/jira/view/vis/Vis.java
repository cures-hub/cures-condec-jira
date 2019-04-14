package de.uhd.ifi.se.decision.management.jira.view.vis;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImplFiltered;
import de.uhd.ifi.se.decision.management.jira.filtering.GraphFiltering;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import org.json.JSONPropertyIgnore;

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

	private Graph graph;
	private boolean isHyperlinked;
	private List<DecisionKnowledgeElement> elementsAlreadyAsNode;
	private long startDate;
	private long endDate;
	private List<String> issueTypeNames;

	public Vis(){
	}

	public Vis(String projectKey, String elementKey, boolean isHyperlinked){
		this.graph = new GraphImpl(projectKey, elementKey);
		this.setHyperlinked(isHyperlinked);
		DecisionKnowledgeElement rootElement = this.graph.getRootElement();
		nodes = new HashSet<>();
		edges= new HashSet<>();
		this.elementsAlreadyAsNode = new ArrayList<>();
		startDate = -1;
		endDate = -1;
		issueTypeNames = getNamesOfExistingIssueTypes();
		fillNodesAndEdges(rootElement,null);
	}

	public Vis(String projectKey, String elementKey, boolean isHyperlinked, String query, ApplicationUser user){
		GraphFiltering filter;
		if ((query.matches("\\?jql=(.)+")) || (query.matches("\\?filter=(.)+"))) {
			filter = new GraphFiltering(projectKey, query, user);
			filter.produceResultsFromQuery();
			startDate = filter.getStartDate();
			endDate = filter.getEndDate();
			this.graph = new GraphImplFiltered(projectKey, elementKey, filter);
		} else {

		}
		this.setHyperlinked(isHyperlinked);
		DecisionKnowledgeElement rootElement = this.graph.getRootElement();
		nodes = new HashSet<>();
		edges= new HashSet<>();
		elementsAlreadyAsNode = new ArrayList<>();
		fillNodesAndEdges(rootElement, null);
	}



	private void fillNodesAndEdges(DecisionKnowledgeElement element, Link link) {
		if (element == null || element.getProject() == null) {
			return;
		}
		if (graph == null) {
			graph = new GraphImpl(element);
		}
		Map<DecisionKnowledgeElement, Link> childrenAndLinks = graph.getAdjacentElementsAndLinks(element);
		if (link != null) {
			switch (link.getType()) {
				case "support":
					if (element.getId() == link.getSourceElement().getId()) {
						if (!(this.elementsAlreadyAsNode.contains(element))) {
							this.elementsAlreadyAsNode.add(element);
							this.nodes.add(new VisNode(element, "Pro"));
						}
					}
					break;
				case "attack":
					if (element.getId() == link.getSourceElement().getId()) {
						if (!(this.elementsAlreadyAsNode.contains(element))) {
							this.elementsAlreadyAsNode.add(element);
							this.nodes.add(new VisNode(element, "Con"));
						}
					}
					break;
				default:
					if (!(this.elementsAlreadyAsNode.contains(element))) {
						this.elementsAlreadyAsNode.add(element);
						this.nodes.add(new VisNode(element));
					}
					break;
			}

		} else {
			if (!(this.elementsAlreadyAsNode.contains(element))) {
				this.elementsAlreadyAsNode.add(element);
				this.nodes.add(new VisNode(element));
			}
		}
		for (Map.Entry<DecisionKnowledgeElement, Link> childAndLink : childrenAndLinks.entrySet()) {
			fillNodesAndEdges(childAndLink.getKey(),childAndLink.getValue());
			this.edges.add(new VisEdge(childAndLink.getValue()));
		}
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

	public List<String> getNamesOfExistingIssueTypes() {
		List<String> existingIssueTypeNames = new ArrayList<String>();
		ConstantsManager constantsManager = ComponentAccessor.getConstantsManager();
		Collection<IssueType> issueTypes = constantsManager.getAllIssueTypeObjects();
		for (IssueType issueType : issueTypes) {
			existingIssueTypeNames.add(issueType.getName());
		}
		return existingIssueTypeNames;
	}
}
